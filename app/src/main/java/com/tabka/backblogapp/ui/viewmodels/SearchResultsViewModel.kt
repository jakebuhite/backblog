package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.tmdb.MovieSearchResult
import com.tabka.backblogapp.network.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchResultsViewModel : ViewModel() {
    private val TAG = "SearchResultsViewModel"
    private val apiService = ApiClient.movieApiService

    private val movieRepository = MovieRepository(apiService)

    private val _movieResults: MutableStateFlow<List<MovieSearchResult>?> = MutableStateFlow(mutableListOf())
    val movieResults = _movieResults.asStateFlow()

    private val _halfSheets: MutableStateFlow<MutableMap<String, String>> = MutableStateFlow(mutableMapOf())
    val halfSheet = _halfSheets.asStateFlow()

    fun getMovieResults(query: String) {
        movieRepository.searchMovie(query, 1,
            onResponse = { searchData ->
                searchData?.results?.forEach {
                    val movieId = it.id?.toString() ?: ""
                    movieRepository.getMovieHalfSheet(movieId,
                        onResponse = {backdropPath ->
                            _halfSheets.value[movieId] = backdropPath
                        },
                        onFailure = { error ->
                            Log.d(TAG, error)
                        })
                }
                Log.d(TAG, searchData.toString())
                _movieResults.value = searchData?.results
            },
            onFailure = { error ->
                Log.d(TAG, error)
            }
        )
    }

    fun getMovieResultsByGenre(genreId: String) {
        movieRepository.searchMoviesByGenre(1, genreId,
            onResponse = { searchData ->
                searchData?.results?.forEach {
                    val movieId = it.id?.toString() ?: ""
                    movieRepository.getMovieHalfSheet(movieId,
                        onResponse = {backdropPath ->
                            _halfSheets.value[movieId] = backdropPath
                        },
                        onFailure = { error ->
                            Log.d(TAG, error)
                        })
                }
                _movieResults.value = searchData?.results
            },
            onFailure = { error ->
                Log.d(TAG, error)
            }
        )
    }
}