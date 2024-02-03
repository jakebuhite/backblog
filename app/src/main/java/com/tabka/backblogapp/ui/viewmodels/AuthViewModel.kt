package com.tabka.backblogapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.getErrorMessage
import kotlinx.coroutines.tasks.await

private val auth = Firebase.auth

class AuthViewModel: ViewModel() {
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

    suspend fun attemptSignup(email: String, username: String, password: String): Pair<Boolean, String> {
        var resultMsg = "Something went wrong. Please try again."

        try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
            if (result.user != null) {
                val userRepository = UserRepository()
                userRepository.addUser(result.user!!.uid, username.trim(), 1)
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

}