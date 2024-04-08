package com.tabka.backblogapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.mocks.FakeMovieRepository
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MinimalMovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever

class LogDetailsViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var logRepository: LogRepository

    @Mock
    private lateinit var logLocalRepository: LogLocalRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var firestore: FirebaseFirestore

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var currentUser: FirebaseUser // Mock FirebaseUser

    private lateinit var logDetailsViewModel: LogDetailsViewModel

    private lateinit var movieRepository: FakeMovieRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        movieRepository = FakeMovieRepository(firestore)
        logDetailsViewModel = LogDetailsViewModel("11",
            firestore,
            auth,
            logLocalRepository,
            logRepository,
            movieRepository,
            userRepository
        )

        // Mock behavior for auth.currentUser
        whenever(auth.currentUser).thenReturn(currentUser)
        whenever(currentUser.uid).thenReturn("fakeUserId")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testIsCollaboratorShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            logDetailsViewModel.logData.value = LogData(
                logId = "log123",
                name = "My log",
                collaborators = mutableListOf("fakeUserId"),
                creationDate = "3453454534",
                lastModifiedDate = "43545654654",
                isVisible = true,
                owner = Owner(userId = "anotherUser123", priority = 0),
                order = emptyMap(),
                movieIds = mutableListOf(),
                watchedIds = mutableListOf()
            )

            // Act
            val result = logDetailsViewModel.isCollaborator()

            // Assert
            Assert.assertEquals(true, result)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testIsCollaboratorNilUserIdShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(auth.currentUser).thenReturn(null)

            // Act
            val result = logDetailsViewModel.isCollaborator()

            // Assert
            Assert.assertEquals(false, result)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testDeleteLogShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            logDetailsViewModel.logData.value = LogData(
                logId = "log123",
                name = "My log",
                collaborators = mutableListOf("fakeUserId"),
                creationDate = "3453454534",
                lastModifiedDate = "43545654654",
                isVisible = true,
                owner = Owner(userId = "anotherUser123", priority = 0),
                order = emptyMap(),
                movieIds = mutableListOf(),
                watchedIds = mutableListOf()
            )
            whenever(logRepository.deleteLog(anyString())).thenReturn(DataResult.Success(true))

            // Act
            logDetailsViewModel.deleteLog()

            // Assert
            Assert.assertEquals(logDetailsViewModel.logData.value?.logId, "log123")
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
            logDetailsViewModel.logData.value = LogData(
                logId = "log123",
                name = "My log",
                collaborators = mutableListOf("fakeUserId"),
                creationDate = "3453454534",
                lastModifiedDate = "43545654654",
                isVisible = true,
                owner = Owner(userId = "anotherUser123", priority = 0),
                order = emptyMap(),
                movieIds = mutableListOf(),
                watchedIds = mutableListOf()
            )
            whenever(auth.currentUser).thenReturn(null)
            doNothing().whenever(logLocalRepository).deleteLog(anyString())

            // Act
            logDetailsViewModel.deleteLog()

            // Assert
            Assert.assertEquals(logDetailsViewModel.logData.value?.logId, "log123")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testShuffleMoviesShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        logDetailsViewModel = LogDetailsViewModel("11",
            firestore,
            auth,
            logLocalRepository,
            logRepository,
            movieRepository,
            userRepository,
            testDispatcher
        )

        try {
            // Arrange
            val logData = LogData(
                logId = "log123",
                name = "My log",
                collaborators = mutableListOf("fakeUserId"),
                creationDate = "3453454534",
                lastModifiedDate = "43545654654",
                isVisible = true,
                owner = Owner(userId = "anotherUser123", priority = 0),
                order = emptyMap(),
                movieIds = mutableListOf("11", "12"),
                watchedIds = mutableListOf()
            )
            val userData = UserData(
                userId = "mockUserId",
                username = "mockUsername"
            )
            logDetailsViewModel.logData.value = logData
            logDetailsViewModel.movies.value = mapOf(
                "11" to MinimalMovieData(id = "11"),
                "12" to MinimalMovieData(id = "12")
            )
            whenever(logRepository.updateLog(anyString(), anyMap())).thenReturn(DataResult.Success(true))
            whenever(logRepository.getLog(anyString())).thenReturn(DataResult.Success(logData))
            whenever(userRepository.getUser(anyString())).thenReturn(DataResult.Success(userData))

            // Act
            logDetailsViewModel.shuffleMovies()

            // Assert
            Assert.assertEquals(logDetailsViewModel.logData.value?.logId, "log123")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testShuffleMoviesNullUserIdShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        logDetailsViewModel = LogDetailsViewModel("11",
            firestore,
            auth,
            logLocalRepository,
            logRepository,
            movieRepository,
            userRepository,
            testDispatcher
        )

        try {
            // Arrange
            val logData = LogData(
                logId = "log123",
                name = "My log",
                collaborators = mutableListOf("fakeUserId"),
                creationDate = "3453454534",
                lastModifiedDate = "43545654654",
                isVisible = true,
                owner = Owner(userId = "anotherUser123", priority = 0),
                order = emptyMap(),
                movieIds = mutableListOf("11", "12"),
                watchedIds = mutableListOf("123", "145")
            )
            logDetailsViewModel.logData.value = logData
            logDetailsViewModel.movies.value = mapOf(
                "11" to MinimalMovieData(id = "11"),
                "12" to MinimalMovieData(id = "12")
            )
            logDetailsViewModel.watchedMovies.value = mapOf(
                "123" to MinimalMovieData(id = "123"),
                "145" to MinimalMovieData(id = "145")
            )
            whenever(auth.currentUser).thenReturn(null)
            doNothing().whenever(logLocalRepository).updateLog(anyString(), anyMap())
            whenever(logLocalRepository.getLogById(anyString())).thenReturn(logData)

            // Act
            logDetailsViewModel.shuffleMovies()

            // Assert
            Assert.assertEquals(logDetailsViewModel.logData.value?.logId, "log123")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUpdateLogCollaboratorsShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        logDetailsViewModel = LogDetailsViewModel("11",
            firestore,
            auth,
            logLocalRepository,
            logRepository,
            movieRepository,
            userRepository,
            testDispatcher
        )

        try {
            // Arrange
            val logData = LogData(
                logId = "log123",
                name = "My log",
                collaborators = mutableListOf("fakeUserId"),
                creationDate = "3453454534",
                lastModifiedDate = "43545654654",
                isVisible = true,
                owner = Owner(userId = "anotherUser123", priority = 0),
                order = emptyMap(),
                movieIds = mutableListOf("11", "12"),
                watchedIds = mutableListOf()
            )
            val userData = UserData(
                userId = "mockUserId",
                username = "mockUsername"
            )
            logDetailsViewModel.logData.value = logData
            logDetailsViewModel.movies.value = mapOf(
                "11" to MinimalMovieData(id = "11"),
                "12" to MinimalMovieData(id = "12")
            )
            whenever(logRepository.updateLog(anyString(), anyMap())).thenReturn(DataResult.Success(true))
            whenever(logRepository.getLog(anyString())).thenReturn(DataResult.Success(logData))
            whenever(logRepository.addCollaborators(anyString(), anyList())).thenReturn(DataResult.Success(true))
            whenever(logRepository.removeCollaborators(anyString(), anyList())).thenReturn(DataResult.Success(true))
            whenever(userRepository.getUser(anyString())).thenReturn(DataResult.Success(userData))

            // Act
            logDetailsViewModel.updateLogCollaborators(emptyList(), emptyList())

            // Assert
            Assert.assertEquals(logDetailsViewModel.logData.value?.logId, "log123")
        } finally {
            Dispatchers.resetMain()
        }
    }
}