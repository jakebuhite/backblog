package com.tabka.backblogapp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tabka.backblogapp.models.LogData
import com.tabka.backblogapp.util.JsonUtility

class LogLocalRepository {
    private val tag = "LocalStorageLogsRepo"
    private lateinit var jsonUtility: JsonUtility

    private val userLogsList = MutableLiveData<List<LogData>>()

    fun init(context: Context) {
        jsonUtility = JsonUtility(context)
    }

    fun getLogs(): LiveData<List<LogData>> {
        val logs = jsonUtility.readFromFile()
        userLogsList.value = logs

        Log.d(tag, logs.toString())
        return userLogsList
    }

    fun addLog(log: LogData) {
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