package com.tabka.backblogapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.mocks.FakeMovieRepository
import com.tabka.backblogapp.ui.viewmodels.SearchResultsViewModel
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
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class SearchResultsViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var firestore: FirebaseFirestore

    private lateinit var searchResultsViewModel: SearchResultsViewModel

    private lateinit var movieRepository: FakeMovieRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        movieRepository = FakeMovieRepository(firestore)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetMovieResultsShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        searchResultsViewModel = SearchResultsViewModel(
            movieRepository,
            testDispatcher
        )

        try {
            // Act
            searchResultsViewModel.getMovieResults("Star Wars")

            // Assert
            Assert.assertEquals(searchResultsViewModel.movieResults.value?.size, 1)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetMovieResultsShouldReturnSuccessAndFailOnGetMovieHalfSheet() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        movieRepository.shouldGetMovieHalfSheetNotSucceed()
        searchResultsViewModel = SearchResultsViewModel(
            movieRepository,
            testDispatcher
        )

        try {
            // Act
            searchResultsViewModel.getMovieResults("Star Wars")

            // Assert
            Assert.assertEquals(searchResultsViewModel.movieResults.value?.size, 1)
            Assert.assertEquals(searchResultsViewModel.halfSheet.value.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetMovieResultsShouldReturnFailure() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        movieRepository.shouldSearchMovieNotSucceed()
        searchResultsViewModel = SearchResultsViewModel(
            movieRepository,
            testDispatcher
        )

        try {
            // Act
            searchResultsViewModel.getMovieResults("Star Wars")

            // Assert
            Assert.assertEquals(searchResultsViewModel.movieResults.value, null)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetMovieResultsByGenreShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        searchResultsViewModel = SearchResultsViewModel(
            movieRepository,
            testDispatcher
        )

        try {
            // Act
            searchResultsViewModel.getMovieResultsByGenre("11")

            // Assert
            Assert.assertEquals(searchResultsViewModel.movieResults.value?.size, 1)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetMovieResultsByGenreShouldReturnSuccessAndFailOnGetMovieHalfSheet() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        movieRepository.shouldGetMovieHalfSheetNotSucceed()
        searchResultsViewModel = SearchResultsViewModel(
            movieRepository,
            testDispatcher
        )

        try {
            // Act
            searchResultsViewModel.getMovieResultsByGenre("11")

            // Assert
            Assert.assertEquals(searchResultsViewModel.movieResults.value?.size, 1)
            Assert.assertEquals(searchResultsViewModel.halfSheet.value.size, 0)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetMovieResultsByGenreShouldReturnFailure() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        movieRepository.shouldSearchMovieNotSucceed()
        searchResultsViewModel = SearchResultsViewModel(
            movieRepository,
            testDispatcher
        )

        try {
            // Act
            searchResultsViewModel.getMovieResultsByGenre("11")

            // Assert
            Assert.assertEquals(searchResultsViewModel.movieResults.value, null)
        } finally {
            Dispatchers.resetMain()
        }
    }
}