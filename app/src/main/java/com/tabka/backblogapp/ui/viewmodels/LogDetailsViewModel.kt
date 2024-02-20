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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    private suspend fun updateLogData(newLog: LogData) {
        logData.postValue(newLog)

        viewModelScope.launch {
            getUserData(isOwner = true)
            getUserData(isOwner = false)

            val moviesDeferred = getMovies()
            val watchedMoviesDeferred = getWatchedMovies()

            moviesDeferred.await()
            watchedMoviesDeferred.await()

            isLoading.postValue(false)

        }
    }

    private fun updateMovies(newList: MutableMap<String, MinimalMovieData>) {
        movies.postValue(newList)
    }

    private fun updateWatchedMovies(newList: MutableMap<String, MinimalMovieData>) {
        watchedMovies.postValue(newList)
    }

    private fun updateIsOwner(userIsOwner: Boolean) {
        isOwner.postValue(userIsOwner)
    }

    private fun updateOwner(user: UserData) {
        owner.postValue(user)
    }

    private fun updateCollaboratorsList(collaborators: List<UserData>) {
        collaboratorsList.postValue(collaborators)
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
                    withContext(Dispatchers.IO) {
                        Log.d(tag, "In dispatchers")
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

    private suspend fun getMovies(): Deferred<Unit> = coroutineScope {
        async {
            try {
                val movieIds = logData.value?.movieIds ?: listOf()
                val movieDataList = mutableMapOf<String, MinimalMovieData>()

                // Launch all fetch operations in parallel
                val fetchJobs = movieIds.map { movieId ->
                    async {
                        try {
                            val result = CompletableDeferred<MinimalMovieData?>()
                            movieRepository.getMovieById(
                                movieId = movieId,
                                onResponse = { movie ->
                                    val minimalData = MinimalMovieData(
                                        id = movie?.id.toString(),
                                        image = movie?.images?.backdrops?.get(0)?.filePath ?: "",
                                        title = movie?.title ?: ""
                                    )
                                    result.complete(minimalData)
                                },
                                onFailure = { e ->
                                    Log.e(tag, "Failed to fetch details for movie ID $movieId: $e")
                                    result.complete(null) // Complete with null on failure
                                }
                            )
                            result.await() // Wait for the callback to complete
                        } catch (e: Exception) {
                            Log.e(tag, "Error fetching movie ID $movieId: $e")
                            null // Return null in case of error
                        }
                    }
                }

                // Await all the operations to complete and filter out nulls
                val fetchedMovies = fetchJobs.awaitAll().filterNotNull()
                fetchedMovies.forEach { movie ->
                    movie.id?.let { movieDataList[it] = movie }
                }

                withContext(Dispatchers.IO) {
                    updateMovies(movieDataList)
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to fetch movie details: $e")
            }
            Unit
        }
    }

    private suspend fun getWatchedMovies(): Deferred<Unit> = coroutineScope {
        async {
            try {
                val movieIds = logData.value?.watchedIds ?: listOf()
                val movieDataList = mutableMapOf<String, MinimalMovieData>()

                // Launch all fetch operations in parallel
                val fetchJobs = movieIds.map { movieId ->
                    async {
                        try {
                            val result = CompletableDeferred<MinimalMovieData?>()
                            movieRepository.getMovieById(
                                movieId = movieId,
                                onResponse = { movie ->
                                    val minimalData = MinimalMovieData(
                                        id = movie?.id.toString(),
                                        image = movie?.images?.backdrops?.get(0)?.filePath ?: "",
                                        title = movie?.title ?: ""
                                    )
                                    result.complete(minimalData)
                                },
                                onFailure = { e ->
                                    Log.e(tag, "Failed to fetch details for movie ID $movieId: $e")
                                    result.complete(null) // Complete with null on failure
                                }
                            )
                            result.await() // Wait for the callback to complete
                        } catch (e: Exception) {
                            Log.e(tag, "Error fetching movie ID $movieId: $e")
                            null // Return null in case of error
                        }
                    }
                }

                // Await all the operations to complete and filter out nulls
                val fetchedMovies = fetchJobs.awaitAll().filterNotNull()
                fetchedMovies.forEach { movie ->
                    movie.id?.let { movieDataList[it] = movie }
                }

                withContext(Dispatchers.IO) {
                    updateWatchedMovies(movieDataList)
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to fetch movie details: $e")
            }
            Unit
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
