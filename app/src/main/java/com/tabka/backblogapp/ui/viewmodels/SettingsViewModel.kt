package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.NetworkError
import com.tabka.backblogapp.util.NetworkExceptionType

class SettingsViewModel: ViewModel() {
    private val auth = Firebase.auth
    private val userRepository = UserRepository()
    private val tag = "SettingsViewModel"

    suspend fun getUserData(): DataResult<UserData?> {
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

    suspend fun updateUserData(updates: Map<String, Any?>): DataResult<Boolean> {
        return try {
            val userId = auth.currentUser!!.uid
            when(userRepository.updateUser(userId, updates)) {
                is DataResult.Success -> {
                    return DataResult.Success(true)
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
}