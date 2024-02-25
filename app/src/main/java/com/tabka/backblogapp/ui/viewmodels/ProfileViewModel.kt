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

open class ProfileViewModel : ViewModel() {
    private val tag = "ProfileViewModel"
    private val auth = Firebase.auth

    // User Info
    private val userRepository = UserRepository()
    open val userData: MutableLiveData<UserData> = MutableLiveData()

    // Log Info
    private val logRepository = LogRepository()
    open val publicLogData: MutableLiveData<List<LogData>> = MutableLiveData()

    // Friend Info
    private val friendRepository = FriendRepository()
    private val _friendsData = MutableStateFlow<List<UserData>>(emptyList())
    open val friendsData = _friendsData.asStateFlow()

    // Status Message
    val notificationMsg: MutableLiveData<String> = MutableLiveData("")

    private fun updateMessage(message: String) {
        notificationMsg.value = message
    }

    private fun updateUserData(user: UserData) {
        userData.value = user
    }

    private fun updateLogData(logData: List<LogData>) {
        publicLogData.value = logData
    }

    private fun updateFriends(newFriends: List<UserData>) {
        _friendsData.value = newFriends
    }

    fun clearMessage() {
        notificationMsg.value = ""
    }

    open suspend fun getUserData(friendId: String) {
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

    open suspend fun getPublicLogs(friendId: String) {
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

    open suspend fun getFriends(friendId: String) {
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

    open fun sendFriendRequest() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    updateMessage("User not authenticated")
                    return@launch
                }

                val friendUsername = userData.value?.username
                if (friendUsername == null) {
                    updateMessage("User not found")
                    return@launch
                }

                val result = userRepository.getUserByUsername(friendUsername)
                if (result is DataResult.Failure) {
                    throw result.throwable
                }

                val user = (result as DataResult.Success).item
                val friends = user.friends ?: emptyMap()
                if (friends.containsKey(userId)) {
                    updateMessage("$friendUsername is already a friend!")
                    return@launch
                }

                val targetId = user.userId ?: ""
                val targetRequests = friendRepository.getFriendRequests(targetId)
                if (targetRequests is DataResult.Failure) {
                    throw targetRequests.throwable
                }

                if ((targetRequests as DataResult.Success).item.any {
                        it.senderId == userId && it.targetId == targetId
                    }) {
                    updateMessage("You've already sent a request to $friendUsername!")
                    return@launch
                }

                val userRequests = friendRepository.getFriendRequests(userId)
                if (userRequests is DataResult.Failure) {
                    throw userRequests.throwable
                }

                if ((userRequests as DataResult.Success).item.any {
                        it.senderId == targetId && it.targetId == userId
                    }) {
                    updateMessage("$friendUsername has already sent you a friend request!")
                    return@launch
                }

                val addFriendReq = friendRepository.addFriendRequest(userId, targetId, System.currentTimeMillis().toString())
                if (addFriendReq is DataResult.Failure) {
                    throw addFriendReq.throwable
                }

                updateMessage("Friend request sent to $friendUsername!")
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
                updateMessage("There was an error sending a request")
            }
        }
    }

    open fun removeFriend() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    updateMessage("User not authenticated")
                    return@launch
                }

                val friendId = userData.value?.userId
                if (friendId == null) {
                    updateMessage("User not found")
                    return@launch
                }

                when(val result = friendRepository.removeFriend(userId, friendId)) {
                    is DataResult.Failure -> throw result.throwable
                    is DataResult.Success -> {
                        val username = userData.value?.username ?: "Unknown"
                        updateMessage("Removed $username as a friend!")
                    }
                }
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
                updateMessage("There was an error sending a request")
            }
        }
    }

    open fun blockUser() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    updateMessage("User not authenticated")
                    return@launch
                }

                val friendId = userData.value?.userId
                if (friendId == null) {
                    updateMessage("User not found")
                    return@launch
                }

                val alreadyFriends = userData.value?.friends?.any { it.key == userId } ?: false

                when(val result = friendRepository.blockUser(userId, friendId, alreadyFriends )) {
                    is DataResult.Failure -> throw result.throwable
                    is DataResult.Success -> {
                        val username = userData.value?.username ?: "Unknown"
                        updateMessage("Successfully blocked $username!")
                    }
                }
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
                updateMessage("There was an error sending a request")
            }
        }
    }
}