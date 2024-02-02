package com.tabka.backblogapp.util

import android.content.Context
import android.util.Log
import com.tabka.backblogapp.network.models.LogData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

class JsonUtility(private val context: Context) {
    private val fileName = "logs.json"

    fun appendToFile(newLog: LogData) {
        val existingLogs = readFromFile()
        existingLogs.add(newLog)

        val updatedJson = Json.encodeToString(existingLogs)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(updatedJson.toByteArray())
        }
    }

    fun readFromFile(): MutableList<LogData> {
        val fileContents = try {
            context.openFileInput(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            return mutableListOf()
        }

        return if (fileContents.startsWith("[")) {
            Json.decodeFromString(fileContents)
        } else if (fileContents.isNotEmpty()) {
            mutableListOf(Json.decodeFromString(fileContents))
        } else {
            mutableListOf()
        }
    }

    fun overwriteJSON(logs: List<LogData>) {
        val json = Json.encodeToString(logs)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    fun deleteLog(logToDelete: LogData) {
        val existingLogs = readFromFile()

        // Find the index of the log to delete
        val logIndex = existingLogs.indexOfFirst { it.logId == logToDelete.logId }
        if (logIndex != -1) {
            existingLogs.removeAt(logIndex)
            overwriteJSON(existingLogs)
        } else {
            Log.d("JSONUtility", "Failed to delete log.")
        }
    }

    fun deleteAllLogs() {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write("".toByteArray())
        }
    }
}