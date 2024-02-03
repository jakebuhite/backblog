package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogDetailsViewModel: ViewModel() {
    private val tag = "LogDetailsViewModel"
    private val auth = Firebase.auth

    // Log data
    private val localRepository = LogLocalRepository()
    private val logRepository = LogRepository()
    val logData: MutableLiveData<LogData> = MutableLiveData()

    // Movie data
    private val apiService = ApiClient.movieApiService
    private val movieRepository = MovieRepository(apiService)
    val movies: MutableLiveData<List<MovieData>> = MutableLiveData()

    private suspend fun updateLogData(newLog: LogData) {
        logData.value = newLog
        getMovies()
    }

    private fun updateMovieList(newList: List<MovieData>) {
        movies.value = newList
    }

    suspend fun getLogData(logId: String) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser?.uid
                if (user != null) {
                    val result: DataResult<LogData> = logRepository.getLog(logId)
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is DataResult.Success -> updateLogData(result.item)
                            is DataResult.Failure -> throw result.throwable
                        }
                    }
                } else {
                    val result = localRepository.getLogById(logId)
                    if (result != null) {
                        updateLogData(result)
                    } else {
                        throw Throwable("Failed to get log from local log repository")
                    }
                }
            } catch (e: Exception) {
                Log.d(tag, "Error: $e")
            }
        }
    }

    private suspend fun getMovies() {
        val movieDataList = mutableListOf<MovieData>()
        val movieIds = logData.value?.movieIds?.keys ?: listOf()

        viewModelScope.launch {
            try {
                for (movieId in movieIds) {
                    movieRepository.getMovieById(
                        movieId = movieId,
                        onResponse = { movieResponse ->
                            movieResponse?.let { movieDataList.add(it) }
                        },
                        onFailure = { e ->
                            Log.e("Movies", "Failed to fetch details for movie ID $movieId: $e")
                        }
                    )
                }

                withContext(Dispatchers.Main) {
                    updateMovieList(movieDataList)
                }
            } catch (e: Exception) {
                Log.e("Movies", "Failed to fetch details for movies: $e")
            }
        }
    }
}