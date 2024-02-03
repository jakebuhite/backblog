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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsViewModel : ViewModel() {
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
    val friendsData: MutableLiveData<List<UserData>> = MutableLiveData()

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
        friendsData.value = newFriends
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