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
import com.tabka.backblogapp.network.models.tmdb.MinimalMovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val movies: MutableLiveData<MutableMap<String, MinimalMovieData>> = MutableLiveData(mutableMapOf())
    val watchedMovies: MutableLiveData<MutableMap<String, MinimalMovieData>> = MutableLiveData(mutableMapOf())


    //open val watchedMovies: MutableLiveData<List<MinimalMovieData>> = MutableLiveData()
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
    }

    private fun updateIsOwner(userIsOwner: Boolean) {
        isOwner.value = userIsOwner
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
        viewModelScope.launch {
            try {
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

    private fun getMovies() {
        val movieIds = logData.value?.movieIds ?: listOf()

        for (movieId in movieIds) {
            // Set movieId to empty val to maintain order
            var movieData = MinimalMovieData()
            movies.value?.set(movieId, movieData)

            try {
                // Assume getMinimalMovieDetailsById is a suspend function
                viewModelScope.launch {
                    movieData = movieRepository.getMinimalMovieDetailsById(movieId)
                    withContext(Dispatchers.Main) {
                        /*val tempMap = movies.value ?: mutableMapOf()
                        tempMap[movieId] = movieData
                        movies.value = tempMap*/
                        movies.value?.set(movieId, movieData)
                        Log.d(tag, "Movies: ${movies.value}")
                    }
                }
                //}
            } catch (e: Exception) {
                Log.e(tag, "Failed to fetch details for movie ID $movieId: $e")
            }
        }
    }

    private fun getWatchedMovies() {
        val movieIds = logData.value?.watchedIds ?: listOf()

        for (movieId in movieIds) {
            // Set movieId to empty val to maintain order
            var movieData = MinimalMovieData()
            watchedMovies.value?.set(movieId, movieData)

            try {
                // Assume getMinimalMovieDetailsById is a suspend function
                viewModelScope.launch {
                    movieData = movieRepository.getMinimalMovieDetailsById(movieId)
                    withContext(Dispatchers.Main) {
                        watchedMovies.value?.set(movieId, movieData)
                        Log.d(tag, "Movies: ${movies.value}")
                    }
                }
                //}
            } catch (e: Exception) {
                Log.e(tag, "Failed to fetch details for movie ID $movieId: $e")
            }
        }
    }


    fun updateLog(newLogName: String, editedMovies: List<MinimalMovieData>) {
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
