package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MovieDetailsViewModel(
    db: FirebaseFirestore = Firebase.firestore,
    val movieRepository: MovieRepository = MovieRepository(db, ApiClient.movieApiService)
): ViewModel() {
    private val TAG = "MovieDetailsViewModel"
    private val _movie = MutableStateFlow<MovieData?>(null)
    val movie = _movie.asStateFlow()

    fun setMovie(movieId: String) {
        Log.d(TAG, "Movie ID: $movieId")
        getMovie(movieId)
    }

    private fun getMovie(movieId: String) {
        movieRepository.getMovieById(
            movieId = movieId,
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