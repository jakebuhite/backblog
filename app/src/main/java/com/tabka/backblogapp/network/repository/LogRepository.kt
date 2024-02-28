package com.tabka.backblogapp.network.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.FirebaseError
import com.tabka.backblogapp.util.FirebaseExceptionType
import com.tabka.backblogapp.util.toJsonElement
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class LogRepository(val db: FirebaseFirestore = Firebase.firestore, val auth: FirebaseAuth = Firebase.auth) {
    private val tag = "LogRepo"

    suspend fun addLog(name: String, isVisible: Boolean, ownerId: String): DataResult<String> {
        return try {
            // Get new log id
            val logRef = db.collection("logs").document()
            val logId = logRef.id

            when (val result = getLogs(ownerId, true)) {
                is DataResult.Success ->  {
                    val priority = result.item.size

                    // Get all log data
                    val logData = mapOf(
                        "log_id" to logId,
                        "name" to name,
                        "creation_date" to System.currentTimeMillis().toString(),
                        "last_modified_date" to System.currentTimeMillis().toString(),
                        "is_visible" to isVisible,
                        "owner" to mapOf("user_id" to ownerId, "priority" to priority),
                        "collaborators" to mutableListOf<String>(),
                        "order" to emptyMap<String, Int>(),
                        "movie_ids" to mutableListOf<String>(),
                        "watched_ids" to mutableListOf<String>()
                    )

                    db.collection("logs").document(logId).set(logData).await()

                    Log.d(tag, "Log successfully written!")
                    DataResult.Success(logId)
                }
                is DataResult.Failure -> DataResult.Failure(result.throwable)
            }
        } catch (e: Exception) {
            Log.w(tag, "Error writing log document", e)
            DataResult.Failure(e)
        }
    }

    // For syncing local logs
    suspend fun addLog(name: String, ownerId: String, priority: Int, creationDate: String, movieIds: MutableList<String>, watchedIds: MutableList<String>): DataResult<Boolean> {
        return try {
            // Get new log id
            val logRef = db.collection("logs").document()
            val logId = logRef.id

            // Get all log data
            val logData = mapOf(
                "log_id" to logId,
                "name" to name,
                "creation_date" to creationDate,
                "last_modified_date" to System.currentTimeMillis().toString(),
                "is_visible" to false, // Hiding logs default
                "owner" to mapOf("user_id" to ownerId, "priority" to priority),
                "collaborators" to mutableListOf<String>(),
                "order" to emptyMap<String, Int>(),
                "movie_ids" to movieIds,
                "watched_ids" to watchedIds
            )

            db.collection("logs").document(logId).set(logData).await()

            Log.d(tag, "Log successfully written!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error writing log document", e)
            DataResult.Failure(e)
        }
    }

    suspend fun getLog(logId: String): DataResult<LogData> {
        return try {
            val doc = db.collection("logs").document(logId).get().await()
            if (doc.exists()) {
                Log.d(tag, "Log successfully written!")
                val docData = doc.data
                DataResult.Success(Json.decodeFromString(Json.encodeToString(docData.toJsonElement())))
            } else {
                Log.d(tag, "Log not found.")
                DataResult.Failure(FirebaseError(FirebaseExceptionType.NOT_FOUND))
            }
        } catch (e: Exception) {
            Log.w(tag, "Error reading log", e)
            DataResult.Failure(e)
        }
    }

    suspend fun getLogs(userId: String, private: Boolean): DataResult<List<LogData>> {
        Log.d(tag, "Getting logs")
        return try {
            coroutineScope {
                // Query logs based on the owner_id field
                val logRef = db.collection("logs")
                val logs: MutableList<LogData> = mutableListOf()

                val userOwned = async {
                    try {
                        var q: Query = logRef.whereEqualTo("owner.user_id", userId)
                        if (!private) {
                            q = logRef.whereEqualTo("is_visible", true)
                        }
                        val snapshot = q.get().await()
                        logs.addAll(snapshot.documents.map { doc -> Json.decodeFromString(Json.encodeToString(doc.data.toJsonElement())) })
                    } catch (e: Exception) {
                        Log.w(tag, "Error receiving logs document (userOwned)", e)
                        return@async DataResult.Failure(e)
                    }
                }

                val userCollab = async {
                    try {
                        var q: Query = logRef.whereArrayContains("collaborators", userId)
                        if (!private) {
                            q = logRef.whereEqualTo("is_visible", true)
                        }
                        val snapshot = q.get().await()
                        logs.addAll(snapshot.documents.map { doc ->
                            Json.decodeFromString(Json.encodeToString(doc.data.toJsonElement()))
                        })
                    } catch (e: Exception) {
                        Log.w(tag, "Error receiving logs document (userCollab)", e)
                        return@async DataResult.Failure(e)
                    }
                }

                userOwned.await()
                userCollab.await()

                // Sort the logs based on priority
                logs.sortBy { log ->
                    if (log.owner?.userId == userId) {
                        log.owner.priority ?: 0
                    } else {
                        log.order?.get(userId) ?: 0
                    }
                }

                DataResult.Success(logs)
            }
        } catch (e: Exception) {
            Log.w(tag, "Error fetching logs", e)
            DataResult.Failure(e)
        }
    }

    suspend fun updateLog(logId: String, updateData: Map<String, Any?>): DataResult<Boolean> {
        Log.d(tag, "Update Log: $updateData")
        try {
            val logRef = db.collection("logs").document(logId)
            val updatedLogObj = mutableMapOf<String, Any>()

            // Add the modified properties to updatedUserObj
            updateData["name"]?.let { updatedLogObj["name"] = it }
            updateData["is_visible"]?.let { updatedLogObj["is_visible"] = it }
            updateData["movie_ids"]?.let { updatedLogObj["movie_ids"] = it }
            updateData["watched_ids"]?.let { updatedLogObj["watched_ids"] = it }

            if (updatedLogObj.isNotEmpty()) {
                // Update Firestore user document
                updatedLogObj["last_modified_date"] = System.currentTimeMillis().toString()
                logRef.update(updatedLogObj).await()
            }

            Log.d(tag, "Log successfully updated!")
            return DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error updating log", e)
            return DataResult.Failure(FirebaseError(FirebaseExceptionType.FAILED_TRANSACTION))
        }
    }

    suspend fun deleteLog(logId: String): DataResult<Boolean> {
        return try {
            db.collection("logs").document(logId).delete().await()
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error deleting log", e)
            DataResult.Failure(e)
        }
    }


    suspend fun updateUserLogOrder(userId: String, logIds: List<Pair<String, Boolean>>): DataResult<Boolean> {
        return try {
            logIds.mapIndexed { index, log ->
                val logRef = db.collection("logs").document(log.first)
                val priorityField = if (log.second) "owner.priority" else "order.$userId"
                logRef.update(priorityField, index).await()
            }

            Log.d(tag, "Log order successfully updated!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error updating log order", e)
            DataResult.Failure(e)
        }
    }


    suspend fun addCollaborators(logId: String, collaborators: List<String>): DataResult<Boolean> {
        return try {
            if (auth.currentUser?.uid == null) {
                return DataResult.Failure(FirebaseError(FirebaseExceptionType.FAILED_TRANSACTION))
            }

            // Iterate through each collaborator in the array
            collaborators.forEach { collaborator ->
                // Add collaborator to the updatedCollaborators object
                val result = FriendRepository(db, auth).addLogRequest(auth.currentUser?.uid!!, collaborator, logId, System.currentTimeMillis().toString())
                when (result) {
                    is DataResult.Success -> Unit
                    is DataResult.Failure -> {
                        Log.d(tag, "Error sending log request: ${result.throwable.message}")
                        return DataResult.Failure(result.throwable)
                    }
                }
            }

            Log.d(tag, "Log requests sent!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error updating collaborators", e)
            DataResult.Failure(e)
        }
    }


    suspend fun removeCollaborators(logId: String, collaborators: List<String>): DataResult<Boolean> {
        return try {
            Log.d(tag, "Collabs to remove: $collaborators")
            val logRef = db.collection("logs").document(logId)
            val updates = mutableMapOf<String, Any>()

            // Iterate through each collaborator in the array
            collaborators.forEach { collaborator ->
                // Remove collaborator from the updatedCollaborators object
                updates["order.${collaborator}"] = FieldValue.delete()
            }

            updates["collaborators"] = FieldValue.arrayRemove(*collaborators.toTypedArray())

            // Remove collaborators from the log
            logRef.update(updates).await()

            Log.d(tag, "after removal: $updates")
            Log.d(tag, "Collaborators successfully updated!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error updating collaborators", e)
            DataResult.Failure(e)
        }
    }
}