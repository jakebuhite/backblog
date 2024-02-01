package com.tabka.backblogapp.ui.viewmodels

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

    suspend fun getUsername(): String? {
       val user = auth.currentUser?.uid

        if(user != null) {
            val userData: DataResult<UserData> = userRepository.getUser(user)
            //return userData?.username
        }
        return null
    }

}