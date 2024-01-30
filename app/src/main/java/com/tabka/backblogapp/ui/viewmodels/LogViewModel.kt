package com.tabka.backblogapp.ui.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.burnoutcrew.reorderable.ItemPosition
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class LogViewModel(): ViewModel() {
    private val TAG = "LogViewModel"
    private val localLogRepository = LogLocalRepository()

    private val _allLogs = MutableStateFlow<List<LogData>?>(emptyList())
    var allLogs = _allLogs.asStateFlow()

    init {
        loadLogs()
    }

    private fun loadLogs() {
        Log.d(TAG, "Load Logs")
        _allLogs.value = localLogRepository.getLogs()
       /* sortLogsByOwnerPriority()*/
    }

    fun onMove(from: Int, to: Int) {
        Log.d(TAG, "From: $from To: $to")
        _allLogs.value = _allLogs.value!!.toMutableList().apply {
            add(to, removeAt(from))
        }
        localLogRepository.reorderLogs(allLogs.value!!)
        Log.d(TAG, _allLogs.value.toString())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createLog(logName: String) {
        // Create an ID
        val id = UUID.randomUUID().toString()

        // Find the next priority
        val priority: Int = findMaxPriority() + 1
        Log.d(TAG, "Priority of new log: $priority")

        // Create the date
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)

        val log = LogData(
            logId =  id,
            name = logName,
            isVisible = false,
            movieIds = emptyMap(),
            watchedIds = emptyMap(),
            owner = mapOf("priority" to priority),
            collaborators = emptyMap(),
            creationDate = formattedDate,
            lastModifiedDate = formattedDate
        )
        Log.d(TAG, "Creating Log: $log")
        localLogRepository.createLog(log)
        loadLogs()
    }

/*    private fun sortLogsByOwnerPriority() {
        CoroutineScope(Dispatchers.IO).launch {
            allLogs.collect { logsList ->
                logsList?.let { list ->
                    val sortedList = list.sortedBy { logData ->
                        // Assuming the map has an integer value and a key "x"
                        logData.owner?.get("priority") as? Int ?: Int.MAX_VALUE
                    }
                }
            }
        }
    }*/

    private fun findMaxPriority(): Int {
        var maxPriority = 0
        _allLogs.value?.forEach { log ->
            val temp = log.owner?.get("priority") as Double
            val tempInt = temp.toInt()

            if (tempInt > maxPriority) {
                maxPriority = tempInt
            }
/*            val temp = log.owner?.get("priority")

            if (temp is Int) { // Check if it's already an Int
                if (temp > maxPriority) {
                    maxPriority = temp
                }
            } else if (temp is Double) { // If it's a Double, convert it to Int
                val intTemp = temp.toInt()
                if (intTemp > maxPriority) {
                    maxPriority = intTemp
                }
            }*/
/*           if (temp > maxPriority) {
                maxPriority = temp
            }*/
        }
        return maxPriority
    }
}