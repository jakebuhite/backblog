package com.tabka.backblogapp.viewmodels
/*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.mocks.FakeMovieRepository
import com.tabka.backblogapp.ui.viewmodels.SearchResultsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
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

    private lateinit var viewModel: SearchResultsViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    // This is used to test coroutines
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        val movieRepository = FakeMovieRepository(firestore)
        viewModel = SearchResultsViewModel(movieRepository)

        // Use TestCoroutineDispatcher for testing coroutines
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }

    @Test
    fun `test getMovieResults`() = testScope.runBlockingTest {
        val query = "Action"
        val job = launch {
            viewModel.getMovieResults(query)
        }

        // Advance the coroutine until it's completed
        advanceUntilIdle()

        // Verify the results
        viewModel.movieResults.collect { movieResults ->
            assertEquals(1, movieResults?.size)

            viewModel.halfSheet.collect { halfSheet ->
                assertEquals(1, halfSheet.size)

                assertEquals(false, viewModel.isLoading.value)

                // Cancel the job to ensure it's completed
                job.cancel()
            }
        }
    }

    @Test
    fun `test getMovieResultsByGenre`() = testScope.runBlockingTest {
        val genreId = "28"
        val job = launch {
            viewModel.getMovieResultsByGenre(genreId)
        }

        // Advance the coroutine until it's completed
        advanceUntilIdle()

        // Verify the results
        viewModel.movieResults.collect { movieResults ->
            assertEquals(1, movieResults?.size)

            viewModel.halfSheet.collect { halfSheet ->
                assertEquals(1, halfSheet.size)

                assertEquals(false, viewModel.isLoading.value)

                // Cancel the job to ensure it's completed
                job.cancel()
            }
        }
    }
}*/