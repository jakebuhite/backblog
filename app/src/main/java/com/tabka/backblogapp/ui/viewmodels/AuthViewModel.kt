package com.tabka.backblogapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.repository.UserRepository
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
            resultMsg = getErrorMessage(errorCode)
        }
        return Pair(false, resultMsg)
    }

    private fun getErrorMessage(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email."
            "ERROR_INVALID_PASSWORD" -> "Invalid password."
            "ERROR_INVALID_CREDENTIAL" -> "Incorrect email or password."
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "An account already exists with the same email address."
            "ERROR_EMAIL_EXISTS" -> "The email address is already in use by another account."
            "ERROR_USER_DISABLED" -> "The user account has been disabled by an administrator."
            "ERROR_USER_DELETED" -> "User not found."
            else -> "There was an error performing authentication."
        }
    }

}