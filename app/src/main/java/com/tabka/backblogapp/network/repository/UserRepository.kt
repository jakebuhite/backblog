package com.tabka.backblogapp.network.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.FirebaseError
import com.tabka.backblogapp.util.FirebaseExceptionType
import com.tabka.backblogapp.util.toJsonElement
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserRepository(val db: FirebaseFirestore = Firebase.firestore, val auth: FirebaseAuth = Firebase.auth) {
    private val tag = "UsersRepo"

    suspend fun addUser(userId: String, username: String, avatarPreset: Int): DataResult<Boolean> {
        try {
            val userData = mapOf(
                "userId" to userId,
                "username" to username,
                "joinDate" to System.currentTimeMillis().toString(),
                "avatarPreset" to avatarPreset,
                "friends" to emptyMap<String, Boolean>(),
                "blocked" to emptyMap<String, Boolean>()
            )

            db.collection("users").document(userId).set(userData).await()

            Log.d(tag, "User successfully written!")
            return DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error writing user document", e)
            return DataResult.Failure(e)
        }
    }

    suspend fun getUser(userId: String): DataResult<UserData> {
        try {
            val result = db.collection("users").document(userId).get().await()

            return if (result.exists()) {
                val data = result.data

                val userData = Json.decodeFromString<UserData>(Json.encodeToString(data.toJsonElement()))

                DataResult.Success(userData)
            } else {
                DataResult.Failure(FirebaseError(FirebaseExceptionType.NOT_FOUND))
            }
        } catch (e: Exception) {
            Log.w(tag, "Error receiving user document", e)
            return DataResult.Failure(e)
        }
    }

    suspend fun updateUser(userId: String, updateData: Map<String, Any?>): DataResult<Boolean> {
        try {
            val updatedUserObj = mutableMapOf<String, Any>()

            // Add the modified properties to updatedUserObj
            updateData["username"]?.let { updatedUserObj["username"] = it }
            updateData["avatar_preset"]?.let { updatedUserObj["avatar_preset"] = it }
            updateData["friends"]?.let { updatedUserObj["friends"] = it }
            updateData["blocked"]?.let { updatedUserObj["blocked"] = it }

            if (updatedUserObj.isNotEmpty()) {
                // Update Firestore user document
                db.collection("users").document(userId)
                    .update(updatedUserObj)
                    .await()
            }

            // Check if the password is provided in the request body
            updateData["password"]?.let {
                auth.currentUser!!.updatePassword(it as String).await()
            }

            return DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error updating user", e)
            return DataResult.Failure(FirebaseError(FirebaseExceptionType.FAILED_TRANSACTION))
        }
    }
}
