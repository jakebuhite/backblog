package com.tabka.backblogapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.util.JsonUtility
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class JsonUtilityTest {

    private lateinit var jsonUtility: JsonUtility
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
        jsonUtility = JsonUtility(context)
    }

    @After
    fun tearDown() {
        val file = File(context.filesDir, "logs.json")
        file.delete()
    }

    @Test
    fun testAppendToFileShouldAddLog() {
        // Arrange
        val logData = LogData(logId = "fakeLogId", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )

        // Act
        jsonUtility.appendToFile(logData)

        // Assert
        val logsFromFile = jsonUtility.readFromFile()
        assertEquals(1, logsFromFile.size)
        assertEquals(logData, logsFromFile.first())
    }

    @Test
    fun testReadFromFileShouldReturnEmptyList() {
        // Act
        val logs = jsonUtility.readFromFile()

        // Assert
        assertEquals(0, logs.size)
    }

    @Test
    fun testReadFromFileShouldReturnList() {
        // Arrange
        val log1 = LogData(logId = "logId1", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val jsonLog1 = Json.encodeToString(log1)
        context.openFileOutput("logs.json", Context.MODE_PRIVATE).use {
            it.write(jsonLog1.toByteArray())
        }

        // Act
        val logs = jsonUtility.readFromFile()

        // Assert
        assertEquals(1, logs.size)
        assertEquals(log1, logs[0])
    }

    @Test
    fun testOverwriteJSONShouldWriteLogs() {
        // Arrange
        val log1 = LogData(logId = "logId1", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val log2 = LogData(logId = "logId2", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val logs = listOf(log1, log2)

        // Act
        jsonUtility.overwriteJSON(logs)

        // Assert
        val logsFromFile = jsonUtility.readFromFile()
        assertEquals(logs, logsFromFile)
    }

    @Test
    fun testDeleteLogShouldRemoveLog() {
        // Arrange
        val log1 = LogData(logId = "logId1", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val log2 = LogData(logId = "logId2", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val logs = mutableListOf(log1, log2)
        jsonUtility.overwriteJSON(logs)

        // Act
        jsonUtility.deleteLog(log1)

        // Assert
        val updatedLogs = jsonUtility.readFromFile()
        assertEquals(1, updatedLogs.size)
        assertEquals(log2, updatedLogs.first())
    }

    @Test
    fun testDeleteLogShouldNotRemoveAnythingIfLogNotFound() {
        // Arrange
        val log1 = LogData(logId = "logId1", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val log2 = LogData(logId = "logId2", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val logs = mutableListOf(log1, log2)
        jsonUtility.overwriteJSON(logs)

        // Act
        jsonUtility.deleteLog(LogData(logId = "nonExistentLog", name = "Fake Log",
        creationDate = "2024-02-04",
        lastModifiedDate = "2024-02-04",
        isVisible = true,
        owner = Owner("fakeUserId", 0),
        collaborators = mutableListOf(),
        order = emptyMap(),
        movieIds = mutableListOf("fakeMovieId"),
        watchedIds = mutableListOf("fakeWatchedId")
        ))

        // Assert
        val updatedLogs = jsonUtility.readFromFile()
        assertEquals(2, updatedLogs.size)
        assertEquals(logs, updatedLogs)
    }

    @Test
    fun testDeleteAllLogsShouldRemoveAllLogs() {
        // Arrange
        val log1 = LogData(logId = "logId1", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val log2 = LogData(logId = "logId2", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val logs = mutableListOf(log1, log2)
        jsonUtility.overwriteJSON(logs)

        // Act
        jsonUtility.deleteAllLogs()

        // Assert
        val updatedLogs = jsonUtility.readFromFile()
        assertEquals(0, updatedLogs.size)
    }

    @Test
    fun testJsonSerializationAndDeserialization() {
        // Arrange
        val log1 = LogData(logId = "logId1", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val log2 = LogData(logId = "logId2", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )
        val logs = listOf(log1, log2)

        // Act
        val jsonLogs = Json.encodeToString(logs)
        val decodedLogs = Json.decodeFromString<List<LogData>>(jsonLogs)

        // Assert
        assertEquals(logs, decodedLogs)
    }
}