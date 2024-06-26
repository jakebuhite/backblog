package com.tabka.backblogapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MinimalMovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.toJsonElement
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

open class LogDetailsViewModel(
    val logId: String,
    private val db: FirebaseFirestore = Firebase.firestore,
    val auth: FirebaseAuth = Firebase.auth,
    private val localRepository: LogLocalRepository = LogLocalRepository(),
    val logRepository: LogRepository = LogRepository(db, auth),
    private val movieRepository: MovieRepository = MovieRepository(db, ApiClient.movieApiService),
    val userRepository: UserRepository = UserRepository(db, auth),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {
    private val tag = "LogDetailsViewModel"

    // Log data
    open val logData: MutableLiveData<LogData> = MutableLiveData()

    //val movies: MutableLiveData<MutableMap<String, MinimalMovieData>> = MutableLiveData(mutableMapOf())
    open var movies: MutableLiveData<Map<String, MinimalMovieData>> = MutableLiveData(mapOf())
    open val watchedMovies: MutableLiveData<Map<String, MinimalMovieData>> = MutableLiveData(mapOf())


    //open val watchedMovies: MutableLiveData<List<MinimalMovieData>> = MutableLiveData()
    open val owner: MutableLiveData<UserData> = MutableLiveData()
    open val isOwner: MutableLiveData<Boolean> = MutableLiveData()
    open val isCollaborator: MutableLiveData<Boolean> = MutableLiveData()
    open val collaboratorsList: MutableLiveData<List<UserData>> = MutableLiveData()

    open val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    // Listeners
    private var logListener: ListenerRegistration? = null

    init {
        viewModelScope.launch {
            initLogListener()
        }
    }

    private suspend fun updateLogData(newLog: LogData) {
        withContext(dispatcher) {
            // Directly set value if called from a coroutine to ensure immediate update
            logData.value = newLog
            Log.d(tag, "New Log: ${logData.value} - $newLog")
        }
        coroutineScope {
            Log.d(tag, "Updated logData: ${logData.value}")

            if (auth.currentUser != null) {
                getUserData(isOwner = true)
                getUserData(isOwner = false)
            } else {
                updateIsOwner(true)
            }

            val moviesDeferred = getMovies()
            val watchedMoviesDeferred = getWatchedMovies()

            moviesDeferred.await()
            watchedMoviesDeferred.await()

        }
        withContext(dispatcher) {
            // Directly set value if called from a coroutine to ensure immediate update
            isLoading.value = false
        }
    }

    private fun updateMovies(newList: Map<String, MinimalMovieData>) {
        movies.value = newList
        // Force refresh for sort
        if (movies.value?.isNotEmpty() == true) {
            val movieId = movies.value?.keys?.last() ?: return
            val movieData = movies.value?.get(movieId) ?: return
            val newMovies = movies.value?.toMutableMap() ?: mutableMapOf()
            newMovies.remove(movieId)

            // Update
            movies.value = newMovies

            // Revert back to original
            newMovies[movieId] = movieData
            movies.value = newMovies
        }
    }

    private fun updateWatchedMovies(newList: Map<String, MinimalMovieData>) {
        watchedMovies.value = newList
        // Force refresh for sort
        if (watchedMovies.value?.isNotEmpty() == true) {
            val movieId = watchedMovies.value?.keys?.last() ?: return
            val movieData = watchedMovies.value?.get(movieId) ?: return
            val newMovies = watchedMovies.value?.toMutableMap() ?: mutableMapOf()
            newMovies.remove(movieId)

            // Update
            watchedMovies.value = newMovies

            // Revert back to original
            newMovies[movieId] = movieData
            watchedMovies.value = newMovies
        }
    }

    private fun updateIsOwner(userIsOwner: Boolean) {
        isOwner.postValue(userIsOwner)
    }

    private fun updateOwner(user: UserData) {
        owner.postValue(user)
    }

    private fun updateIsCollaborator(userIsCollaborator: Boolean) {
        isCollaborator.postValue(userIsCollaborator)
    }

    private fun updateCollaboratorsList(collaborators: List<UserData>) {
        collaboratorsList.postValue(collaborators)
    }

    suspend fun updateLogCollaborators(collaboratorsToAdd: List<String>, collaboratorsToRemove: List<String>) {
        val logId = logData.value?.logId!!
        viewModelScope.launch(Dispatchers.IO) {
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

    open fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    open fun getLogData(logId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val result: DataResult<LogData> = logRepository.getLog(logId)
                    withContext(dispatcher) {
                        when (result) {
                            is DataResult.Success -> {
                                updateLogData(result.item)
                                if (result.item.owner?.userId == currentUser.uid) {
                                    updateIsOwner(true)
                                } else {
                                    updateIsOwner(false)
                                    updateIsCollaborator(isCollaborator())
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
                withContext(dispatcher) {
                    Log.d(tag, "Error: $e")
                }
            }
        }
    }

    private suspend fun getMovies(): Deferred<Unit> = coroutineScope {
        val movieIds = logData.value?.movieIds ?: listOf()
        Log.d(tag, "Movie IDS: $movieIds")
        async(Dispatchers.IO) {
            try {
                val movieDataList = mutableMapOf<String, MinimalMovieData>()

                // Launch all fetch operations in parallel
                val fetchJobs = movieIds.map { movieId ->
                    Log.d(tag, movieId)
                    async(Dispatchers.IO) {
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
                Log.d(tag, "Movie List: $movieDataList")

                withContext(dispatcher) {
                    updateMovies(movieDataList)
                }
            } catch (e: Exception) {
                withContext(dispatcher) {
                    Log.e(tag, "Failed to fetch movie details: $e")
                }
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

                withContext(dispatcher) {
                    updateWatchedMovies(movieDataList)
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to fetch movie details: $e")
            }
            Unit
        }
    }



    fun updateLog(newLogName: String, editedMovies: List<MinimalMovieData>, editedWatchedMovies: List<MinimalMovieData>) {
        Log.d(tag, "Update log! ${editedMovies.map { it.id }}")
        val logId = logData.value?.logId!!


        val newLogData = mapOf(
            "name" to newLogName,
            "movie_ids" to editedMovies.map { it.id.toString() },
            "watched_ids" to editedWatchedMovies.map { it.id.toString() }
        )

        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = logRepository.updateLog(logId, newLogData)) {
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

        Log.d(tag, "Old Movie order: $movies")

        val currentMoviesMap = movies.value ?: mutableMapOf()

        val shuffledList = currentMoviesMap.toList().shuffled()
        //val shuffledMap = shuffledList.toMap().toMutableMap()

        //val shuffledMovies = moviesIds.shuffled()
        val newMovies = mapOf("movie_ids" to shuffledList.map{ it.first})

        Log.d(tag, "New Movie order: $newMovies")
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val result = logRepository.updateLog(logId, newMovies)) {
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

    open suspend fun leaveLog(): Job? {
        val logId = logData.value?.logId!!
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            viewModelScope.launch {
                logRepository.removeCollaborators(logId, listOf(currentUser.uid))
            }
        } else null
    }

    fun isCollaborator(): Boolean {
        val userId = auth.currentUser?.uid ?: ""
        val collaborators = logData.value?.collaborators ?: mutableListOf()

        return userId in collaborators
    }

/*    suspend fun leaveLog() {
        if (auth.currentUser?.uid == null) {
            return
        }

        val currentUser = auth.currentUser?.uid!!
        viewModelScope.launch {
            logRepository.removeCollaborators(logId, listOf(currentUser))
        }
    }*/

    private suspend fun initLogListener() {
        if (auth.currentUser?.uid == null) {
            getLogData(logId)
            return
        }
        val userId = auth.currentUser?.uid!!

        logListener = db.collection("logs").document(logId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d(tag, error.localizedMessage ?: "Error with snapshot listener")
                    return@addSnapshotListener
                }

                if (value?.exists() == true) {
                    val log: LogData = Json.decodeFromString(Json.encodeToString(value.data.toJsonElement()))
                    viewModelScope.launch {
                        updateLogData(log)
                        updateIsOwner(userId == log.owner?.userId)
                        updateIsCollaborator(isCollaborator())
                    }
                } else {
                    // Document was likely to be deleted
                    logData.value = LogData(
                        logId = null,
                        name = null,
                        creationDate = null,
                        lastModifiedDate = null,
                        isVisible = null,
                        owner = null,
                        collaborators = null,
                        order = null,
                        movieIds = null,
                        watchedIds = null
                    )
                    updateIsOwner(false)
                    updateIsCollaborator(false)
                    updateMovies(mapOf())
                    updateWatchedMovies(mapOf())
                    updateCollaboratorsList(listOf())
                }
            }
    }
}
