package com.tabka.backblogapp

import android.content.Context
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.util.JsonUtility
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class LogLocalRepositoryTest {

    @Mock
    private lateinit var mockJsonUtility: JsonUtility

    @Mock
    private lateinit var mockContext: Context

    private lateinit var logLocalRepository: LogLocalRepository

    @Before
    fun setUp() {
        BackBlog.appContext = mockContext
        logLocalRepository = LogLocalRepository()
        logLocalRepository.jsonUtility = mockJsonUtility
    }

    @Test
    fun testGetLogsShouldReturnLogsFromJsonUtility() {
        // Arrange
        val expectedLogs = listOf(
            LogData(logId = "fakeLogId", name = "Fake Log",
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                isVisible = true,
                owner = Owner("fakeUserId", 0),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId" to true),
                watchedIds = mapOf("fakeWatchedId" to true)),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId2" to true),
                watchedIds = mapOf("fakeWatchedId3" to true)))
        whenever(mockJsonUtility.readFromFile()).thenReturn(expectedLogs.toMutableList())

        // Act
        val actualLogs = logLocalRepository.getLogs()

        // Assert
        assertEquals(expectedLogs, actualLogs)
    }

    @Test
    fun testCreateLogShouldAppendToFile() {
        // Arrange
        val logData = LogData(logId = "fakeLogId", name = "Fake Log",
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                isVisible = true,
                owner = Owner("fakeUserId", 0),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId" to true),
                watchedIds = mapOf("fakeWatchedId" to true))

        // Act
        logLocalRepository.createLog(logData)

        // Assert
        verify(mockJsonUtility).appendToFile(logData)
    }

    @Test
    fun testAddMovieToLogShouldUpdateLogFile() {
        // Arrange
        val logId = "fakeLogId"
        val movieId = "someMovieId"
        val existingLogs = listOf(
            LogData(logId = "fakeLogId", name = "Fake Log",
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                isVisible = true,
                owner = Owner("fakeUserId", 0),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId" to true),
                watchedIds = mapOf("fakeWatchedId" to true)),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId2" to true),
                watchedIds = mapOf("fakeWatchedId3" to true)))
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.addMovieToLog(logId, movieId)

        // Assert
        verify(mockJsonUtility).overwriteJSON(any())
    }

    @Test
    fun testAddMovieToLogShouldNotUpdateLogFile() {
        // Arrange
        val logId = "fakeLogId5965469"
        val movieId = "someMovieId"
        val existingLogs = listOf(
            LogData(logId = "fakeLogId", name = "Fake Log",
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                isVisible = true,
                owner = Owner("fakeUserId", 0),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId" to true),
                watchedIds = mapOf("fakeWatchedId" to true)),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId2" to true),
                watchedIds = mapOf("fakeWatchedId3" to true)))
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.addMovieToLog(logId, movieId)

        // Assert
        verify(mockJsonUtility, times(0)).overwriteJSON(any())
    }

    @Test
    fun testClearLogsShouldDeleteLogs() {
        // Act
        logLocalRepository.clearLogs()

        // Assert
        verify(mockJsonUtility).deleteAllLogs()
    }

    @Test
    fun testReorderLogsShouldUpdateJsonUtility() {
        // Arrange
        val userLogsJson = listOf(
            LogData(logId = "fakeLogId", name = "Fake Log",
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                isVisible = true,
                owner = Owner("fakeUserId", 0),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId" to true),
                watchedIds = mapOf("fakeWatchedId" to true)),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId2" to true),
                watchedIds = mapOf("fakeWatchedId3" to true))
        )

        // Act
        logLocalRepository.reorderLogs(userLogsJson)

        // Assert
        verify(mockJsonUtility).overwriteJSON(any())
    }

    @Test
    fun testGetLogByIdShouldReturnLog() {
        // Arrange
        val logId = "fakeLogId"
        val expectedLog = LogData(logId = "fakeLogId", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mapOf(),
            movieIds = mapOf("fakeMovieId" to true),
            watchedIds = mapOf("fakeWatchedId" to true))

        whenever(mockJsonUtility.readFromFile()).thenReturn(listOf(expectedLog).toMutableList())

        // Act
        val actualLog = logLocalRepository.getLogById(logId)

        // Assert
        assert(expectedLog == actualLog)
    }

    @Test
    fun testGetLogByIdShouldReturnNull() {
        // Arrange
        val logId = "fakeLogId456"
        val expectedLog = LogData(logId = "fakeLogId", name = "Fake Log",
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            isVisible = true,
            owner = Owner("fakeUserId", 0),
            collaborators = mapOf(),
            movieIds = mapOf("fakeMovieId" to true),
            watchedIds = mapOf("fakeWatchedId" to true))

        whenever(mockJsonUtility.readFromFile()).thenReturn(listOf(expectedLog).toMutableList())

        // Act
        val actualLog = logLocalRepository.getLogById(logId)

        // Assert
        assert(expectedLog != actualLog)
    }

    @Test
    fun testGetLogCountShouldReturnCorrectCount() {
        // Arrange
        val logs = listOf(
            LogData(logId = "fakeLogId", name = "Fake Log",
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                isVisible = true,
                owner = Owner("fakeUserId", 0),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId" to true),
                watchedIds = mapOf("fakeWatchedId" to true)),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mapOf(),
                movieIds = mapOf("fakeMovieId2" to true),
                watchedIds = mapOf("fakeWatchedId3" to true))
        )

        whenever(mockJsonUtility.readFromFile()).thenReturn(logs.toMutableList())

        // Act
        val logCount = logLocalRepository.getLogCount()

        // Assert
        assert(logCount == logs.size)
    }
}