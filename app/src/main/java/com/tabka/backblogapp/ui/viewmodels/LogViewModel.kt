package com.tabka.backblogapp.ui.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.Executors


open class LogViewModel(
    val db: FirebaseFirestore = Firebase.firestore,
    val auth: FirebaseAuth = Firebase.auth,
    val movieRepository: MovieRepository = MovieRepository(db, ApiClient.movieApiService),
    val logRepository: LogRepository = LogRepository(db, auth),
    val localLogRepository: LogLocalRepository = LogLocalRepository()
) : ViewModel() {
    private val TAG = "LogViewModel"
    val singleThreadContext: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    // Log Data
    private val _allLogs = MutableStateFlow<List<LogData>?>(emptyList())
    open var allLogs = _allLogs.asStateFlow()

    // Up Next Movie
    private val _movie = MutableStateFlow<Pair<MovieData?, String>>(null to "")
    open val movie = _movie.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { _ ->
        Log.d(TAG, "We are here")
        viewModelScope.launch {
            loadLogs()
        }
    }
    init {
        auth.addAuthStateListener(authListener)
    }

    fun loadLogs() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "Getting the logs from DB: ${currentUser.uid}")
            viewModelScope.launch {
                val result = logRepository.getLogs(currentUser.uid, private = true)
                when (result) {
                    is DataResult.Success -> _allLogs.value = result.item
                    is DataResult.Failure -> {
                        Log.d(TAG, "Error getting logs from DB: ${result.throwable}")
                        //throw result.throwable
                    }
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

        Log.d(TAG, "New order: ${_allLogs.value}")
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val logIds = allLogs.value?.map { log ->
                if (log.owner!!.userId == currentUser.uid) {
                    Pair(log.logId ?: "", true)
                } else {
                    Pair(log.logId ?: "", false)
                }
            } ?: emptyList()

            Log.d(TAG, "NEW ORDER: $logIds")
            viewModelScope.launch(singleThreadContext) {
                logRepository.updateUserLogOrder(currentUser.uid, logIds)
            }
        } else {
            localLogRepository.reorderLogs(allLogs.value!!)
        }
    }

    fun addMovieToLog(logId: String?, movieId: String?) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                movieRepository.addMovie(logId ?: "", movieId ?: "")
                loadLogs()
            }
        } else {
            localLogRepository.addMovieToLog(logId!!, movieId!!)
            loadLogs()
        }
    }

    open fun getMovieById(movieId: String) {
        movieRepository.getMovieById(
            movieId = movieId,
            onResponse = { movieResponse ->
                movieRepository.getMovieHalfSheet(movieId = movieId,
                    onResponse = { halfSheet ->
                        _movie.value = Pair(movieResponse, halfSheet)
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Failed to fetch half sheet for movie ID $movieId: $e")
                    }
                )
            },
            onFailure = { e ->
                Log.e(TAG, "Failed to fetch details for movie ID $movieId: $e")
            }
        )
    }

    fun markMovieAsWatched(logId: String, movieId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                movieRepository.markMovie(logId, movieId, watched = true)
            }
        } else {
            localLogRepository.markMovie(logId, movieId, watched = true)
        }
        loadLogs()
    }

    fun unmarkMovieAsWatched(logId: String, movieId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                movieRepository.markMovie(logId, movieId, watched = false)
            }
        } else {
            localLogRepository.markMovie(logId, movieId, watched = false)
        }
        loadLogs()
    }

    data class MovieResult<T>(
        val data: T? = null,
        val error: String? = null
    )
    fun fetchMovieDetails(movieId: String, onResponse: (MovieResult<Pair<MovieData, String>>) -> Unit) {
        viewModelScope.launch {
            movieRepository.getMovieById(movieId, { movieData ->
                // Success response
                if (movieData != null) {
                    movieRepository.getMovieHalfSheet(movieId,
                        onResponse = { halfSheet ->
                            onResponse(MovieResult(movieData to halfSheet))
                        },
                        onFailure = {
                            onResponse(MovieResult(movieData to ""))
                        })
                } else {
                    onResponse(MovieResult(error = "No data received"))
                }
            }, { errorMsg ->
                // Error response
                onResponse(MovieResult(error = errorMsg))
            })
        }
    }

    fun resetMovie() {
        _movie.value = (null to "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createLog(logName: String, collaborators: List<String>, isVisible: Boolean) {
        val currentUser = auth.currentUser

        // If logged in
        if (currentUser != null) {
            Log.d(TAG, "User is logged in")
            viewModelScope.launch {
                val result = logRepository.addLog(logName, isVisible = isVisible, currentUser.uid)
                when (result) {
                    is DataResult.Success -> {
                        val logId = result.item
                        logRepository.addCollaborators(logId, collaborators)
                        loadLogs()
                    }
                    is DataResult.Failure -> {
                        Log.d(TAG, "There was an error creating a log: ${result.throwable}")
                    }
                }
            }
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
                collaborators = mutableListOf(),
                order = emptyMap(),
                creationDate = formattedDate,
                lastModifiedDate = formattedDate
            )
            Log.d(TAG, "Creating Log: $log")
            localLogRepository.createLog(log)
            loadLogs()
        }
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