package com.tabka.backblogapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.mocks.FakeMovieRepository
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MovieDetailsViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var firestore: FirebaseFirestore

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel

    private lateinit var movieRepository: FakeMovieRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        movieRepository = FakeMovieRepository(firestore)
        movieDetailsViewModel = MovieDetailsViewModel(firestore, movieRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSetMovieShouldReturnSuccess() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val movieId = "123"

            // Act
            movieDetailsViewModel.setMovie(movieId)

            // Assert
            assert(movieDetailsViewModel.movie.value != null)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSetMovieShouldReturnFailure() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val movieId = "123"

            // Act
            movieRepository.shouldGetMovieByIdNotSucceed()
            movieDetailsViewModel.setMovie(movieId)

            // Assert
            assert(movieDetailsViewModel.movie.value == null)
        } finally {
            Dispatchers.resetMain()
        }
    }
}