package com.tabka.backblogapp.network.repository

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
    
    fun addMovieToLog(logId: String, movieId: String) {
        // Get all logs
        val existingLogs = jsonUtility.readFromFile()

        // Find specific log
        val log = existingLogs.find { it.logId == logId }

        if (log == null) {
            return
        }

        // Add movieId to log
        val updatedMovieIds = log.movieIds!!.toMutableMap()
        updatedMovieIds[movieId] = true

        // Update Log accordingly
        val updatedLog = log.copy(movieIds = updatedMovieIds)
        val indexToUpdate = existingLogs.indexOf(log)
        existingLogs[indexToUpdate] = updatedLog

        jsonUtility.overwriteJSON(existingLogs)
    }
}