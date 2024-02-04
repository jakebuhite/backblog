package com.tabka.backblogapp.ui.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

open class LogViewModel : ViewModel() {
    private val TAG = "LogViewModel"
    private val localLogRepository = LogLocalRepository()

    // Log Data
    private val _allLogs = MutableStateFlow<List<LogData>?>(emptyList())
    open var allLogs = _allLogs.asStateFlow()

    // Movie Data
    private val apiService = ApiClient.movieApiService
    private val movieRepository = MovieRepository(apiService)
    private val _movie = MutableStateFlow<MovieData?>(null)
    open val movie = _movie.asStateFlow()

    private val _nextMovie = MutableStateFlow<MovieData?>(null)
    val nextMovie = _nextMovie.asStateFlow()


    init {
        loadLogs()
    }

    private fun loadLogs() {
        Log.d(TAG, "Load Logs")
        _allLogs.value = localLogRepository.getLogs()
       /* sortLogsByOwnerPriority()*/
    }

    fun onMove(from: Int, to: Int) {
        Log.d(TAG, "From: $from To: $to")
        _allLogs.value = _allLogs.value!!.toMutableList().apply {
            add(to, removeAt(from))
        }
        localLogRepository.reorderLogs(allLogs.value!!)
    }

    fun addMovieToLog(logId: String?, movieId: String?) {
        localLogRepository.addMovieToLog(logId!!, movieId!!)
        loadLogs()
    }

    open fun getMovieById(movieId: String) {
        movieRepository.getMovieById(
            movieId = movieId,
            onResponse = { movieResponse ->
                _movie.value = movieResponse
                Log.d(TAG, "$movieResponse")
            },
            onFailure = { e ->
                Log.e(TAG, "Failed to fetch details for movie ID $movieId: $e")
            }
        )
    }

    data class MovieResult<T>(
        val data: T? = null,
        val error: String? = null
    )
    fun fetchMovieDetails(
        movieId: String,
        onResponse: (MovieResult<MovieData>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                movieRepository.getMovieById(movieId, { movieData ->
                    // Success response
                    if (movieData != null) {
                        onResponse(MovieResult(data = movieData))
                    } else {
                        onResponse(MovieResult(error = "No data received"))
                    }
                }, { errorMsg ->
                    // Error response
                    onResponse(MovieResult(error = errorMsg))
                })
            } catch (e: Exception) {
                onResponse(MovieResult(error = e.message ?: "An unknown error occurred"))
            }
        }
    }

    fun getNextMovieById(movieId: String) {
        movieRepository.getMovieById(
            movieId = movieId,
            onResponse = { movieResponse ->
                _nextMovie.value = movieResponse
            },
            onFailure = { e ->
                Log.e(TAG, "Failed to fetch details for movie ID $movieId: $e")
            }
        )
    }

    fun resetNextMovie() {
        _nextMovie.value = null // Reset the value
    }

    fun resetMovie() {
        _movie.value = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createLog(logName: String) {
        // Create an ID
        val id = UUID.randomUUID().toString()

        // Find the next priority
        val priority: Int = findMaxPriority() + 1
        Log.d(TAG, "Priority of new log: $priority")

        // Create the date
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)

        val owner = Owner(
            userId = null,
            priority = priority
        )

        val log = LogData(
            logId =  id,
            name = logName,
            isVisible = false,
            movieIds = emptyMap(),
            watchedIds = emptyMap(),
            owner = owner,
            collaborators = emptyMap(),
            creationDate = formattedDate,
            lastModifiedDate = formattedDate
        )
        Log.d(TAG, "Creating Log: $log")
        localLogRepository.createLog(log)
        loadLogs()
    }

    private fun findMaxPriority(): Int {
        var maxPriority = 0
        _allLogs.value?.forEach { log ->
            val temp = log.owner?.priority!!

            if (temp > maxPriority) {
                maxPriority = temp
            }
        }
        return maxPriority
    }
}