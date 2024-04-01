package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.tmdb.MovieSearchData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchResult
import com.tabka.backblogapp.network.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class SearchResultsViewModel(val movieRepository: MovieRepository = MovieRepository(Firebase.firestore, ApiClient.movieApiService)) : ViewModel() {
    private val TAG = "SearchResultsViewModel"

    private val _movieResults: MutableStateFlow<List<MovieSearchResult>?> = MutableStateFlow(mutableListOf())
    val movieResults = _movieResults.asStateFlow()

    private val _halfSheets: MutableStateFlow<MutableMap<String, String>> = MutableStateFlow(mutableMapOf())
    val halfSheet = _halfSheets.asStateFlow()

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    private val searchMutex = Mutex()
    private var currentJob: Job? = null

    fun resetMovieResults() {
        _movieResults.value = emptyList()
    }

    suspend fun getMovieResults(query: String) {
        isLoading.value = true
        _movieResults.value = emptyList()

        searchMutex.withLock {

            currentJob?.cancel()
            currentJob = viewModelScope.launch {
                try {
                    val searchData: MovieSearchData? = withContext(Dispatchers.IO) {
                        suspendCancellableCoroutine { continuation ->
                            movieRepository.searchMovie(query, 1,
                                onResponse = { searchData ->
                                    Log.d(TAG, "Data: $searchData")
                                    continuation.resume(searchData)
                                },
                                onFailure = { error ->
                                    Log.d(TAG, "Error getting movies by query: $query. $error")
                                    continuation.resume(null)
                                }
                            )
                        }
                    }

                    searchData?.results?.forEach { movie ->
                        val movieId = movie.id?.toString() ?: ""
                        withContext(Dispatchers.IO) {
                            val backdropPath =
                                suspendCancellableCoroutine { continuation ->
                                    movieRepository.getMovieHalfSheet(movieId,
                                        onResponse = { backdropPath ->
                                            Log.d(TAG, "Backdrop exists")
                                            continuation.resume(backdropPath)
                                        },
                                        onFailure = { error ->
                                            Log.d(TAG, "Error: $error")
                                            continuation.resume(null)
                                        }
                                    )
                                }
                            backdropPath?.let { _halfSheets.value[movieId] = it }
                        }
                    }

                    _movieResults.value = searchData?.results

                } catch (error: Throwable) {
                    Log.e(TAG, "Error fetching movie results: ${error.message}", error)
                    isLoading.value = false
                } finally {
                    isLoading.value = false
                }
            }
        }
    }

    suspend fun getMovieResultsByGenre(genreId: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val searchData: MovieSearchData? = withContext(Dispatchers.IO) {
                    suspendCancellableCoroutine { continuation ->
                        movieRepository.searchMoviesByGenre(1, genreId,
                            onResponse = { searchData ->
                                Log.d(TAG, "Data: $searchData")
                                continuation.resume(searchData)
                            },
                            onFailure = { error ->
                                Log.d(TAG, "Error getting movies by genre: $genreId. $error")
                                continuation.resume(null)
                            }
                        )
                    }
                }
                searchData?.results?.forEach { movie ->
                    val movieId = movie.id?.toString() ?: ""
                    withContext(Dispatchers.IO) {
                        val backdropPath =
                            suspendCancellableCoroutine { continuation ->
                                movieRepository.getMovieHalfSheet(movieId,
                                    onResponse = { backdropPath ->
                                        Log.d(TAG, "Backdrop exists")
                                        continuation.resume(backdropPath)
                                    },
                                    onFailure = { error ->
                                        Log.d(TAG, "Error: $error")
                                        continuation.resume(null)
                                    }
                                )
                            }
                        backdropPath?.let { _halfSheets.value[movieId] = it }
                    }
                }
                _movieResults.value = searchData?.results
            } catch (error: Throwable) {
                Log.e(TAG, "Error fetching movie results by genre: ${error.message}", error)
                isLoading.value = false
            } finally {
                isLoading.value = false
            }
        }
    }
}