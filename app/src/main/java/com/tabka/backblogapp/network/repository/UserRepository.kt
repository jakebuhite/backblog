package com.tabka.backblogapp.network.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogRequestData
import com.tabka.backblogapp.network.models.UserData

class UserRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val tag = "FriendsRepo"

    fun addUser(userId: String, username: String, avatarPreset: Int?) {
        // If no avatar preset provided, select default
        val avatar = avatarPreset ?: 1

        // Get all user data
        val userData = UserData(
            userId = userId,
            username = username,
            joinDate = System.currentTimeMillis().toString(),
            avatarPreset = avatar,
            friends = emptyMap(),
            blocked = emptyMap()
        )

        db.collection("users").document(userId).set(userData)
            .addOnSuccessListener { Log.d(tag, "User successfully written!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing user document", e) }
    }

    fun getUser(userId: String): UserData? {
        var userData : UserData? = null

        db.collection("users").document(userId).get()
            .addOnSuccessListener {
                Log.d(tag, "User successfully received!")
                if (it.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    userData = UserData(
                        userId = it.getString("user_id"),
                        username = it.getString("username"),
                        joinDate = it.getString("join_date"),
                        avatarPreset = it.getLong("avatar_preset")!!.toInt(),
                        friends = it.data?.get("friends") as Map<String, Boolean>?,
                        blocked = it.data?.get("friends") as Map<String, Boolean>?
                    )
                }
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving user document", e) }

        return userData
    }

    fun updateUser(userId: String, updateData: Map<String, Any?>) {
        val updatedUserObj = mutableMapOf<String, Any>()

        // Add the modified properties to updatedUserObj
        updateData["username"]?.let { updatedUserObj["username"] = it }
        updateData["avatarPreset"]?.let { updatedUserObj["avatar_preset"] = it }
        updateData["friends"]?.let { updatedUserObj["friends"] = it }
        updateData["blocked"]?.let { updatedUserObj["blocked"] = it }

        if (updatedUserObj.isNotEmpty()) {
            // Update Firestore user document
            db.collection("users").document(userId).update(updatedUserObj)
                .addOnSuccessListener { Log.d(tag, "User successfully written!") }
                .addOnFailureListener { e -> Log.w(tag, "Error writing user document", e) }
        }

        // Check if password is provided in the request body
        updateData["password"]?.let {
            auth.currentUser!!.updatePassword(updateData["password"] as String)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "User password updated.")
                    }
                }
        }
    }

    fun getLogRequests(userId: String): List<LogRequestData> {
        var logRequests = emptyList<DocumentSnapshot>()

        db.collection("log_requests")
            .whereEqualTo("target_id", userId)
            .get()
            .addOnSuccessListener {
                Log.d(tag, "Log request successfully received!")
                logRequests = it.documents
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving log request document", e) }

        val logRequestData = logRequests.map { e ->
            LogRequestData(
                senderId = e.getString("sender_id"),
                targetId = e.getString("target_id"),
                logId = e.getString("log_id"),
                requestDate = e.getString("request_date"),
                isComplete = e.getBoolean("is_complete")
            )
        }.toMutableList()

        return logRequestData
    }

    fun getFriendRequests(userId: String): List<FriendRequestData> {
        var friendRequests = emptyList<DocumentSnapshot>()

        db.collection("friend_requests")
            .whereEqualTo("target_id", userId)
            .get()
            .addOnSuccessListener {
                Log.d(tag, "Friend request successfully received!")
                friendRequests = it.documents
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving friend request document", e) }

        val friendRequestData = friendRequests.map { e ->
            FriendRequestData(
                senderId = e.getString("sender_id"),
                targetId = e.getString("target_id"),
                requestDate = e.getString("request_date"),
                isComplete = e.getBoolean("is_complete")
            )
        }.toMutableList()

        return friendRequestData
    }
}
