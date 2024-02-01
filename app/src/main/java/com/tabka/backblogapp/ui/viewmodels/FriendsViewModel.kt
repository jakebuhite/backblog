package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.auth.User
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendsViewModel(): ViewModel() {
    private val TAG = "FriendsViewModel"
    private val userRepository = UserRepository()
    private val _username = MutableStateFlow<String?>(null)
    val username = _username.asStateFlow()
    private val auth = Firebase.auth

    init {
        viewModelScope.launch {
            _username.value = getUsername()
        }

    }

    private suspend fun getUsername(): String? {
        Log.d(TAG, "Trying to get Username")
        val user = auth.currentUser?.uid
        Log.d(TAG, "$user")

        if (user != null) {
            val userData: DataResult<UserData> = userRepository.getUser(user)
            when (val result = userData) {
                is DataResult.Success -> {
                    val userData = result.item
                    return userData.username
                }
                is DataResult.Failure -> {
                    val error = result.throwable
                    // Do something with error!
                }
            }
        }
        return null
    }

}