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
import org.mockito.kotlin.eq
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
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId"),
                watchedIds = mutableListOf("fakeWatchedId")),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId2"),
                watchedIds = mutableListOf("fakeWatchedId3")
            ))
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
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId"),
                watchedIds = mutableListOf("fakeWatchedId")
        )

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
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId"),
                watchedIds = mutableListOf("fakeWatchedId")
            ),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId2"),
                watchedIds = mutableListOf("fakeWatchedId3")
            ))
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.addMovieToLog(logId, movieId)

        // Assert
        verify(mockJsonUtility).overwriteJSON(any())
    }

    @Test
    fun testAddMovieToLogWithNoMovieIdsShouldUpdateLogFile() {
        // Arrange
        val logId = "fakeLogId"
        val movieId = "someMovieId"
        val existingLogs = listOf(
            LogData(logId = "fakeLogId", name = "Fake Log",
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                isVisible = true,
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = null,
                watchedIds = mutableListOf("fakeWatchedId")
            ),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId2"),
                watchedIds = mutableListOf("fakeWatchedId3")
            ))
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
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId"),
                watchedIds = mutableListOf("fakeWatchedId")
            ),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId2"),
                watchedIds = mutableListOf("fakeWatchedId3")
            ))
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
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId"),
                watchedIds = mutableListOf("fakeWatchedId")
            ),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId2"),
                watchedIds = mutableListOf("fakeWatchedId3")
            )
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
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )

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
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("fakeMovieId"),
            watchedIds = mutableListOf("fakeWatchedId")
        )

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
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId"),
                watchedIds = mutableListOf("fakeWatchedId")
            ),
            LogData(logId = "fakeLogId2", name = "Fake Log 2",
                creationDate = "2024-02-05",
                lastModifiedDate = "2024-02-05",
                isVisible = true,
                owner = Owner("fakeUserId", 1),
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf("fakeMovieId2"),
                watchedIds = mutableListOf("fakeWatchedId3")
            )
        )

        whenever(mockJsonUtility.readFromFile()).thenReturn(logs.toMutableList())

        // Act
        val logCount = logLocalRepository.getLogCount()

        // Assert
        assert(logCount == logs.size)
    }

    @Test
    fun testUpdateLogWithValidIdShouldUpdateLog() {
        // Arrange
        val logId = "fakeLogId"
        val updateData = mapOf(
            "name" to "Updated Log Name",
            "is_visible" to false,
            "movie_ids" to mutableListOf("movieId1", "movieId2"),
            "watched_ids" to mutableListOf("watchedId1", "watchedId2")
        )
        val existingLogs = listOf(
            LogData(
                logId = logId,
                name = "Original Log Name",
                isVisible = true,
                movieIds = mutableListOf("oldMovieId1", "oldMovieId2"),
                watchedIds = mutableListOf("oldWatchedId1", "oldWatchedId2"),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            )
        )
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.updateLog(logId, updateData)

        // Assert
        val expectedUpdatedLog = LogData(
            logId = logId,
            name = "Updated Log Name",
            isVisible = false,
            movieIds = mutableListOf("movieId1", "movieId2"),
            watchedIds = mutableListOf("watchedId1", "watchedId2"),
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap()
        )
        verify(mockJsonUtility).overwriteJSON(any())
        verify(mockJsonUtility).readFromFile()
        verify(mockJsonUtility).overwriteJSON(eq(listOf(expectedUpdatedLog).toMutableList()))
    }

    @Test
    fun testUpdateLogWithInvalidIdShouldNotUpdateLog() {
        // Arrange
        val logId = "nonExistentLogId"
        val updateData = mapOf(
            "name" to "Updated Log Name",
            "is_visible" to false,
            "movie_ids" to mutableListOf("movieId1", "movieId2"),
            "watched_ids" to mutableListOf("watchedId1", "watchedId2")
        )
        val existingLogs = listOf(
            LogData(
                logId = "existingLogId",
                name = "Original Log Name",
                isVisible = true,
                movieIds = mutableListOf("oldMovieId1", "oldMovieId2"),
                watchedIds = mutableListOf("oldWatchedId1", "oldWatchedId2"),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            )
        )
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.updateLog(logId, updateData)

        // Assert
        verify(mockJsonUtility).readFromFile()
    }

    @Test
    fun testUpdateLogWithPartialDataShouldUpdatePartially() {
        // Arrange
        val logId = "fakeLogId"
        val updateData = mapOf(
            "name" to "Updated Log Name",
            "is_visible" to false
        )
        val existingLogs = listOf(
            LogData(
                logId = logId,
                name = "Original Log Name",
                isVisible = true,
                movieIds = mutableListOf("oldMovieId1", "oldMovieId2"),
                watchedIds = mutableListOf("oldWatchedId1", "oldWatchedId2"),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            )
        )
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.updateLog(logId, updateData)

        // Assert
        val expectedUpdatedLog = LogData(
            logId = logId,
            name = "Updated Log Name",
            isVisible = false,  // Updated
            movieIds = mutableListOf("oldMovieId1", "oldMovieId2"),
            watchedIds = mutableListOf("oldWatchedId1", "oldWatchedId2"),
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap()
        )
        verify(mockJsonUtility).overwriteJSON(any())
        verify(mockJsonUtility).readFromFile()
        verify(mockJsonUtility).overwriteJSON(eq(listOf(expectedUpdatedLog).toMutableList()))
    }

    @Test
    fun testUpdateLogWithEmptyDataShouldNotUpdate() {
        // Arrange
        val logId = "fakeLogId"
        val updateData = emptyMap<String, Any?>()
        val existingLogs = listOf(
            LogData(
                logId = logId,
                name = "Original Log Name",
                isVisible = true,
                movieIds = mutableListOf("oldMovieId1", "oldMovieId2"),
                watchedIds = mutableListOf("oldWatchedId1", "oldWatchedId2"),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            )
        )
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.updateLog(logId, updateData)

        // Assert
        verify(mockJsonUtility).readFromFile()
        verify(mockJsonUtility).overwriteJSON(any())
    }

    @Test
    fun testMarkMovieShouldMarkMovie() {
        // Arrange
        val logId = "fakeLogId"
        val movieId = "fakeMovieId"
        val existingLogs = listOf(
            LogData(
                logId = logId,
                name = "Fake Log",
                isVisible = true,
                movieIds = mutableListOf(movieId),
                watchedIds = mutableListOf(),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            )
        )
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.markMovie(logId, movieId, watched = true)

        // Assert
        val expectedUpdatedLog = LogData(
            logId = logId,
            name = "Fake Log",
            isVisible = true,
            movieIds = mutableListOf(),
            watchedIds = mutableListOf(movieId),
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap()
        )
        verify(mockJsonUtility).readFromFile()
        verify(mockJsonUtility).overwriteJSON(listOf(expectedUpdatedLog).toMutableList())
    }

    @Test
    fun testMarkMovieShouldUnMarkMovie() {
        // Arrange
        val logId = "fakeLogId"
        val movieId = "fakeMovieId"
        val existingLogs = listOf(
            LogData(
                logId = logId,
                name = "Fake Log",
                isVisible = true,
                movieIds = mutableListOf(),
                watchedIds = mutableListOf(movieId),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            )
        )
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.markMovie(logId, movieId, watched = false)

        // Assert
        val expectedUpdatedLog = LogData(
            logId = logId,
            name = "Fake Log",
            isVisible = true,
            movieIds = mutableListOf(movieId),
            watchedIds = mutableListOf(),
            creationDate = "2024-02-04",
            lastModifiedDate = "2024-02-04",
            owner = Owner("fakeUserId", 0),
            collaborators = mutableListOf(),
            order = emptyMap()
        )
        verify(mockJsonUtility).readFromFile()
        verify(mockJsonUtility).overwriteJSON(listOf(expectedUpdatedLog).toMutableList())
    }

    @Test
    fun testMarkMovieShouldHandleNonExistentLogIdMarkMovie() {
        // Arrange
        val logId = "nonExistentLogId"
        val movieId = "fakeMovieId"
        val existingLogs = emptyList<LogData>()
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.markMovie(logId, movieId, watched = true)

        // Assert
        verify(mockJsonUtility).readFromFile()
        // No overwrite should be called as logId does not exist
        verify(mockJsonUtility, times(0)).overwriteJSON(any())
    }

    @Test
    fun testMarkMovieShouldHandleNonExistentLogIdUnMarkMovie() {
        // Arrange
        val logId = "nonExistentLogId"
        val movieId = "fakeMovieId"
        val existingLogs = emptyList<LogData>()
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs.toMutableList())

        // Act
        logLocalRepository.markMovie(logId, movieId, watched = false)

        // Assert
        verify(mockJsonUtility).readFromFile()
        // No overwrite should be called as logId does not exist
        verify(mockJsonUtility, times(0)).overwriteJSON(any())
    }

    @Test
    fun testDeleteLogShouldRemoveLog() {
        // Arrange
        val logIdToDelete = "fakeLogIdToDelete"
        val existingLogs = mutableListOf(
            LogData(
                logId = "fakeLogId1",
                name = "Fake Log 1",
                isVisible = true,
                movieIds = mutableListOf("movieId1"),
                watchedIds = mutableListOf("watchedId1"),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            ),
            LogData(
                logId = logIdToDelete,
                name = "Fake Log to Delete",
                isVisible = true,
                movieIds = mutableListOf("movieIdToDelete"),
                watchedIds = mutableListOf("watchedIdToDelete"),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            ),
            LogData(
                logId = "fakeLogId3",
                name = "Fake Log 3",
                isVisible = true,
                movieIds = mutableListOf("movieId3"),
                watchedIds = mutableListOf("watchedId3"),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            )
        )
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs)

        // Act
        logLocalRepository.deleteLog(logIdToDelete)

        // Assert
        verify(mockJsonUtility).overwriteJSON(existingLogs.filterNot { it.logId == logIdToDelete })
    }

    @Test
    fun testDeleteLogShouldHandleNonExistentLog() {
        // Arrange
        val nonExistentLogId = "nonExistentLogId"
        val existingLogs = mutableListOf(
            LogData(
                logId = "fakeLogId1",
                name = "Fake Log 1",
                isVisible = true,
                movieIds = mutableListOf("movieId1"),
                watchedIds = mutableListOf("watchedId1"),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            ),
            LogData(
                logId = "fakeLogId2",
                name = "Fake Log 2",
                isVisible = true,
                movieIds = mutableListOf("movieId2"),
                watchedIds = mutableListOf("watchedId2"),
                creationDate = "2024-02-04",
                lastModifiedDate = "2024-02-04",
                owner = Owner("fakeUserId", 0),
                collaborators = mutableListOf(),
                order = emptyMap()
            )
        )
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs)

        // Act
        logLocalRepository.deleteLog(nonExistentLogId)

        // Assert
        verify(mockJsonUtility).overwriteJSON(existingLogs) // No change expected
    }

    @Test
    fun testDeleteLogShouldHandleNoLogs() {
        // Arrange
        val logIdToDelete = "logIdToDelete"
        val existingLogs = mutableListOf<LogData>()
        whenever(mockJsonUtility.readFromFile()).thenReturn(existingLogs)

        // Act
        logLocalRepository.deleteLog(logIdToDelete)

        // Assert
        verify(mockJsonUtility).overwriteJSON(existingLogs) // No change expected
    }
}