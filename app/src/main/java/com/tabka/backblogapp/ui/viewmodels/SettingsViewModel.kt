package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.NetworkError
import com.tabka.backblogapp.util.NetworkExceptionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

open class SettingsViewModel: ViewModel() {
    private val auth = Firebase.auth
    private val userRepository = UserRepository()
    private val logLocalRepository = LogLocalRepository()
    private val logRepository = LogRepository()
    private val tag = "SettingsViewModel"

    open suspend fun getUserData(): DataResult<UserData?> {
        return try {
            val userId = auth.currentUser!!.uid
            when(val userData = userRepository.getUser(userId)) {
                is DataResult.Success -> {
                    return DataResult.Success(userData.item)
                }
                is DataResult.Failure -> {
                    return DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                }
            }
        } catch (e: Exception) {
            Log.d(tag, "Error: $e")
            DataResult.Failure(e)
        }
    }

    suspend fun updateUserData(updates: Map<String, Any?>, password: String): DataResult<Boolean> {
        return try {
            when (isCorrectPassword(password)) {
                is DataResult.Success -> {
                    val userId = auth.currentUser!!.uid
                    return when(userRepository.updateUser(userId, updates)) {
                        is DataResult.Success -> {
                            DataResult.Success(true)
                        }

                        is DataResult.Failure -> {
                            DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                        }
                    }
                }
                is DataResult.Failure -> {
                    return DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                }
            }
        } catch (e: Exception) {
            Log.d(tag, "Error: $e")
            DataResult.Failure(e)
        }
    }

    open fun getLogCount(): Int {
        return logLocalRepository.getLogCount()
    }

    open suspend fun syncLocalLogsToDB(): DataResult<Boolean> = coroutineScope {
        try {
            val logs = logLocalRepository.getLogs()

            val userId = auth.currentUser!!.uid
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

    private suspend fun isCorrectPassword(password: String): DataResult<Boolean> {
        return try {
            val userEmail = auth.currentUser?.email!!
            auth.currentUser?.reauthenticateAndRetrieveData(EmailAuthProvider.getCredential(userEmail, password))?.await()
            return DataResult.Success(true)
        } catch (e: Exception) {
            Log.d(tag, "Error: $e")
            DataResult.Failure(e)
        }
    }
}