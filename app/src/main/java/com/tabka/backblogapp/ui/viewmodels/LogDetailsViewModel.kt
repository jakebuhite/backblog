package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LogDetailsViewModel(savedStateHandle: SavedStateHandle): ViewModel() {
    private val TAG = "LogDetailsViewModel"

    private val apiService = ApiClient.movieApiService
    private val movieRepository = MovieRepository(apiService)


    private val logId: String = checkNotNull(savedStateHandle["logId"])
    val log: LogData? = LogLocalRepository().getLogById(logId)


    private val _movies = MutableStateFlow<List<MovieData>?>(emptyList())
    val movies = _movies.asStateFlow()

    init {
        Log.d(TAG, "LogID! $logId")
        getMovies()
    }

    private fun getMovies() {
        Log.d(TAG, "Getting the movies")
        log!!.movieIds?.let { movieIds ->
            Log.d(TAG, "Here are the Movie IDs: $movieIds")
            movieIds.keys.forEach { movieId ->
                Log.d(TAG, "Going further... $movieId")
                movieRepository.getMovieById(movieId,
                    onResponse = { movieData ->
                        Log.d(TAG, "Good response, here is the movie: $movieData")
                        _movies.update { currentList ->
                            currentList?.plus(movieData!!)
                        }
                        Log.d(TAG, "Got the movie!")
                    },
                    onFailure = { error ->
                        Log.d(TAG, "Error: $error")
                    }
                )
            }
        }
    }

}