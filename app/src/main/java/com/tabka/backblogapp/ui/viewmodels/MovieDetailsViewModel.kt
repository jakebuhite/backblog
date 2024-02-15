package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MovieDetailsViewModel(savedStateHandle: SavedStateHandle): ViewModel() {
    private val TAG = "MovieDetailsViewModel"
    private val apiService = ApiClient.movieApiService
    private val movieRepository = MovieRepository(apiService)

    private val movieId: String = checkNotNull(savedStateHandle["movieId"])
    private val _movie = MutableStateFlow<MovieData?>(null)
    val movie = _movie.asStateFlow()

    init {
        Log.d(TAG, "Movie ID: $movieId")
        movieRepository.getMovieById(movieId,
            onResponse = { movie ->
                _movie.value = movie
                Log.d(TAG, "GOT THE MOVIE: $movie")
            },
            onFailure = { error ->
                Log.d(TAG, error)
            }
        )
    }

}