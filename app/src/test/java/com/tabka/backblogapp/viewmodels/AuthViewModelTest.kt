package com.tabka.backblogapp.viewmodels

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.ui.viewmodels.AuthViewModel
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthViewModelTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var logRepository: LogRepository

    @Mock
    private lateinit var logLocalRepository: LogLocalRepository

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var authResult: AuthResult

    @Mock
    private lateinit var currentUser: FirebaseUser

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authViewModel = AuthViewModel(auth, logLocalRepository, logRepository, userRepository)

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
        val result = authViewModel.syncLocalLogsToDB("mockUserId")

        // Assert
        verify(logLocalRepository).getLogs()
        verify(logRepository, times(2)).addLog(any(), any(), any(), any(), any(), any())
        verify(logLocalRepository).clearLogs()

        Assert.assertEquals(DataResult.Success(true), result)
    }

    @Test
    fun testSyncLocalLogsToDBWithEmptyLogsShouldReturnSuccess() = runBlocking {
        // Arrange
        val logs = listOf<LogData>()

        whenever(logLocalRepository.getLogs()).thenReturn(logs)
        whenever(logRepository.addLog(any(), any(), any(), any(), any(), any())).thenReturn(DataResult.Success(true))

        // Act
        val result = authViewModel.syncLocalLogsToDB("mockUserId")

        // Assert
        verify(logLocalRepository).getLogs()
        Assert.assertEquals(DataResult.Success(true), result)
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
        val result = authViewModel.syncLocalLogsToDB("mockUserId")

        // Assert
        Assert.assertEquals(DataResult.Failure(RuntimeException("Error fetching logs")).toString(), result.toString())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAttemptSignupShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val email = "email@email.com"
            val username = "fakeUser"
            val password = "password"

            val task: Task<AuthResult> = Tasks.forResult(authResult)
            whenever(auth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(task)
            whenever(authResult.user).thenReturn(currentUser)
            whenever(userRepository.addUser(anyString(), anyString(), anyInt())).
            thenReturn(DataResult.Success(true))

            // Act
            val result = authViewModel.attemptSignup(email, username, password)

            // Assert
            Assert.assertEquals(result, Pair(true, ""))
        } finally {
            Dispatchers.resetMain()
        }
    }
}