//
//  FriendRepository.kt
//  backblog
//
//  Created by Jake Buhite on 2/9/24.
//
package com.tabka.backblogapp.network.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogRequestData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.FirebaseError
import com.tabka.backblogapp.util.FirebaseExceptionType
import com.tabka.backblogapp.util.NetworkError
import com.tabka.backblogapp.util.NetworkExceptionType
import com.tabka.backblogapp.util.toJsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FriendRepository(
    val db: FirebaseFirestore = Firebase.firestore,
    val auth: FirebaseAuth = Firebase.auth
) {
    private val tag = "FriendsRepo"

    suspend fun addLogRequest(senderId: String, targetId: String, logId: String, requestDate: String): DataResult<Boolean> {
        return try {
            val reqRef = db.collection("log_requests").document()
            val reqId = reqRef.id

            val logRequestData = mapOf(
                "request_id" to reqId,
                "sender_id" to senderId,
                "target_id" to targetId,
                "log_id" to logId,
                "request_date" to requestDate,
                "is_complete" to false
            )

            db.collection("log_requests")
                .document(reqId)
                .set(logRequestData)
                .await()

            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Failure(e)
        }
    }

    suspend fun addFriendRequest(senderId: String, targetId: String, requestDate: String): DataResult<Boolean> {
        return try {
            val reqId = db.collection("friend_requests").document().id

            val friendRequestData = mapOf(
                "request_id" to reqId,
                "sender_id" to senderId,
                "target_id" to targetId,
                "request_date" to requestDate,
                "is_complete" to false
            )

            db.collection("friend_requests")
                .document(reqId)
                .set(friendRequestData)
                .await()

            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Failure(e)
        }
    }

    suspend fun getFriendRequests(userId: String): DataResult<List<FriendRequestData>> {
        val requests: MutableList<FriendRequestData> = mutableListOf()
        try {
            val snapshot = db.collection("friend_requests")
                .whereEqualTo("target_id", userId)
                .whereEqualTo("is_complete", false).get().await()
            requests.addAll(snapshot.documents.map { doc -> Json.decodeFromString(Json.encodeToString(doc.data.toJsonElement())) })
        } catch (e: Exception) {
            Log.w(tag, "Error getting friend request", e)
            DataResult.Failure(e)
        }

        return DataResult.Success(requests)
    }

    suspend fun getLogRequests(userId: String): DataResult<List<LogRequestData>> {
        val requests: MutableList<LogRequestData> = mutableListOf()
        try {
            val snapshot = db.collection("log_requests")
                .whereEqualTo("target_id", userId)
                .whereEqualTo("is_complete", false).get().await()
            requests.addAll(snapshot.documents.map { doc -> Json.decodeFromString(Json.encodeToString(doc.data.toJsonElement())) })
        } catch (e: Exception) {
            Log.w(tag, "Error getting log requests", e)
            DataResult.Failure(e)
        }

        return DataResult.Success(requests)
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

                val friendUserData = Json.decodeFromString<UserData>(Json.encodeToString(friendDocument.data.toJsonElement()))

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

                if (reqData["sender_id"].toString() == auth.currentUser?.uid) {
                    when (addFriendToUser(reqData["sender_id"].toString(), reqData["target_id"].toString())) {
                        is DataResult.Success -> {
                            when (addFriendToUser(reqData["target_id"].toString(), reqData["sender_id"].toString())) {
                                is DataResult.Failure -> DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                                is DataResult.Success -> DataResult.Success(true)
                            }
                        }
                        is DataResult.Failure -> {
                            DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                        }
                    }
                } else {
                    when (addFriendToUser(reqData["target_id"].toString(), reqData["sender_id"].toString())) {
                        is DataResult.Success -> {
                            when (addFriendToUser(reqData["sender_id"].toString(), reqData["target_id"].toString())) {
                                is DataResult.Failure -> DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                                is DataResult.Success -> DataResult.Success(true)
                            }
                        }
                        is DataResult.Failure -> {
                            DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
                        }
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

                when (addCollaborator(reqData["target_id"].toString(), reqData["log_id"].toString())) {
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
            val userUpdate = mapOf("friends.${friendId}" to FieldValue.delete())
            userRef.update(userUpdate).await()

            // Remove user from friend's map
            val friendRef = db.collection("users").document(friendId)
            val friendUpdate = mapOf("friends.${userId}" to FieldValue.delete())
            friendRef.update(friendUpdate).await()

            Log.d(tag, "Friend successfully removed!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error removing friend", e)
            DataResult.Failure(e)
        }
    }

    // TODO Ensure blocker is removed from logs involving this user (excluding ones he owns)
    //  Blocked user must also be removed from the logs that the user above owns
    suspend fun blockUser(userId: String, blockedId: String, isFriend: Boolean): DataResult<Boolean> {
        return try {
            // Add to blocked
            db.collection("users").document(userId)
                .update(mapOf("blocked.${blockedId}" to true)).await()

            // Remove from friends list (both)
            if (isFriend) {
                when(val removeUser = removeFriend(userId, blockedId)) {
                    is DataResult.Failure -> DataResult.Failure(removeUser.throwable)
                    is DataResult.Success -> {
                        when(val removeBlocked = removeFriend(blockedId, userId)) {
                            is DataResult.Failure -> DataResult.Failure(removeBlocked.throwable)
                            is DataResult.Success -> DataResult.Success(true)
                        }
                    }
                }
            }

            Log.d(tag, "User successfully blocked!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error updating user document", e)
            DataResult.Failure(e)
        }
    }

    suspend fun addFriendToUser(userId: String, friendId: String): DataResult<Boolean> {
        return try {
            val userRef = db.collection("users").document(userId)
            userRef.update("friends.$friendId", true).await()
            DataResult.Success(true)
        } catch (e: Exception) {
            println("Error updating user document $e")
            DataResult.Failure(e)
        }
    }

    suspend fun addCollaborator(userId: String, logId: String): DataResult<Boolean> {
        return try {
            val logRef = db.collection("logs").document(logId)

            val priority: Int

            when (val result = LogRepository(db, auth).getLogs(userId, true)) {
                is DataResult.Success -> {
                    priority = result.item.size

                    val updates = mapOf(
                        "collaborators" to FieldValue.arrayUnion(userId),
                        "order.$userId" to priority
                    )

                    // Add user as a collaborator
                    logRef.update(updates).await()

                    Log.d(tag, "User successfully added as a collaborator!")
                    DataResult.Success(true)
                }
                is DataResult.Failure -> throw result.throwable
            }
        } catch (e: Exception) {
            Log.w(tag, "Error reading log document", e)
            DataResult.Failure(FirebaseError(FirebaseExceptionType.FAILED_TRANSACTION))
        }
    }
}
