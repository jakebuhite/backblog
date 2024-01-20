package com.tabka.backblogapp.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.models.LogData

class LogRepository {

    private val db = Firebase.firestore
    private val tag = "FriendsRepo"

    fun addLog(name: String, isVisible: Boolean, ownerId: String) {
        // Get all log data
        val logData = mapOf(
            "name" to name,
            "creation_date" to System.currentTimeMillis().toString(),
            "last_modified_date" to System.currentTimeMillis().toString(),
            "is_visible" to isVisible,
            "owner" to mapOf("user_id" to ownerId, "priority" to 0),
            "collaborators" to emptyMap<String, Map<String, Int>>(),
            "movie_ids" to emptyMap<String, Boolean>(),
            "watched_ids" to emptyMap<String, Boolean>()
        )

        db.collection("logs").add(logData)
            .addOnSuccessListener { Log.d(tag, "Log successfully written!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing log document", e) }
    }

    // TODO - Append movie data, Add half sheet
    fun getLog(logId: String): LogData? {
        var logData: LogData? = null
        db.collection("logs").document(logId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    Log.d(tag, "Log successfully written!")
                    @Suppress("UNCHECKED_CAST")
                    logData = LogData(
                        logId = doc.getString("log_id"),
                        name = doc.getString("name"),
                        creationDate = doc.getString("creation_date"),
                        lastModifiedDate = doc.getString("last_modified_date"),
                        status = doc.getString("status"),
                        owner = doc.data?.get("owner") as Map<String, Any>?,
                        collaborators = doc.data?.get("collaborators") as Map<String, Map<String, Int>>?,
                        movieIds = doc.data?.get("movie_ids") as Map<String, Boolean>?,
                        watchedIds = doc.data?.get("watched_ids") as Map<String, Boolean>?
                    )
                } else {
                    Log.d(tag, "Log not found.")
                }
            }
            .addOnFailureListener { e -> Log.w(tag, "Error reading log", e) }

        return logData
    }

    // TODO - Add half sheet
    fun getLogs(userId: String): List<LogData> {
        // Query logs based on the owner_id field
        val logRef = db.collection("logs")

        val logs: MutableList<LogData> = mutableListOf()

        // Query for user-owned logs
        logRef.whereEqualTo("owner.user_id", userId).get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    logs.addAll(it.documents.map { doc ->
                        @Suppress("UNCHECKED_CAST")
                        LogData(
                            logId = doc.getString("log_id"),
                            name = doc.getString("name"),
                            creationDate = doc.getString("creation_date"),
                            lastModifiedDate = doc.getString("last_modified_date"),
                            status = doc.getString("status"),
                            owner = doc.data?.get("owner") as Map<String, Any>?,
                            collaborators = doc.data?.get("collaborators") as Map<String, Map<String, Int>>?,
                            movieIds = doc.data?.get("movie_ids") as Map<String, Boolean>?,
                            watchedIds = doc.data?.get("watched_ids") as Map<String, Boolean>?
                        )
                    })
                }
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving logs document", e) }

        // Query for user in collaborators
        logRef.orderBy("collaborators.${userId}").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    logs.addAll(it.documents.map { doc ->
                        @Suppress("UNCHECKED_CAST")
                        LogData(
                            logId = doc.getString("log_id"),
                            name = doc.getString("name"),
                            creationDate = doc.getString("creation_date"),
                            lastModifiedDate = doc.getString("last_modified_date"),
                            status = doc.getString("status"),
                            owner = doc.data?.get("owner") as Map<String, Any>?,
                            collaborators = doc.data?.get("collaborators") as Map<String, Map<String, Int>>?,
                            movieIds = doc.data?.get("movie_ids") as Map<String, Boolean>?,
                            watchedIds = doc.data?.get("watched_ids") as Map<String, Boolean>?
                        )
                    })
                }
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving logs document", e) }

        return logs
    }

    fun updateLog(logId: String, updateData: Map<String, Any?>) {
        val updatedLogObj = mutableMapOf<String, Any>()

        // Add the modified properties to updatedUserObj
        updateData["name"]?.let { updatedLogObj["name"] = it }
        updateData["status"]?.let { updatedLogObj["status"] = it }
        updateData["movie_ids"]?.let { updatedLogObj["movie_ids"] = it }
        updateData["watched_ids"]?.let { updatedLogObj["watched_ids"] = it }

        if (updatedLogObj.isNotEmpty()) {
            // Update Firestore user document
            updatedLogObj["last_modified_date"] = System.currentTimeMillis().toString()
            db.collection("logs").document(logId).update(updatedLogObj)
                .addOnSuccessListener { Log.d(tag, "Log successfully updated!") }
                .addOnFailureListener { e -> Log.w(tag, "Error updating log document", e) }
        }
    }

    fun updateLogsBatch(logs: List<LogData>) {
        val batch = db.batch()

        // Iterate through each log in the array
        logs.forEach { log ->
            val updatedLogObj = mutableMapOf<String, Any>()

            // Add the modified properties to updatedUserObj
            log.name?.let { updatedLogObj["name"] = it }
            log.status?.let { updatedLogObj["status"] = it }
            log.movieIds?.let { updatedLogObj["movie_ids"] = it }
            log.watchedIds?.let { updatedLogObj["watched_ids"] = it }

            // Update log in the batch
            if (updatedLogObj.isNotEmpty()) {
                // Update Firestore user document
                updatedLogObj["last_modified_date"] = System.currentTimeMillis().toString()
                batch.update(db.collection("logs").document(log.logId!!), updatedLogObj)
            }
        }

        // Commit the batch
        batch.commit()
            .addOnSuccessListener { Log.d(tag, "Logs successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error batch updating log documents", e) }
    }

    fun deleteLog(logId: String) {
        // Delete log
        db.collection("logs").document(logId).delete()
            .addOnSuccessListener { Log.d(tag, "Log successfully deleted!") }
            .addOnFailureListener { e -> Log.w(tag, "Error deleting log", e) }
    }

    // TODO - Get first movie id, add half sheet
    fun getPublicLogs(userId: String): List<LogData> {
        val logRef = db.collection("logs")
        val logs: MutableList<LogData> = mutableListOf()

        // Query for user-owned logs
        logRef.whereEqualTo("owner.user_id", userId).whereEqualTo("status", "PUBLIC").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    logs.addAll(it.documents.map { doc ->
                        @Suppress("UNCHECKED_CAST")
                        LogData(
                            logId = doc.getString("log_id"),
                            name = doc.getString("name"),
                            creationDate = doc.getString("creation_date"),
                            lastModifiedDate = doc.getString("last_modified_date"),
                            status = doc.getString("status"),
                            owner = doc.data?.get("owner") as Map<String, Any>?,
                            collaborators = doc.data?.get("collaborators") as Map<String, Map<String, Int>>?,
                            movieIds = doc.data?.get("movie_ids") as Map<String, Boolean>?,
                            watchedIds = doc.data?.get("watched_ids") as Map<String, Boolean>?
                        )
                    })
                }
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving logs document", e) }

        // Query for user in collaborators
        logRef.orderBy("collaborators.${userId}").whereEqualTo("status", "PUBLIC").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    logs.addAll(it.documents.map { doc ->
                        @Suppress("UNCHECKED_CAST")
                        LogData(
                            logId = doc.getString("log_id"),
                            name = doc.getString("name"),
                            creationDate = doc.getString("creation_date"),
                            lastModifiedDate = doc.getString("last_modified_date"),
                            status = doc.getString("status"),
                            owner = doc.data?.get("owner") as Map<String, Any>?,
                            collaborators = doc.data?.get("collaborators") as Map<String, Map<String, Int>>?,
                            movieIds = doc.data?.get("movie_ids") as Map<String, Boolean>?,
                            watchedIds = doc.data?.get("watched_ids") as Map<String, Boolean>?
                        )
                    })
                }
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving logs document", e) }

        return logs
    }

    fun updateUserLogOrder(userId: String, logIds: List<Pair<String, Boolean>>) {
        val batch = db.batch()

        // Iterate through each log_id in the array
        logIds.forEachIndexed { index, log ->
            // Update log priority in the batch
            // Boolean represents whether user owns this log
            val logRef = db.collection("logs").document(log.first)
            if (log.second) {
                batch.update(logRef, mapOf("owner.priority" to index))
            } else {
                batch.update(logRef, mapOf("collaborators.$userId.priority" to index))
            }
        }

        // Commit the batch
        batch.commit()
            .addOnSuccessListener { Log.d(tag, "Log order successfully written!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing log order", e) }
    }

    fun addCollaborators(logId: String, collaborators: List<String>) {
        val logRef = db.collection("logs").document(logId)

        // Get log data
        var logData: LogData? = null
        logRef.get()
            .addOnSuccessListener { doc ->
                Log.d(tag, "Log successfully received!")
                @Suppress("UNCHECKED_CAST")
                logData = LogData(
                    logId = doc.getString("log_id"),
                    name = doc.getString("name"),
                    creationDate = doc.getString("creation_date"),
                    lastModifiedDate = doc.getString("last_modified_date"),
                    status = doc.getString("status"),
                    owner = doc.data?.get("owner") as Map<String, Any>?,
                    collaborators = doc.data?.get("collaborators") as Map<String, Map<String, Int>>?,
                    movieIds = doc.data?.get("movie_ids") as Map<String, Boolean>?,
                    watchedIds = doc.data?.get("watched_ids") as Map<String, Boolean>?
                )
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving log", e) }

        if (logData == null) {
            return
        }

        val collabs = mutableMapOf<String, Map<String, Int>>()

        // Iterate through each collaborator in the array
        collaborators.forEach { collaborator ->
            // Check if the user is already a collaborator
            if (logData!!.collaborators?.get(collaborator) != null) {
                Log.w(tag, "User is already a collaborator for this log.")
            } else {
                // Add collaborator to the updatedCollaborators object
                collabs[collaborator] = mapOf("priority" to 0)
            }
        }

        // Add collaborators to the log
        logRef.update(mapOf("collaborators" to FieldValue.arrayUnion(collabs)))
            .addOnSuccessListener { Log.d(tag, "Collaborators successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error updating collaborators", e) }
    }

    fun removeCollaborators(logId: String, collaborators: List<String>) {
        val logRef = db.collection("logs").document(logId)

        val collabs = mutableMapOf<String, Any>()

        // Iterate through each collaborator in the array
        collaborators.forEach { collaborator ->
            // Remove collaborator from the updatedCollaborators object
            collabs["collaborators.${collaborator}"] = FieldValue.delete()
        }

        // Remove collaborators from the log
        logRef.update(collabs)
            .addOnSuccessListener { Log.d(tag, "Collaborators successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error updating collaborators", e) }
    }
}