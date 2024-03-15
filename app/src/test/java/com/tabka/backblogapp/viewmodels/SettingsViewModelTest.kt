package com.tabka.backblogapp.viewmodels

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.ui.viewmodels.SettingsViewModel
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SettingsViewModelTest {
    @Mock
    private lateinit var logRepository: LogRepository

    @Mock
    private lateinit var logLocalRepository: LogLocalRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var currentUser: FirebaseUser // Mock FirebaseUser

    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        settingsViewModel = SettingsViewModel(auth, userRepository, logLocalRepository, logRepository)

        // Mock behavior for auth.currentUser
        whenever(auth.currentUser).thenReturn(currentUser)
        whenever(currentUser.uid).thenReturn("fakeUserId")
    }

    @Test
    fun testSyncLocalLogsToDBShouldReturnSuccess() = runBlocking {
        // Arrange
        val owner = Owner(
            userId = "user123",
            priority = 0
        )

        val logs = listOf(
            LogData(
                logId = "log123",
                name = "My log",
                creationDate = "34345564654",
                lastModifiedDate = "3244365690",
                isVisible = true,
                owner = owner,
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf(),
                watchedIds = mutableListOf()
            ),
            LogData(
                logId = "log123",
                name = "My log",
                creationDate = "34345564654",
                lastModifiedDate = "3244365690",
                isVisible = true,
                owner = owner,
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf(),
                watchedIds = mutableListOf()
            )
        )

        whenever(logLocalRepository.getLogs()).thenReturn(logs)
        whenever(logRepository.addLog(any(), any(), any(), any(), any(), any())).thenReturn(DataResult.Success(true))
        doAnswer {
            // Nothing
        }.`when`(logLocalRepository).clearLogs()

        // Act
        val result = settingsViewModel.syncLocalLogsToDB()

        // Assert
        verify(logLocalRepository).getLogs()
        verify(logRepository, times(2)).addLog(any(), any(), any(), any(), any(), any())
        verify(logLocalRepository).clearLogs()

        assertEquals(DataResult.Success(true), result)
    }

    @Test
    fun testSyncLocalLogsToDBWithEmptyLogsShouldReturnSuccess() = runBlocking {
        // Arrange
        val logs = listOf<LogData>()

        whenever(logLocalRepository.getLogs()).thenReturn(logs)
        whenever(logRepository.addLog(any(), any(), any(), any(), any(), any())).thenReturn(DataResult.Success(true))

        // Act
        val result = settingsViewModel.syncLocalLogsToDB()

        // Assert
        verify(logLocalRepository).getLogs()
        assertEquals(DataResult.Success(true), result)
    }

    @Test
    fun testSyncLocalLogsToDBShouldReturnException() = runBlocking {
        // Arrange
        whenever(logLocalRepository.getLogs()).thenThrow(RuntimeException("Error fetching logs"))
        whenever(logRepository.addLog(any(), any(), any(), any(), any(), any())).thenReturn(DataResult.Success(true))
        doAnswer {
            // Nothing
        }.`when`(logLocalRepository).clearLogs()

        // Act
        val result = settingsViewModel.syncLocalLogsToDB()

        // Assert
        assertEquals(DataResult.Failure(RuntimeException("Error fetching logs")).toString(), result.toString())
    }
}