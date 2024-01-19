package com.tabka.backblogapp.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.models.FriendRequestData
import com.tabka.backblogapp.models.LogData
import com.tabka.backblogapp.models.LogRequestData
import com.tabka.backblogapp.models.UserData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FriendsRepository {

    private val db = Firebase.firestore
    private val tag = "FriendsRepo"

    fun addLogRequest(senderId: String, targetId: String, logId: String) {
        val logRequestData = LogRequestData(
            senderId = senderId,
            targetId = targetId,
            logId = logId,
            requestDate = System.currentTimeMillis().toString(),
            isComplete = false
        )

        db.collection("log_requests").add(logRequestData)
            .addOnSuccessListener { Log.d(tag, "Log request successfully written!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing log request document", e) }
    }

    fun addFriendRequest(senderId: String, targetId: String) {
        val friendRequestData = FriendRequestData(
            senderId = senderId,
            targetId = targetId,
            requestDate = System.currentTimeMillis().toString(),
            isComplete = false
        )

        db.collection("friend_requests").add(friendRequestData)
            .addOnSuccessListener { Log.d(tag, "Friend request successfully written!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing friend request document", e) }
    }

    suspend fun getFriends(userId: String): MutableList<UserData>? {
        // Get user data
        var userData: UserData? = null

        val docRef = db.collection("users").document(userId)

        try {
            // Fetch the document
            val document = docRef.get().await()

            if (document.exists()) {
                @Suppress("UNCHECKED_CAST")
                userData = UserData(
                    userId = document.id,
                    username = document.getString("username"),
                    joinDate = document.getString("join_date"),
                    avatarPreset = document.getLong("avatar_preset")?.toInt(),
                    friends = document.data?.get("friends") as? Map<String, Boolean>?,
                    blocked = document.data?.get("blocked") as? Map<String, Boolean>?
                )
            } else {
                Log.d(tag, "No such document")
            }
        } catch (exception: Exception) {
            Log.d(tag, "get failed with ", exception)
        }

        // Return if no user to obtain friend data from
        if (userData == null) {
            return userData
        }

        val friendsData = mutableListOf<UserData>()

        // Check if the user has friends
        val friendIds = (userData.friends)?.keys?.toList() ?: emptyList()
        if (friendIds.isEmpty()) {
            return friendsData
        }

        coroutineScope {
            // Use async to fetch friend data concurrently
            val deferredFriends = friendIds.map { friendId ->
                async {
                    // Fetch friend's user data
                    val friendRef = db.collection("users").document(friendId)
                    val friendDoc = friendRef.get().await()

                    if (friendDoc.exists()) {
                        // Create User object for friend and add it to the list
                        @Suppress("UNCHECKED_CAST")
                        UserData(
                            userId = friendDoc.id,
                            username = friendDoc.getString("username"),
                            joinDate = friendDoc.getString("join_date"),
                            avatarPreset = friendDoc.getLong("avatar_preset")?.toInt(),
                            friends = friendDoc.data?.get("friends") as? Map<String, Boolean>,
                            blocked = friendDoc.data?.get("blocked") as? Map<String, Boolean>
                        )
                    } else {
                        null
                    }
                }
            }

            // Await the results and add them to friendsData
            friendsData.addAll(deferredFriends.awaitAll().filterNotNull())
        }

        return friendsData
    }

    fun updateFriendRequest(friendRequestId: String, isAccepted: Boolean) {
        val reqRef = db.collection("friend_requests").document(friendRequestId)

        // Mark as complete
        reqRef.update(mapOf("is_complete" to true))
            .addOnSuccessListener { Log.d(tag, "Friend request successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing friend request document", e) }

        if (isAccepted) {
            reqRef.get()
                .addOnSuccessListener{
                    if (it.exists()) {
                        addFriendToUser(it.getString("sender_id")!!, it.getString("target_id")!!)
                    } else {
                        Log.d(tag, "Unable to find friend request!")
                    }
                }
                .addOnFailureListener{ e -> Log.w(tag, "Error writing friend request document", e) }
        }
    }

    fun updateLogRequest(logRequestId: String, isAccepted: Boolean) {
        val reqRef = db.collection("log_requests").document(logRequestId)

        // Mark as complete
        reqRef.update(mapOf("is_complete" to true))
            .addOnSuccessListener { Log.d(tag, "Log request successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing log request document", e) }

        if (isAccepted) {
            reqRef.get()
                .addOnSuccessListener{
                    if (it.exists()) {
                        addCollaborator(it.getString("target_id")!!, it.getString("log_id")!!)
                    } else {
                        Log.d(tag, "Unable to find log request!")
                    }
                }
                .addOnFailureListener{ e -> Log.w(tag, "Error writing log request document", e) }
        }
    }

    fun removeFriend(userId: String, friendId: String) {
        val userRef = db.collection("users").document(userId)

        // Remove friend from user's friends
        val updates = hashMapOf<String, Any>(
            "friends.${friendId}" to FieldValue.delete(),
        )

        userRef.update(updates)
            .addOnSuccessListener {
                Log.d(tag, "Friend successfully removed!")
            }
            .addOnFailureListener { e -> Log.w(tag, "Error updating user document", e) }
    }

    // TODO Ensure blocker is removed from friends list, logs involving this user (excluding ones he owns)
    //  Blocked user must also be removed from the logs that the user above owns
    fun blockUser(userId: String, blockedId: String) {
        val userRef = db.collection("users").document(userId)

        // Add blocked user to user's blocked list
        userRef.update(mapOf("blocked.${blockedId}" to true))
            .addOnSuccessListener {
                Log.d(tag, "User successfully blocked!")
            }
            .addOnFailureListener { e -> Log.w(tag, "Error updating user document", e) }
    }

    private fun addFriendToUser(userId: String, friendId: String) {
        db.collection("users").document(userId)
            .update("friends.$friendId", true)
            .addOnSuccessListener { Log.d(tag, "Friend successfully added!") }
            .addOnFailureListener { e -> Log.w(tag, "Error updating user document", e) }
    }

    // Add collaborator to user doc
    private fun addCollaborator(userId: String, logId: String) {
        val logRef = db.collection("logs").document(logId)

        // Get log data
        var logData : LogData? = null
        logRef.get()
            .addOnSuccessListener {
                Log.d(tag, "Successfully received log data!")
                @Suppress("UNCHECKED_CAST")
                logData = LogData(
                    logId = it.id,
                    name = it.getString("name"),
                    creationDate = it.getString("creation_date"),
                    lastModifiedDate = it.getString("last_modified_date"),
                    status = it.getString("status"),
                    owner = it.data?.get("owner") as? Map<String, Any>?,
                    collaborators = it.data?.get("collaborators") as Map<String, Map<String, Int>>?,
                    movieIds = it.data?.get("movie_ids") as Map<String, Boolean>?,
                    watchedIds = it.data?.get("watched_ids") as Map<String, Boolean>?
                )
            }
            .addOnFailureListener {
                    e -> Log.w(tag, "Error reading log document", e)
            }

        // Return if no user to obtain friend data from
        if (logData == null) {
            return
        }

        // Check if the user is already a collaborator
        if ((logData!!.collaborators != null) && logData!!.collaborators!!.containsKey(userId)) {
            Log.d(tag, "User is already a collaborator.")
            return
        }

        // Add user as a collaborator
        logRef.update("collaborators.$userId", mapOf("priority" to 0))
            .addOnSuccessListener { Log.d(tag, "User successfully added as a collaborator!") }
            .addOnFailureListener { e -> Log.w(tag, "Error updating user document", e) }
    }
}
