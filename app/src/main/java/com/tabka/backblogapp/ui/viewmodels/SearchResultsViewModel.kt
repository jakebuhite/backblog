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

    private val _movieResults: MutableStateFlow<Pair<List<MovieSearchResult>?, MutableList<String>>> = MutableStateFlow(Pair(emptyList(), mutableListOf()))
    val movieResults = _movieResults.asStateFlow()

    fun getMovieResults(query: String) {
        movieRepository.searchMovieWithHalfSheet(query, 1,
            onResponse = { searchData ->
                searchData.first?.toString()?.let { Log.d(TAG, it) }
                Log.d(TAG, searchData.second.toString())
                _movieResults.value = Pair(searchData.first?.results, searchData.second)
            },
            onFailure = { error ->
                Log.d(TAG, error)
            }
        )
    }
}