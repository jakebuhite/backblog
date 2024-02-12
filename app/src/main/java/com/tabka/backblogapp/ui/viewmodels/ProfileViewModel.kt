package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.repository.FriendRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {
    private val tag = "ProfileViewModel"
    private val auth = Firebase.auth

    // User Info
    private val userRepository = UserRepository()
    val userData: MutableLiveData<UserData> = MutableLiveData()

    // Log Info
    private val logRepository = LogRepository()
    val publicLogData: MutableLiveData<List<LogData>> = MutableLiveData()

    // Friend Info
    private val friendRepository = FriendRepository()
    private val _friendsData = MutableStateFlow<List<UserData>>(emptyList())
    val friendsData = _friendsData.asStateFlow()

    private fun updateUserData(user: UserData) {
        userData.value = user
    }

    private fun updateLogData(logData: List<LogData>) {
        publicLogData.value = logData
    }

    private fun updateFriends(newFriends: List<UserData>) {
        _friendsData.value = newFriends
    }

    suspend fun getUserData(friendId: String) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser?.uid
                if (user != null) {
                    val result: DataResult<UserData> = userRepository.getUser(friendId)
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is DataResult.Success -> updateUserData(result.item)
                            is DataResult.Failure -> throw result.throwable
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
            }
        }
    }

    suspend fun getPublicLogs(friendId: String) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser?.uid
                if (user != null) {
                    val result: DataResult<List<LogData>> = logRepository.getLogs(friendId, false)
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is DataResult.Success -> {
                                updateLogData(result.item)
                            }
                            is DataResult.Failure -> throw result.throwable
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
            }
        }
    }

    suspend fun getFriends(friendId: String) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser?.uid
                if (user != null) {
                    val result = friendRepository.getFriends(friendId)
                    updateFriends(result)
                }
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
            }
        }
    }
}