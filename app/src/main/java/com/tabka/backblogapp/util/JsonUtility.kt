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
        // Read existing content
        val existingLogs = readFromFile()

        // Add the new log to the list
        existingLogs.add(newLog)

        // Convert the updated list back to JSON string
        val updatedJson = Json.encodeToString(existingLogs)

        // Write the JSON string to the file
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
            // The content is a JSON array
            Json.decodeFromString(fileContents)
        } else if (fileContents.isNotEmpty()) {
            // The content is a single JSON object
            mutableListOf(Json.decodeFromString(fileContents))
        } else {
            // The file is empty
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
        // Read existing content
        val existingLogs = readFromFile()

        // Find the index of the log to delete
        val logIndex = existingLogs.indexOfFirst { it.logId == logToDelete.logId }

        // Check if the log was found
        if (logIndex != -1) {
            existingLogs.removeAt(logIndex)
            overwriteJSON(existingLogs)
        } else {
            Log.d("JSONUtility", "Failed to delete log.")
        }
    }

    fun deleteAllLogs() {
        // Clear the content of the JSON file
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write("".toByteArray())
        }
    }
}