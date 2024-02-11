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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    private val userRepository = UserRepository()

    val movies: MutableLiveData<List<MovieData>> = MutableLiveData()
    val watchedMovies: MutableLiveData<List<MovieData>> = MutableLiveData()
    val owner: MutableLiveData<UserData> = MutableLiveData()
    val collaboratorsList: MutableLiveData<List<UserData>> = MutableLiveData()

    private suspend fun updateLogData(newLog: LogData) {
        logData.value = newLog
        Log.d("Testing", "MY LOG: ${logData.value}")
        getMovies()
        getWatchedMovies()
        // Get the owner
        getUserData(isOwner = true)

        // Get the collaborators
        getUserData(isOwner = false)
    }

    private fun updateMovieList(newList: List<MovieData>) {
        movies.value = newList
    }

    private fun updateWatchedMovieList(newList: List<MovieData>) {
        Log.d("Testing", "Updating watched movie: $newList")
        watchedMovies.value = newList
    }

    private fun updateOwner(user: UserData) {
        owner.value = user
    }

    private fun updateCollaboratorsList(user: UserData) {
        val currentList = collaboratorsList.value ?: emptyList()
        val updatedList = currentList + user
        collaboratorsList.postValue(updatedList)
    }

    private suspend fun getUserData(isOwner: Boolean) {

        val userIds: Collection<String> = if (isOwner) {
            // If isOwner is true, create a list containing only the owner's userId
            listOfNotNull(logData.value?.owner?.userId)
        } else {
            // If isOwner is false, use the keys from collaborators
            logData.value?.collaborators?.keys ?: emptyList()
        }

        for (userId in userIds) {
            val result: DataResult<UserData> = userRepository.getUser(userId)
            if (isOwner) {
                when (result) {
                    is DataResult.Success -> updateOwner(result.item)
                    is DataResult.Failure -> throw result.throwable
                }
            } else {
                when (result) {
                    is DataResult.Success -> updateCollaboratorsList(result.item)
                    is DataResult.Failure -> throw result.throwable
                }
            }
        }
    }

    suspend fun getLogData(logId: String) {
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
                            is DataResult.Success -> updateLogData(result.item)
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
        val movieDataList = mutableListOf<MovieData>()
        val movieIds = logData.value?.movieIds ?: mutableListOf()

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
                    withContext(Dispatchers.Main) {
                        updateMovieList(movieDataList)
                    }
                }
            } catch (e: Exception) {
                Log.e("Movies", "Failed to fetch details for movies: $e")
            }
        }
    }

    suspend fun getWatchedMovies() {
        val movieDataList = mutableListOf<MovieData>()
        val watchedMovieIds = logData.value?.watchedIds ?: mutableListOf()
        Log.d("Testing", "Here are the watchedMovieIds: $watchedMovieIds")

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
}
