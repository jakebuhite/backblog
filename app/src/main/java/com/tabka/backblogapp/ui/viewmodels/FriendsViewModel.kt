package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.LogRequestData
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

open class FriendsViewModel : ViewModel() {
    private val tag = "FriendsViewModel"
    private val auth = Firebase.auth

    // User Info
    private val userRepository = UserRepository()
    val userData: MutableLiveData<UserData> = MutableLiveData()

    // Log Info
    private val logRepository = LogRepository()
    val publicLogData: MutableLiveData<List<LogData>> = MutableLiveData()

    // Request Info
    private val friendRepository = FriendRepository()
    val friendReqData: MutableLiveData<List<Pair<FriendRequestData, UserData>>> = MutableLiveData()
    val logReqData: MutableLiveData<List<Pair<LogRequestData, UserData>>> = MutableLiveData()

    //val friendsData: MutableLiveData<List<UserData>> = MutableLiveData()
    private val _friendsData = MutableStateFlow<List<UserData>>(emptyList())
    open val friendsData = _friendsData.asStateFlow()

    // Status Message
    val notificationMsg: MutableLiveData<String> = MutableLiveData("")

    private fun updateMessage(message: String) {
        notificationMsg.value = message
    }

    fun clearMessage() {
        notificationMsg.value = ""
    }

    private fun updateUserData(user: UserData) {
        userData.value = user
    }

    private fun updateLogData(logData: List<LogData>) {
        publicLogData.value = logData
    }

    private fun updateFriendReqData(reqData: List<Pair<FriendRequestData, UserData>>) {
        friendReqData.value = reqData
    }

    private fun updateLogReqData(reqData: List<Pair<LogRequestData, UserData>>) {
        logReqData.value = reqData
    }

    private fun updateFriends(newFriends: List<UserData>) {
        _friendsData.value = newFriends
    }

    suspend fun getUserData() {
        viewModelScope.launch {
            try {
                val user = auth.currentUser?.uid
                if (user != null) {
                    val result: DataResult<UserData> = userRepository.getUser(user)
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

    suspend fun getPublicLogs() {
        viewModelScope.launch {
            try {
                val user = auth.currentUser?.uid
                if (user != null) {
                    val result: DataResult<List<LogData>> = logRepository.getLogs(user, false)
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

    suspend fun getFriendRequests() {
        viewModelScope.launch {
            try {
                val user = auth.currentUser?.uid
                if (user != null) {
                    val result: DataResult<List<FriendRequestData>> = friendRepository.getFriendRequests(user)
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is DataResult.Success -> {
                                val friendRequests = result.item
                                val friendRequestsWithUserData = getFriendSenderData(friendRequests)
                                updateFriendReqData(friendRequestsWithUserData)
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

    fun sendFriendRequest(targetUsername: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    updateMessage("User not authenticated")
                    return@launch
                }

                if (targetUsername == userData.value?.username) {
                    updateMessage("You cannot friend yourself!")
                    return@launch
                }

                val result = userRepository.getUserByUsername(targetUsername)
                if (result is DataResult.Failure) {
                    throw result.throwable
                }

                val user = (result as DataResult.Success).item
                val friends = user.friends ?: emptyMap()
                if (friends.containsKey(userId)) {
                    updateMessage("$targetUsername is already a friend!")
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
                    updateMessage("You've already sent a request to $targetUsername!")
                    return@launch
                }

                val userRequests = friendRepository.getFriendRequests(userId)
                if (userRequests is DataResult.Failure) {
                    throw userRequests.throwable
                }

                if ((userRequests as DataResult.Success).item.any {
                    it.senderId == targetId && it.targetId == userId
                }) {
                    updateMessage("$targetUsername has already sent you a friend request!")
                    return@launch
                }

                val addFriendReq = friendRepository.addFriendRequest(userId, targetId, System.currentTimeMillis().toString())
                if (addFriendReq is DataResult.Failure) {
                    throw addFriendReq.throwable
                }

                updateMessage("Friend request sent to $targetUsername!")
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
                updateMessage("There was an error sending a request")
            }
        }
    }

    fun updateRequest(reqId: String, reqType: String, accepted: Boolean) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    updateMessage("User not authenticated")
                    return@launch
                }

                val result = if (reqType.lowercase() == "log") {
                    // Log request
                    friendRepository.updateLogRequest(reqId, accepted)
                } else {
                    // Friend request
                    friendRepository.updateFriendRequest(reqId, accepted)
                }

                when (result) {
                    is DataResult.Failure -> result.throwable
                    is DataResult.Success -> {
                        updateMessage("Successfully updated request!")
                    }
                }
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
                updateMessage("There was an error sending a request")
            }
        }
    }

    suspend fun getLogRequests() {
        viewModelScope.launch {
            try {
                val user = auth.currentUser?.uid
                if (user != null) {
                    val result: DataResult<List<LogRequestData>> = friendRepository.getLogRequests(user)
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is DataResult.Success -> {
                                val logRequests = result.item
                                val logRequestsDataWithUserData = getLogSenderData(logRequests)
                                updateLogReqData(logRequestsDataWithUserData)
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

    suspend fun getFriends() {
        viewModelScope.launch {
            try {
                val user = auth.currentUser?.uid
                if (user != null) {
                    val result = friendRepository.getFriends(user)
                    updateFriends(result)
                }
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
            }
        }
    }

    private suspend fun getLogSenderData(logReqData: List<LogRequestData>): List<Pair<LogRequestData, UserData>> {
        val requestsUserData = mutableListOf<Pair<LogRequestData, UserData>>()

        for (req in logReqData) {
            val senderId = req.senderId ?: ""
            when (val response = userRepository.getUser(senderId)) {
                is DataResult.Success -> {
                    val userData = response.item
                    requestsUserData.add(Pair(req, userData))
                }
                is DataResult.Failure -> {
                    Log.d(tag, "Error fetching user data for sender $senderId: ${response.throwable}")
                }
            }
        }

        return requestsUserData
    }

    private suspend fun getFriendSenderData(logReqData: List<FriendRequestData>): List<Pair<FriendRequestData, UserData>> {
        val requestsUserData = mutableListOf<Pair<FriendRequestData, UserData>>()

        for (req in logReqData) {
            val senderId = req.senderId ?: ""
            when (val response = userRepository.getUser(senderId)) {
                is DataResult.Success -> {
                    val userData = response.item
                    requestsUserData.add(Pair(req, userData))
                }
                is DataResult.Failure -> {
                    Log.d(tag, "Error fetching user data for sender $senderId: ${response.throwable}")
                }
            }
        }

        return requestsUserData
    }

}