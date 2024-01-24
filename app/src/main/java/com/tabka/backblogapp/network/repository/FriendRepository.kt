package com.tabka.backblogapp.network.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.LogRequestData
import com.tabka.backblogapp.network.models.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FriendRepository(private val db: FirebaseFirestore) {
    private val tag = "FriendsRepo"

    suspend fun addLogRequest(senderId: String, targetId: String, logId: String, requestDate: String): Result<Unit> = coroutineScope {
        try {
            val logRef = db.collection("log_requests")
            val logRequestData = LogRequestData(
                senderId = senderId,
                targetId = targetId,
                logId = logId,
                requestDate = requestDate,
                isComplete = false
            )
            logRef.add(logRequestData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFriendRequest(senderId: String, targetId: String, requestDate: String): Result<Unit> = coroutineScope {
        try {
            val collectionReference = db.collection("friend_requests")
            val friendRequestData = FriendRequestData(
                senderId = senderId,
                targetId = targetId,
                requestDate = requestDate,
                isComplete = false
            )

            collectionReference.add(friendRequestData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFriends(userId: String): List<UserData> = withContext(Dispatchers.IO) {
        val usersCollection = db.collection("users")

        try {
            val userDocument = usersCollection.document(userId).get().await()

            val friendsMap = userDocument.data?.get("friends") as Map<String, Boolean>?
            val friendIds = friendsMap?.keys ?: emptySet()

            val friendUserDataList = mutableListOf<UserData>()

            for (friendId in friendIds) {
                val friendDocument = usersCollection.document(friendId).get().await()

                @Suppress("UNCHECKED_CAST")
                val friendUserData = UserData(
                    userId = friendDocument.id,
                    username = friendDocument.getString("name"),
                    joinDate = friendDocument.getString("join_date"),
                    avatarPreset = friendDocument.getLong("avatar_preset")?.toInt() ?: 0,
                    friends = friendDocument.data?.get("friends") as Map<String, Boolean>?,
                    blocked = friendDocument.data?.get("blocked") as Map<String, Boolean>?
                )

                friendUserDataList.add(friendUserData)
            }

            return@withContext friendUserDataList
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateFriendRequest(friendRequestId: String, isAccepted: Boolean): Result<Unit> = coroutineScope {
        val reqRef = db.collection("friend_requests").document(friendRequestId)

        try {
            // Mark as complete
            reqRef.update(mapOf("is_complete" to true)).await()

            if (isAccepted) {
                // Perform operations sequentially without async
                val friendRequestData = reqRef.get().await().data

                friendRequestData?.let {
                    addFriendToUser(it["sender_id"].toString(), it["target_id"].toString())
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            println("Error writing friend request document $e")
            Result.failure(e)
        }
    }

    suspend fun updateLogRequest(logRequestId: String, isAccepted: Boolean) {
        val reqRef = db.collection("log_requests").document(logRequestId)

        // Mark as complete
        reqRef.update(mapOf("is_complete" to true))
            .addOnSuccessListener { Log.d(tag, "Log request successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing log request document", e) }
            .await()

        if (isAccepted) {
            reqRef.get()
                .addOnSuccessListener{
                    if (it.exists()) {
                        runBlocking {
                            addCollaborator(it.getString("target_id")!!, it.getString("log_id")!!)
                        }
                    } else {
                        Log.d(tag, "Unable to find log request!")
                    }
                }
                .addOnFailureListener{ e -> Log.w(tag, "Error writing log request document", e) }
                .await()
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

    private suspend fun addFriendToUser(userId: String, friendId: String): Result<Unit> = coroutineScope {
        try {
            db.collection("users").document(userId)
                .update("friends.$friendId", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            println("Error updating user document $e")
            Result.failure(e)
        }
    }

    private suspend fun addCollaborator(userId: String, logId: String) {
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
                    isVisible = it.getBoolean("is_visible"),
                    creationDate = it.getString("creation_date"),
                    lastModifiedDate = it.getString("last_modified_date"),
                    movieIds = it.data?.get("movie_ids") as Map<String, Boolean>?,
                    watchedIds = it.data?.get("watched_ids") as Map<String, Boolean>?,
                    owner = it.data?.get("owner") as? Map<String, Any>?,
                    collaborators = it.data?.get("collaborators") as Map<String, Map<String, Int>>?
                )
            }
            .addOnFailureListener {
                    e -> Log.w(tag, "Error reading log document", e)
            }
            .await()

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
