package com.tabka.backblogapp.ui.screens

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.tabka.backblogapp.models.LogData
import com.tabka.backblogapp.repository.LogLocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class LogViewModel(): ViewModel() {
    private val TAG = "LogViewModel"
    private val localRepository = LogLocalRepository()

    private val _allLogs = MutableStateFlow<List<LogData>?>(emptyList())
    val allLogs = _allLogs.asStateFlow()

    init {
        loadLogs()
    }

    private fun loadLogs() {
        Log.d(TAG, "Load Logs")
        _allLogs.value = localRepository.getLogs()
        Log.d(TAG, "All logs:\n${_allLogs.value.toString()}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createLog(logName: String) {
        // Create an ID
        val id = UUID.randomUUID().toString()

        // Find the next priority
        val priority = (allLogs.value?.maxByOrNull { it?.priority ?: 0 }?.priority ?: 0) + 1

        // Create the date
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)

        val log = LogData(
            logId =  id,
            name = logName,
            priority = priority,
            status = null,
            movieIds = null,
            watchedIds = null,
            owner = null,
            collaborators = null,
            creationDate = formattedDate,
            lastModifiedDate = formattedDate
        )
        localRepository.createLog(log)
        loadLogs()
    }
}