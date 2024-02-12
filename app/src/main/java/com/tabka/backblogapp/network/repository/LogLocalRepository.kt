package com.tabka.backblogapp.network.repository

import android.util.Log
import com.tabka.backblogapp.BackBlog
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.util.JsonUtility

class LogLocalRepository {
    private val tag = "LocalStorageLogsRepo"
    var jsonUtility: JsonUtility = JsonUtility(BackBlog.appContext!!)

    fun getLogs(): List<LogData> {
        return jsonUtility.readFromFile()
    }

    fun createLog(log: LogData) {
        jsonUtility.appendToFile(log)
    }

    fun reorderLogs(userLogsJson: List<LogData>) {
        val updatedLogs = userLogsJson.mapIndexed { index, logData ->
            val newPriority = (index + 1)
            val updatedOwner = logData.owner?.copy(priority = newPriority)
            logData.copy(owner = updatedOwner)
        }
        jsonUtility.overwriteJSON(updatedLogs)
    }

    fun getLogById(id: String): LogData? {
        return jsonUtility.readFromFile().find { it.logId == id }
    }

    fun getLogCount(): Int {
        return jsonUtility.readFromFile().size
    }

    fun clearLogs() {
        jsonUtility.deleteAllLogs()
    }

    fun updateLog(logId: String, updateData: Map<String, Any?>) {
        // Read all logs from the file
        val existingLogs = jsonUtility.readFromFile().toMutableList()

        // Find the index of the log with the specified logId
        val logIndex = existingLogs.indexOfFirst { it.logId == logId }
        if (logIndex == -1) {
            Log.d(tag, "Log with ID $logId not found.")
            return
        }

        // Get the current log data
        val currentLog = existingLogs[logIndex]
        Log.d(tag, "Old Log: $currentLog")

        // Prepare updated log data based on the provided updates
        val updatedLog = currentLog.copy(
            name = updateData["name"] as? String ?: currentLog.name,
            isVisible = updateData["is_visible"] as? Boolean ?: currentLog.isVisible,
            movieIds = updateData["movie_ids"] as? MutableList<String> ?: currentLog.movieIds,
            watchedIds = updateData["watched_ids"] as? MutableList<String> ?: currentLog.watchedIds
        )

        Log.d(tag, "New Log: $updatedLog")

        // Replace the old log data with the updated log data in the list
        existingLogs[logIndex] = updatedLog

        // Write the updated logs back to the JSON file
        jsonUtility.overwriteJSON(existingLogs)

        Log.d(tag, "Log with ID $logId has been updated.")
    }

    fun markMovie(logId: String, movieId: String, watched: Boolean) {
        if (watched) {
            // Get specific log
            val existingLogs = jsonUtility.readFromFile()
            val log = existingLogs.find { it.logId == logId }

            if (log == null) {
                return
            }

            Log.d(tag, "Log before the move: $log")

            val updatedMovieIds = log.movieIds ?: mutableListOf()
            updatedMovieIds.remove(movieId)

            val updatedWatchedIds = log.watchedIds ?: mutableListOf()
            updatedWatchedIds.add(movieId)

            val updatedLog = log.copy(movieIds = updatedMovieIds, watchedIds = updatedWatchedIds)
            val indexToUpdate = existingLogs.indexOf(log)
            existingLogs[indexToUpdate] = updatedLog

            jsonUtility.overwriteJSON(existingLogs)
            Log.d(tag, "Log after move: $updatedLog")
        } else {
            // Unmark as watched
            // Get all logs
            val existingLogs = jsonUtility.readFromFile()

            // Find specific log
            val log = existingLogs.find { it.logId == logId }!!

            val updatedMovieIds = log.movieIds ?: mutableListOf()
            updatedMovieIds.add(movieId)

            val updatedWatchedIds = log.watchedIds ?: mutableListOf()
            updatedWatchedIds.remove(movieId)

            val updatedLog = log.copy(movieIds = updatedMovieIds, watchedIds = updatedWatchedIds)
            Log.d(tag, "This is the updated log: $updatedLog")
            val indexToUpdate = existingLogs.indexOf(log)
            existingLogs[indexToUpdate] = updatedLog

            jsonUtility.overwriteJSON(existingLogs)
        }
    }
    
    fun addMovieToLog(logId: String, movieId: String) {
        // Find specific log
        val existingLogs = jsonUtility.readFromFile()
        val log = existingLogs.find { it.logId == logId }

        if (log == null) {
            return
        }

        // Add movieId to log
        val updatedMovieIds = log.movieIds ?: mutableListOf()
        updatedMovieIds.add(movieId)

        // Update log
        val updatedLog = log.copy(movieIds = updatedMovieIds)
        val indexToUpdate = existingLogs.indexOf(log)
        existingLogs[indexToUpdate] = updatedLog

        jsonUtility.overwriteJSON(existingLogs)
    }

    fun deleteLog(logId: String) {
        // Find specific log
        val existingLogs = jsonUtility.readFromFile()
        existingLogs.removeIf { it.logId == logId }

        jsonUtility.overwriteJSON(existingLogs)
    }

}