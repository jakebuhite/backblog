package com.tabka.backblogapp.ui.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.util.DataResult
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

    private val logRepository = LogRepository()

    // Up Next Movie
    private val _movie = MutableStateFlow<MovieData?>(null)
    open val movie = _movie.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        Log.d(TAG, "We are here")
        viewModelScope.launch {
            loadLogs()
        }
    }
    init {
        Firebase.auth.addAuthStateListener(authListener)
    }

    private fun loadLogs() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "Getting the logs from DB: ${currentUser.uid}")
            viewModelScope.launch {
                val result = logRepository.getLogs(currentUser.uid, private = false)
                when (result) {
                    is DataResult.Success -> _allLogs.value = result.item
                    is DataResult.Failure -> throw result.throwable
                }
                Log.d(TAG, "Here are the results from DB: $result")
            }
        } else {
            _allLogs.value = localLogRepository.getLogs()
        }
    }

    fun onMove(from: Int, to: Int) {
        Log.d(TAG, "From: $from To: $to")
        _allLogs.value = _allLogs.value!!.toMutableList().apply {
            add(to, removeAt(from))
        }
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val logIds = allLogs.value?.map { log ->
                if (log.owner!!.userId == currentUser.uid) {
                    Pair(log.logId ?: "", true)
                } else {
                    Pair(log.logId ?: "", false)
                }
            } ?: emptyList()

            Log.d(TAG, "NEW ORDER: $logIds")
            viewModelScope.launch {
                logRepository.updateUserLogOrder(currentUser.uid, logIds)
            }
        } else {
            localLogRepository.reorderLogs(allLogs.value!!)
        }
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

    fun markMovieAsWatched(logId: String, movieId: String) {
        localLogRepository.markMovie(logId, movieId, watched = true)
        loadLogs()
    }

    fun unmarkMovieAsWatched(logId: String, movieId: String) {
        localLogRepository.markMovie(logId, movieId, watched = false)
        loadLogs()
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

    fun resetMovie() {
        _movie.value = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createLog(logName: String, collaborators: List<String>) {
        val currentUser = Firebase.auth.currentUser

        // If logged in
        if (currentUser != null) {
            Log.d(TAG, "User is logged in")
            /*viewModelScope.launch {
                val result = logRepository.addLog(logName, isVisible = true, currentUser.uid)
                when (result) {
                    is DataResult.Success -> {
                        val logId = result.item
                        logRepository.addCollaborators(logId, collaborators)
                    }
                    is DataResult.Failure -> throw result.throwable
                }
            }*/
        } else {
            Log.d(TAG, "User is not logged in")
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
                userId = id,
                priority = priority
            )

            val log = LogData(
                logId = id,
                name = logName,
                isVisible = false,
                movieIds = mutableListOf(),
                watchedIds = mutableListOf(),
                owner = owner,
                collaborators = emptyMap(),
                creationDate = formattedDate,
                lastModifiedDate = formattedDate
            )
            Log.d(TAG, "Creating Log: $log")
            localLogRepository.createLog(log)
        }
        loadLogs()
        resetMovie()
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