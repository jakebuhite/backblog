package com.tabka.backblogapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tabka.backblogapp.BackBlog
import com.tabka.backblogapp.models.LogData
import com.tabka.backblogapp.util.JsonUtility

class LogLocalRepository {
    private val tag = "LocalStorageLogsRepo"
    private var jsonUtility: JsonUtility = JsonUtility(BackBlog.appContext!!)

    fun getLogs(): List<LogData> {
        return jsonUtility.readFromFile()
    }

    fun createLog(log: LogData) {
        jsonUtility.appendToFile(log)
    }

    fun reorderLogs(userLogsJson: List<LogData>) {
        jsonUtility.overwriteJSON(userLogsJson)
    }

    fun getLogById(id: String): LogData? {
        return jsonUtility.readFromFile().find { it.logId == id }
    }

    fun addMovieToLog(movieId: Int, logId: String) {
        // Get all logs
        val existingLogs = jsonUtility.readFromFile()

        // Find specific log
        val log = existingLogs.find { it.logId == logId }!!

        // Add movieId to log
        val updatedMovieIds = log.movieIds!!.toMutableMap()
        updatedMovieIds[movieId.toString()] = true

        // Update Log accordingly
        val updatedLog = log.copy(movieIds = updatedMovieIds)
        val indexToUpdate = existingLogs.indexOf(log)
        existingLogs[indexToUpdate] = updatedLog

        jsonUtility.overwriteJSON(existingLogs)
    }
}