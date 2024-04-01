package com.tabka.backblogapp

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.ApiService
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchResult
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    @Test
    fun testGetMovieByIdShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val movieApiService = mock<ApiService>()
        val call = mock<Call<MovieData>>()
        val movieId = "123456"

        movieRepository = MovieRepository(mockDb, movieApiService)

        whenever(movieApiService.getMovieDetails(movieId, "images,release_dates,watch/providers,credits", "Bearer " + BuildConfig.MOVIE_SECRET)).thenReturn(call)

        // Successful response
        whenever(call.enqueue(any())).thenAnswer {
            val callback = it.arguments[0] as Callback<MovieData>
            val movieData = MovieData(
                adult = false,
                belongsToCollection = null,
                budget = null,
                credits = null,
                backdropPath = "backdrop.png",
                genres = null,
                homepage = null,
                id = 11,
                images = null,
                imdbId = null,
                originalLanguage = null,
                originalTitle = null,
                overview = null,
                popularity = null,
                posterPath = "poster123",
                productionCompanies = null,
                productionCountries = null,
                releaseDate = null,
                releaseDates = null,
                revenue = null,
                runtime = null,
                spokenLanguages = null,
                status = null,
                tagline = null,
                title = null,
                video = null,
                voteAverage = 10.0,
                voteCount = 10,
                watchProviders = null
            )
            val response = Response.success(movieData)
            callback.onResponse(call, response)
        }

        // Act
        movieRepository.getMovieById(movieId,
            onResponse = { movieData ->
                // Assert
                assert(movieData != null)
            },
            onFailure = { errorMessage ->
                // Assert
                Assert.fail("Function should not return onFailure: $errorMessage")
            }
        )
    }

    @Test
    fun testGetMovieByIdShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val movieApiService = mock<ApiService>()
        val call = mock<Call<MovieData>>()
        val movieId = "123456"

        movieRepository = MovieRepository(mockDb, movieApiService)

        whenever(movieApiService.getMovieDetails(movieId, "images,release_dates,watch/providers,credits", "Bearer " + BuildConfig.MOVIE_SECRET)).thenReturn(call)

        // Failed response
        whenever(call.enqueue(any())).thenAnswer {
            val callback = it.arguments[0] as Callback<MovieData>
            val error = Throwable("Failed to get movie details")
            callback.onFailure(call, error)
        }

        // Act
        movieRepository.getMovieById(movieId,
            onResponse = {
                // Assert
                Assert.fail("Function should not return onResponse")
            },
            onFailure = { errorMessage ->
                // Assert
                assert(errorMessage.isNotEmpty())
            }
        )
    }

    @Test
    fun testSearchMoviesByGenreShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val movieApiService = mock<ApiService>()
        val call = mock<Call<MovieSearchData>>()

        movieRepository = MovieRepository(mockDb, movieApiService)

        whenever(movieApiService.searchMoviesByGenre(anyBoolean(), anyBoolean(), anyString(), anyInt(), anyString(), anyString(), anyString())).thenReturn(call)

        // Successful response
        whenever(call.enqueue(any())).thenAnswer {
            val callback = it.arguments[0] as Callback<MovieSearchData>
            val movieSearchData = MovieSearchData(
                page = 1,
                results = listOf(
                    MovieSearchResult(
                    adult = false,
                    backdropPath = "backdrop.png",
                    genreIds = null,
                    id = 11,
                    originalLanguage = null,
                    originalTitle = null,
                    overview = null,
                    popularity = null,
                    posterPath = "poster123",
                    releaseDate = null,
                    title = null,
                    video = null,
                    voteAverage = 10.0,
                    voteCount = 10
                )
                ),
                totalPages = 1,
                totalResults = 1
            )
            val response = Response.success(movieSearchData)
            callback.onResponse(call, response)
        }

        // Act
        movieRepository.searchMoviesByGenre(1, "11",
            onResponse = { movieData ->
                // Assert
                assert(movieData != null)
            },
            onFailure = { errorMessage ->
                // Assert
                Assert.fail("Function should not return onFailure: $errorMessage")
            }
        )
    }

    @Test
    fun testSearchMoviesByGenreShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val movieApiService = mock<ApiService>()
        val call = mock<Call<MovieSearchData>>()

        movieRepository = MovieRepository(mockDb, movieApiService)

        whenever(movieApiService.searchMoviesByGenre(anyBoolean(), anyBoolean(), anyString(), anyInt(), anyString(), anyString(), anyString())).thenReturn(call)

        // Failed response
        whenever(call.enqueue(any())).thenAnswer {
            val callback = it.arguments[0] as Callback<MovieSearchData>
            val error = Throwable("Failed to get movie search details")
            callback.onFailure(call, error)
        }

        // Act
        movieRepository.searchMoviesByGenre(1, "11",
            onResponse = {
                // Assert that the correct movie data is received
                Assert.fail("Function should not return onResponse")
            },
            onFailure = { errorMessage ->
                // Assert
                assert(errorMessage.isNotEmpty())
            }
        )
    }
}