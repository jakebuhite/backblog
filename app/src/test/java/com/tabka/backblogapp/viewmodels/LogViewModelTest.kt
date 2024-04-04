package com.tabka.backblogapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.mocks.FakeMovieRepository
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LogViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var logRepository: LogRepository

    @Mock
    private lateinit var logLocalRepository: LogLocalRepository

    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var currentUser: FirebaseUser // Mock FirebaseUser

    private lateinit var logViewModel: LogViewModel

    private lateinit var movieRepository: FakeMovieRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        movieRepository = FakeMovieRepository(firestore)
        logViewModel = LogViewModel(firestore, auth, movieRepository, logRepository, logLocalRepository)

        // Mock behavior for auth.currentUser
        whenever(auth.currentUser).thenReturn(currentUser)
        whenever(currentUser.uid).thenReturn("fakeUserId")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testLoadLogsShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(emptyList()))

            // Act
            logViewModel.loadLogs()

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testLoadLogsNullUserShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(auth.currentUser).thenReturn(null)
            whenever(logLocalRepository.getLogs()).thenReturn(emptyList())

            // Act
            logViewModel.loadLogs()

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testLoadLogsShouldReturnFailure() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val exception = Exception("Simulated exception")
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Failure(exception))

            // Act
            logViewModel.loadLogs()

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testOnMoveShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "mockUserId"
            val fakeLogData = listOf(
                LogData(
                    logId = "log123",
                    name = "My log",
                    collaborators = mutableListOf(userId),
                    creationDate = "3453454534",
                    lastModifiedDate = "43545654654",
                    isVisible = true,
                    owner = Owner(userId = "anotherUser123", priority = 0),
                    order = emptyMap(),
                    movieIds = mutableListOf(),
                    watchedIds = mutableListOf()
                ),
                LogData(
                    logId = "log1234",
                    name = "My log2",
                    collaborators = mutableListOf(userId),
                    creationDate = "3453454534",
                    lastModifiedDate = "43545654654",
                    isVisible = true,
                    owner = Owner(userId = "anotherUser123", priority = 1),
                    order = emptyMap(),
                    movieIds = mutableListOf(),
                    watchedIds = mutableListOf()
                )
            )
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(fakeLogData))
            whenever(logRepository.updateUserLogOrder(anyString(), anyList())).thenReturn(DataResult.Success(true))

            // Act
            logViewModel.loadLogs()
            logViewModel.onMove(0, 1)

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 2)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testOnMoveNullUserIdShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val fakeLogData = listOf(
                LogData(
                    logId = "log123",
                    name = "My log",
                    collaborators = mutableListOf(),
                    creationDate = "3453454534",
                    lastModifiedDate = "43545654654",
                    isVisible = true,
                    owner = Owner(userId = "anotherUser123", priority = 0),
                    order = emptyMap(),
                    movieIds = mutableListOf(),
                    watchedIds = mutableListOf()
                ),
                LogData(
                    logId = "log1234",
                    name = "My log2",
                    collaborators = mutableListOf(),
                    creationDate = "3453454534",
                    lastModifiedDate = "43545654654",
                    isVisible = true,
                    owner = Owner(userId = "anotherUser123", priority = 1),
                    order = emptyMap(),
                    movieIds = mutableListOf(),
                    watchedIds = mutableListOf()
                )
            )
            whenever(auth.currentUser).thenReturn(null)
            whenever(logLocalRepository.getLogs()).thenReturn(fakeLogData)
            doNothing().whenever(logLocalRepository).reorderLogs(anyList())

            // Act
            logViewModel.loadLogs()
            logViewModel.onMove(0, 1)

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 2)
            verify(logLocalRepository, times(1)).reorderLogs(anyList())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testOnMoveShouldReturnFailure() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "mockUserId"
            val fakeLogData = listOf(
                LogData(
                    logId = "log123",
                    name = "My log",
                    collaborators = mutableListOf(userId),
                    creationDate = "3453454534",
                    lastModifiedDate = "43545654654",
                    isVisible = true,
                    owner = Owner(userId = "anotherUser123", priority = 0),
                    order = emptyMap(),
                    movieIds = mutableListOf(),
                    watchedIds = mutableListOf()
                ),
                LogData(
                    logId = "log1234",
                    name = "My log2",
                    collaborators = mutableListOf(userId),
                    creationDate = "3453454534",
                    lastModifiedDate = "43545654654",
                    isVisible = true,
                    owner = Owner(userId = "anotherUser123", priority = 1),
                    order = emptyMap(),
                    movieIds = mutableListOf(),
                    watchedIds = mutableListOf()
                )
            )
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(fakeLogData))
            val exception = Exception("Simulated exception")
            whenever(logRepository.updateUserLogOrder(anyString(), anyList())).thenReturn(DataResult.Failure(exception))

            // Act
            logViewModel.loadLogs()
            logViewModel.onMove(0, 1)

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 2)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAddMovieToLogShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(emptyList()))

            // Act
            logViewModel.addMovieToLog("log123", "movie123")

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAddMovieToLogNullUserShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(auth.currentUser).thenReturn(null)
            whenever(logLocalRepository.getLogs()).thenReturn(emptyList())
            doNothing().whenever(logLocalRepository).addMovieToLog(anyString(), anyString())

            // Act
            logViewModel.addMovieToLog("log123", "movie123")

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
            verify(logLocalRepository, times(1)).addMovieToLog(anyString(), anyString())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testMarkMovieAsWatchedShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(emptyList()))

            // Act
            logViewModel.markMovieAsWatched("log123", "movie123")

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testMarkMovieAsWatchedNullUserShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(auth.currentUser).thenReturn(null)
            whenever(logLocalRepository.getLogs()).thenReturn(emptyList())
            doNothing().whenever(logLocalRepository).markMovie(anyString(), anyString(), anyBoolean())

            // Act
            logViewModel.markMovieAsWatched("log123", "movie123")

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
            verify(logLocalRepository, times(1)).markMovie(anyString(), anyString(), anyBoolean())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUnMarkMovieAsWatchedShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(emptyList()))

            // Act
            logViewModel.unmarkMovieAsWatched("log123", "movie123")

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUnMarkMovieAsWatchedNullUserShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(auth.currentUser).thenReturn(null)
            whenever(logLocalRepository.getLogs()).thenReturn(emptyList())
            doNothing().whenever(logLocalRepository).markMovie(anyString(), anyString(), anyBoolean())

            // Act
            logViewModel.unmarkMovieAsWatched("log123", "movie123")

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
            verify(logLocalRepository, times(1)).markMovie(anyString(), anyString(), anyBoolean())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testResetMovieShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Act
            logViewModel.resetMovie()

            // Assert
            Assert.assertEquals(logViewModel.movie.value, (null to ""))
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetMovieByIdShouldReturnSuccess() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val movieId = "123"

            // Act
            logViewModel.getMovieById(movieId)

            // Assert
            Assert.assertEquals(logViewModel.movie.value.second, "fake/file/path.png")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetMovieByIdShouldReturnFailureOnGetMovieHalfSheet() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val movieId = "123"

            // Act
            movieRepository.shouldGetMovieHalfSheetNotSucceed()
            logViewModel.getMovieById(movieId)

            // Assert
            Assert.assertEquals(logViewModel.movie.value.second, "")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetMovieByIdShouldReturnFailure() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val movieId = "123"

            // Act
            movieRepository.shouldGetMovieByIdNotSucceed()
            logViewModel.getMovieById(movieId)

            // Assert
            Assert.assertEquals(logViewModel.movie.value.second, "")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFetchMovieDetailsShouldReturnSuccess() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val movieId = "123"

            // Act
            logViewModel.fetchMovieDetails(movieId, onResponse = { result ->
                Assert.assertEquals(result.error, null)
                assert(result.data != null)
            })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFetchMovieDetailsShouldReturnFailureOnGetMovieHalfSheet() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val movieId = "123"

            // Act
            movieRepository.shouldGetMovieHalfSheetNotSucceed()
            logViewModel.fetchMovieDetails(movieId, onResponse = { result ->
                assert(result.error == null)
                Assert.assertEquals(result.data?.second, "")
            })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFetchMovieDetailsShouldReturnFailure() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val movieId = "123"

            // Act
            movieRepository.shouldGetMovieByIdNotSucceed()
            logViewModel.fetchMovieDetails(movieId, onResponse = { result ->
                assert(result.error != null)
                Assert.assertEquals(result.data, null)
            })
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCreateLogShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(logRepository.addLog(anyString(), anyBoolean(), anyString())).thenReturn(DataResult.Success("log123"))
            whenever(logRepository.addCollaborators(anyString(), anyList())).thenReturn(DataResult.Success(true))
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(emptyList()))

            // Act
            logViewModel.createLog("My Log", emptyList(), false)

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCreateLogNullUserShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val fakeLogData = listOf(
                LogData(
                    logId = "log123",
                    name = "My log",
                    collaborators = mutableListOf(),
                    creationDate = "3453454534",
                    lastModifiedDate = "43545654654",
                    isVisible = true,
                    owner = Owner(userId = "anotherUser123", priority = 0),
                    order = emptyMap(),
                    movieIds = mutableListOf(),
                    watchedIds = mutableListOf()
                ),
                LogData(
                    logId = "log1234",
                    name = "My log2",
                    collaborators = mutableListOf(),
                    creationDate = "3453454534",
                    lastModifiedDate = "43545654654",
                    isVisible = true,
                    owner = Owner(userId = "anotherUser123", priority = 1),
                    order = emptyMap(),
                    movieIds = mutableListOf(),
                    watchedIds = mutableListOf()
                )
            )
            whenever(auth.currentUser).thenReturn(null)
            doNothing().whenever(logLocalRepository).createLog(any())
            whenever(logLocalRepository.getLogs()).thenReturn(fakeLogData)

            // Act
            logViewModel.loadLogs()
            logViewModel.createLog("My Log", emptyList(), false)

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 2)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCreateLogShouldReturnFailure() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val exception = Exception("Simulated exception")
            whenever(logRepository.addLog(anyString(), anyBoolean(), anyString())).thenReturn(DataResult.Failure(exception))

            // Act
            logViewModel.createLog("My Log", emptyList(), false)

            // Assert
            Assert.assertEquals(logViewModel.allLogs.value?.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }
}