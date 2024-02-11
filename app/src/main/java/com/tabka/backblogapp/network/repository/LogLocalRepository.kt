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

            val updatedMovieIds = log.movieIds!!.toMutableMap().apply {
                put(movieId, true)
            }

            val updatedWatchedIds = log.watchedIds!!.toMutableMap().apply {
                remove(movieId)
            }

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
}