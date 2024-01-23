package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.tmdb.MovieSearchData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchResult
import com.tabka.backblogapp.network.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchResultsViewModel : ViewModel() {
    private val TAG = "SearchResultsViewModel"
    private val apiService = ApiClient.movieApiService

    private val movieRepository = MovieRepository(apiService)

    private val _movieResults = MutableStateFlow<List<MovieSearchResult>?>(emptyList())
    val movieResults = _movieResults.asStateFlow()

    fun getMovieResults(query: String) {
        movieRepository.searchMovie(query, 1,
            onResponse = { searchData ->
                Log.d(TAG, searchData.toString())
                _movieResults.value = searchData?.results
            },
            onFailure = { error ->
                Log.d(TAG, error)
            }
        )
    }
}