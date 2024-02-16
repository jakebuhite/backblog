package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class LogDetailsViewModel: ViewModel() {
    private val tag = "LogDetailsViewModel"
    private val auth = Firebase.auth

    // Log data
    private val localRepository = LogLocalRepository()
    private val logRepository = LogRepository()
    open val logData: MutableLiveData<LogData> = MutableLiveData()

    // Movie data
    private val apiService = ApiClient.movieApiService
    private val movieRepository = MovieRepository(apiService)
    private val userRepository = UserRepository()

    open val movies: MutableLiveData<List<MovieData>> = MutableLiveData()
    open val watchedMovies: MutableLiveData<List<MovieData>> = MutableLiveData()
    val owner: MutableLiveData<UserData> = MutableLiveData()
    open val isOwner: MutableLiveData<Boolean> = MutableLiveData()
    open val collaboratorsList: MutableLiveData<List<UserData>> = MutableLiveData()

    private suspend fun updateLogData(newLog: LogData) {
        logData.value = newLog
        getMovies()
        getWatchedMovies()
        // Get the owner
        getUserData(isOwner = true)
        // Get the collaborators
        getUserData(isOwner = false)
        Log.d(tag, "New Log get: ${logData.value}")
    }

    private fun updateMovieList(newList: List<MovieData>) {
        movies.value = newList
        Log.d(tag, "Here are my updated movies: $newList")

    }

    private fun updateIsOwner(userIsOwner: Boolean) {
        isOwner.value = userIsOwner
    }

    private fun updateWatchedMovieList(newList: List<MovieData>) {
        watchedMovies.value = newList
    }

    private fun updateOwner(user: UserData) {
        owner.value = user
    }

    private fun updateCollaboratorsList(collaborators: List<UserData>) {
        collaboratorsList.value = collaborators
    }

    suspend fun updateLogCollaborators(collaboratorsToAdd: List<String>, collaboratorsToRemove: List<String>) {
        val logId = logData.value?.logId!!
        viewModelScope.launch {
            Log.d(tag, "Add: ${collaboratorsToAdd.toList()}\nRemove: ${collaboratorsToRemove.toList()}")
            logRepository.addCollaborators(logId, collaboratorsToAdd)
            logRepository.removeCollaborators(logId, collaboratorsToRemove)
            getLogData(logId)
        }
    }


    private suspend fun getUserData(isOwner: Boolean) {

        val userIds: Collection<String> = if (isOwner) {
            // If isOwner is true, create a list containing only the owner's userId
            listOfNotNull(logData.value?.owner?.userId)
        } else {
            // If isOwner is false, use the keys from collaborators
            logData.value?.collaborators ?: emptyList()
        }

        val collabList = mutableListOf<UserData>()

        for (userId in userIds) {
            val result: DataResult<UserData> = userRepository.getUser(userId)
            if (isOwner) {
                when (result) {
                    is DataResult.Success -> updateOwner(result.item)
                    is DataResult.Failure -> throw result.throwable
                }
            } else {
                when (result) {
                    is DataResult.Success -> collabList.add(result.item)
                    is DataResult.Failure -> throw result.throwable
                }
            }
        }
        if (!isOwner) {
            updateCollaboratorsList(collabList)
        }
    }

    open suspend fun getLogData(logId: String) {
        Log.d(tag, "Getting log data!")
        viewModelScope.launch {
            try {
                Log.d(tag, "Trying now!")
                //val user = auth.currentUser?.uid
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val result: DataResult<LogData> = logRepository.getLog(logId)
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is DataResult.Success -> {
                                updateLogData(result.item)
                                if (result.item.owner?.userId == currentUser.uid) {
                                    updateIsOwner(true)
                                } else {
                                    updateIsOwner(false)
                                }
                            }
                            is DataResult.Failure -> throw result.throwable
                        }
                    }
                } else {
                    val result = localRepository.getLogById(logId)
                    Log.d(tag, "Here is the log: $result")
                    if (result != null) {
                        Log.d(tag, "The log result: $result")
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
        val movieIds = logData.value?.movieIds ?: listOf()

        viewModelScope.launch {
            try {
                val movieDataList = mutableListOf<MovieData>()

                // Launch all fetch operations in parallel
                val fetchJobs = movieIds.map { movieId ->
                    async {
                        val deferredMovie = CompletableDeferred<MovieData?>()
                        movieRepository.getMovieById(
                            movieId = movieId,
                            onResponse = { movieResponse ->
                                deferredMovie.complete(movieResponse)
                            },
                            onFailure = { e ->
                                Log.e(tag, "Failed to fetch details for movie ID $movieId: $e")
                                deferredMovie.complete(null) // Complete with null on failure
                            }
                        )
                        deferredMovie.await() // Wait for the callback to complete
                    }
                }

                // Await all the operations to complete and filter out nulls
                fetchJobs.awaitAll().filterNotNull().also { fetchedMovies ->
                    movieDataList.addAll(fetchedMovies)
                }

                withContext(Dispatchers.Main) {
                    Log.d(tag, "Here are my movies: $movieDataList")
                    updateMovieList(movieDataList)
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to fetch details for movies: $e")
            }
        }
    }

    fun updateLog(newLogName: String, editedMovies: List<MovieData>) {
        Log.d(tag, "Update log! ${editedMovies.map { it.id }}")
        val logId = logData.value?.logId!!


        val newLogData = mapOf(
            "name" to newLogName,
            "movie_ids" to editedMovies.map { it.id.toString() },
        )

        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                val result = logRepository.updateLog(logId, newLogData)
                when (result) {
                    is DataResult.Success -> getLogData(logId)
                    is DataResult.Failure -> result.throwable
                }
            }
        } else {
            localRepository.updateLog(logId, newLogData)
            viewModelScope.launch {
                getLogData(logId)
            }
        }
    }

    fun shuffleMovies() {
        Log.d(tag, "Shuffle now!")
        val logId = logData.value?.logId!!

        val movies = logData.value?.movieIds ?: emptyList()
        val shuffledMovies = movies.shuffled()
        val newMovies = mapOf("movie_ids" to shuffledMovies)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                val result = logRepository.updateLog(logId, newMovies)
                when (result) {
                    is DataResult.Success -> getLogData(logId)
                    is DataResult.Failure -> result.throwable
                }
            }
        } else {
            localRepository.updateLog(logId, newMovies)
            viewModelScope.launch {
                getLogData(logId)
            }
        }
    }

    private suspend fun getWatchedMovies() {
        val movieDataList = mutableListOf<MovieData>()
        val watchedMovieIds = logData.value?.watchedIds ?: mutableListOf()

        viewModelScope.launch {
            try {
                watchedMovieIds.forEach { movieId ->
                    try {
                        val movieData = suspendCoroutine { cont ->
                            movieRepository.getMovieById(
                                movieId = movieId,
                                onResponse = { movieResponse ->
                                    cont.resume(movieResponse)
                                },
                                onFailure = { e ->
                                    Log.e("Movies", "Failed to fetch details for movie ID $movieId: $e")
                                    cont.resume(null) // Resume with null or consider signaling an error
                                }
                            )
                        }

                        movieData?.let { movieDataList.add(it) }
                    } catch (e: Exception) {
                        Log.e("Movies", "Error fetching movie ID $movieId: $e")
                    }
                }
                withContext(Dispatchers.Main) {
                    updateWatchedMovieList(movieDataList)
                }
            } catch (e: Exception) {
                Log.e("Movies", "Failed to fetch details for movies: $e")
            }
        }
    }

    open suspend fun deleteLog(): Job? {
        val logId = logData.value?.logId!!
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            viewModelScope.launch {
                logRepository.deleteLog(logId)
            }
        } else {
            localRepository.deleteLog(logId)
            null
        }
    }
}
