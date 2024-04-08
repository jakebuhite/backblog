package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.getErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    val auth: FirebaseAuth = Firebase.auth,
    val logLocalRepository: LogLocalRepository = LogLocalRepository(),
    val logRepository: LogRepository = LogRepository(),
    val userRepository: UserRepository = UserRepository()
): ViewModel() {
    private val tag = "AuthViewModel"

    suspend fun attemptLogin(email: String, password: String): Pair<Boolean, String> {
        var resultMsg = "Something went wrong. Please try again."

        try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
            if (result.user != null) {
                return Pair(true, "")
            }
        } catch (e: Exception) {
            println("Error: $e")
            val errorCode = (e as FirebaseAuthException).errorCode
            print("ERROR CODE: $errorCode")
            resultMsg = getErrorMessage(errorCode)
        }
        return Pair(false, resultMsg)
    }
    suspend fun syncLocalLogsToDB(userId: String): DataResult<Boolean> = coroutineScope {
        try {
            val logs = logLocalRepository.getLogs()
            logs.map { log ->
                async(Dispatchers.IO) {
                    logRepository.addLog(log.name!!, userId, log.owner?.priority!!, log.creationDate!!, log.movieIds!!, log.watchedIds!!)
                }
            }.awaitAll()

            // Now delete logs
            if (logs.isNotEmpty()) {
                logLocalRepository.clearLogs()
            }

            DataResult.Success(true)
        } catch (e: Exception) {
            Log.d(tag, "Error: $e")
            DataResult.Failure(e)
        }
    }

    suspend fun attemptSignup(email: String, username: String, password: String): Pair<Boolean, String> {
        var resultMsg = "Something went wrong. Please try again."

        try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
            if (result.user != null) {
                val userId = result.user!!.uid
                userRepository.addUser(userId, username.trim(), 1)

                if (logLocalRepository.getLogCount() > 0) {
                    syncLocalLogsToDB(userId)
                }

                return Pair(true, "")
            }
        } catch (e: Exception) {
            val errorCode = (e as FirebaseAuthException).errorCode
            resultMsg = getErrorMessage(errorCode)
        }
        return Pair(false, resultMsg)
    }

}