package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.FirebaseError
import com.tabka.backblogapp.util.FirebaseExceptionType
import com.tabka.backblogapp.util.NetworkError
import com.tabka.backblogapp.util.NetworkExceptionType
import kotlinx.coroutines.tasks.await

open class SettingsViewModel(
    val auth: FirebaseAuth = Firebase.auth,
    val userRepository: UserRepository = UserRepository(),
    val logLocalRepository: LogLocalRepository = LogLocalRepository(),
    val logRepository: LogRepository = LogRepository()
): ViewModel() {
    private val tag = "SettingsViewModel"

    open suspend fun getUserData(): DataResult<UserData?> {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            return when(val userData = userRepository.getUser(userId)) {
                is DataResult.Success -> {
                    DataResult.Success(userData.item)
                }

                is DataResult.Failure -> {
                    DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                }
            }
        }
        return DataResult.Failure(FirebaseError(FirebaseExceptionType.NOT_FOUND))
    }

    open suspend fun updateUserData(updates: Map<String, Any?>, password: String): DataResult<Boolean> {
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
    }

    open fun getLogCount(): Int {
        return logLocalRepository.getLogCount()
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