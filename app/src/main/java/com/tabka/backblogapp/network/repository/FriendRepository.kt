package com.tabka.backblogapp.network.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.FirebaseError
import com.tabka.backblogapp.util.FirebaseExceptionType
import com.tabka.backblogapp.util.NetworkError
import com.tabka.backblogapp.util.NetworkExceptionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FriendRepository(private val db: FirebaseFirestore) {
    private val tag = "FriendsRepo"

    suspend fun addLogRequest(senderId: String, targetId: String, logId: String, requestDate: String): DataResult<Boolean> {
        return try {
            val logRequestData = mapOf(
                "sender_id" to senderId,
                "target_id" to targetId,
                "log_id" to logId,
                "request_date" to requestDate,
                "is_complete" to false
            )

            db.collection("log_requests")
                .add(logRequestData)
                .await()

            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Failure(e)
        }
    }

    suspend fun addFriendRequest(senderId: String, targetId: String, requestDate: String): DataResult<Boolean> {
        return try {
            val friendRequestData = mapOf(
                "senderId" to senderId,
                "targetId" to targetId,
                "requestDate" to requestDate,
                "isComplete" to false
            )

            db.collection("friend_requests")
                .add(friendRequestData)
                .await()

            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Failure(e)
        }
    }

    suspend fun getFriends(userId: String): List<UserData> = withContext(Dispatchers.IO) {
        val usersCollection = db.collection("users")

        try {
            val userDocument = usersCollection.document(userId).get().await()

            @Suppress("UNCHECKED_CAST")
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

    suspend fun updateFriendRequest(friendRequestId: String, isAccepted: Boolean): DataResult<Boolean> {
        return try {
            val reqRef = db.collection("friend_requests").document(friendRequestId)

            val updates: MutableMap<String, Any> = mutableMapOf()

            // Mark as complete
            updates["is_complete"] = true
            reqRef.update(updates).await()

            if (isAccepted) {
                // Perform operations sequentially without async
                val reqData = reqRef.get().await()
                if (!reqData.exists()) {
                    return DataResult.Failure(FirebaseError(FirebaseExceptionType.DOES_NOT_EXIST))
                }

                when (addFriendToUser(reqData["sender_id"].toString(), reqData["target_id"].toString())) {
                    is DataResult.Success -> {
                        DataResult.Success(true)
                    }
                    is DataResult.Failure -> {
                        DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                    }
                }
            }

            DataResult.Success(true)
        } catch (e: Exception) {
            println("Error writing friend request document $e")
            DataResult.Failure(e)
        }
    }

    suspend fun updateLogRequest(logRequestId: String, isAccepted: Boolean): DataResult<Boolean> {
        return try {
            val reqRef = db.collection("log_requests").document(logRequestId)

            val updates: MutableMap<String, Any> = mutableMapOf()

            // Mark as complete
            updates["is_complete"] = true
            reqRef.update(updates).await()

            if (isAccepted) {
                // Perform operations sequentially without async
                val reqData = reqRef.get().await()
                if (!reqData.exists()) {
                    return DataResult.Failure(FirebaseError(FirebaseExceptionType.DOES_NOT_EXIST))
                }

                when (addCollaborator(reqData["sender_id"].toString(), reqData["target_id"].toString())) {
                    is DataResult.Success -> {
                        DataResult.Success(true)
                    }
                    is DataResult.Failure -> {
                        DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                    }
                }
            }

            DataResult.Success(true)
        } catch (e: Exception) {
            println("Error writing log  request document $e")
            DataResult.Failure(e)
        }
    }

    suspend fun removeFriend(userId: String, friendId: String): DataResult<Boolean> {
        return try {
            val userRef = db.collection("users").document(userId)

            // Remove friend from user's friends
            val updates = mapOf("friends.${friendId}" to FieldValue.delete())
            userRef.update(updates).await()

            Log.d(tag, "Friend successfully removed!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error updating user document", e)
            DataResult.Failure(e)
        }
    }

    // TODO Ensure blocker is removed from friends list, logs involving this user (excluding ones he owns)
    //  Blocked user must also be removed from the logs that the user above owns
    suspend fun blockUser(userId: String, blockedId: String): DataResult<Boolean> {
        return try {
            db.collection("users").document(userId)
                .update(mapOf("blocked.${blockedId}" to true)).await()

            Log.d(tag, "User successfully blocked!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error updating log document", e)
            DataResult.Failure(e)
        }
    }

    private suspend fun addFriendToUser(userId: String, friendId: String): DataResult<Boolean> {
        return try {
            db.collection("users").document(userId)
                .update("friends.$friendId", true)
                .await()
            DataResult.Success(true)
        } catch (e: Exception) {
            println("Error updating user document $e")
            DataResult.Failure(e)
        }
    }

    private suspend fun addCollaborator(userId: String, logId: String): DataResult<Boolean> {
        return try {
            val logRef = db.collection("logs").document(logId)

            // Add user as a collaborator
            logRef.update("collaborators.$userId", mapOf("priority" to 0)).await()

            Log.d(tag, "User successfully added as a collaborator!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error reading log document", e)
            DataResult.Failure(FirebaseError(FirebaseExceptionType.FAILED_TRANSACTION))
        }
    }
}
