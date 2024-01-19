package com.tabka.backblogapp.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tabka.backblogapp.models.LogData
import java.io.IOException
import java.lang.reflect.Type

class JsonUtility(private val context: Context) {

    private val fileName = "logs.json"
    fun appendToFile(newLog: LogData) {
        // Read existing content
        val existingLogs = readFromFile()

        // Add the new log to the list
        existingLogs.add(newLog)

        // Convert the updated list back to JSON string
        val updatedJson = Gson().toJson(existingLogs)

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
            val listType: Type = object : TypeToken<MutableList<LogData>>() {}.type
            Gson().fromJson(fileContents, listType) ?: mutableListOf()
        } else if (fileContents.isNotEmpty()) {
            // The content is a single JSON object
            mutableListOf(Gson().fromJson(fileContents, LogData::class.java))
        } else {
            // The file is empty
            mutableListOf()
        }
    }

    fun overwriteJSON(logs: List<LogData>) {
        val json = Gson().toJson(logs)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

}