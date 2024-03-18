package com.tabka.backblogapp

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class MovieRepositoryTest {
    @Mock
    private lateinit var mockDb: FirebaseFirestore

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockDocument: DocumentReference

    private lateinit var movieRepository: MovieRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        movieRepository = MovieRepository(mockDb, ApiClient.movieApiService)
    }

    @Test
    fun testAddMovieShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val logId = "fakeLogId"
        val movieId = "fakeMovieId"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = movieRepository.addMovie(logId, movieId)

        // Assert
        assert(result is DataResult.Success)
        Mockito.verify(mockDocument).update(anyMap())
    }

    @Test
    fun testAddMovieShouldReturnException(): Unit = runBlocking {
        // Arrange
        val logId = "fakeLogId"
        val movieId = "fakeMovieId"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        // Simulate exception
        val exception = Exception("Simulated exception")
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = movieRepository.addMovie(logId, movieId)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
        Mockito.verify(mockDocument).update(anyMap())
    }

    @Test
    fun testMarkWatchedMovieShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val logId = "fakeLogId"
        val movieId = "fakeMovieId"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = movieRepository.markMovie(logId, movieId, true)

        // Assert
        assert(result is DataResult.Success)
        Mockito.verify(mockDocument).update(anyMap())
    }

    @Test
    fun testMarkUnwatchedMovieShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val logId = "fakeLogId"
        val movieId = "fakeMovieId"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = movieRepository.markMovie(logId, movieId, false)

        // Assert
        assert(result is DataResult.Success)
        Mockito.verify(mockDocument).update(anyMap())
    }

    @Test
    fun testMarkMovieShouldReturnException(): Unit = runBlocking {
        // Arrange
        val logId = "fakeLogId"
        val movieId = "fakeMovieId"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        // Simulate exception
        val exception = Exception("Simulated exception")
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = movieRepository.markMovie(logId, movieId, true)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
        Mockito.verify(mockDocument).update(anyMap())
    }
}