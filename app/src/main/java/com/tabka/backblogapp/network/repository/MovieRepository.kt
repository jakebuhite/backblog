package com.tabka.backblogapp.network.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.BuildConfig
import com.tabka.backblogapp.network.ApiService
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieRepository(private val movieApiService: ApiService) {

    private val db = Firebase.firestore
    private val tag = "MoviesRepo"

    fun addMovie(logId: String, movieId: String) {
        db.collection("logs").document(logId).update(
            mapOf(
                "movie_ids.${movieId}" to true,
                "last_modified_date" to System.currentTimeMillis().toString()
            )
        )
            .addOnSuccessListener { Log.d(tag, "Log successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing log document", e) }
    }

    fun markMovieAsWatched(logId: String, movieId: String) {
        var logData: LogData? = null
        db.collection("logs").document(logId).get()
            .addOnSuccessListener { doc ->
                Log.d(tag, "Log successfully received!")
                @Suppress("UNCHECKED_CAST")
                logData = LogData(
                    logId = doc.getString("log_id"),
                    name = doc.getString("name"),
                    creationDate = doc.getString("creation_date"),
                    lastModifiedDate = doc.getString("last_modified_date"),
                    isVisible = doc.getBoolean("status"),
                    owner = doc.data?.get("owner") as Map<String, Any>?,
                    collaborators = doc.data?.get("collaborators") as Map<String, Map<String, Int>>?,
                    movieIds = doc.data?.get("movie_ids") as Map<String, Boolean>?,
                    watchedIds = doc.data?.get("watched_ids") as Map<String, Boolean>?
                )
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving log document", e) }

        if (logData == null) {
            return
        }

        val movieIds = logData!!.movieIds!!.toMutableMap()
        if (!movieIds.containsKey(movieId)) {
            Log.d(tag, "Movie not found in log!")
            return
        }
        movieIds.remove(movieId)

        val watchedIds = logData!!.watchedIds!!.toMutableMap()
        if (watchedIds.containsKey(movieId)) {
            Log.d(tag, "Movie already found in watched!")
            return
        }
        watchedIds[movieId] = true

        db.collection("logs").document(logId).update(
            mapOf(
                "movie_ids" to movieIds,
                "watched_ids" to watchedIds,
                "last_modified_date" to System.currentTimeMillis().toString()
            )
        )
            .addOnSuccessListener { Log.d(tag, "Log successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing log document", e) }
    }

    fun unMarkMovieAsWatched(logId: String, movieId: String) {
        var logData: LogData? = null
        db.collection("logs").document(logId).get()
            .addOnSuccessListener { doc ->
                Log.d(tag, "Log successfully received!")
                @Suppress("UNCHECKED_CAST")
                logData = LogData(
                    logId = doc.getString("log_id"),
                    name = doc.getString("name"),
                    creationDate = doc.getString("creation_date"),
                    lastModifiedDate = doc.getString("last_modified_date"),
                    isVisible = doc.getBoolean("status"),
                    owner = doc.data?.get("owner") as Map<String, Any>?,
                    collaborators = doc.data?.get("collaborators") as Map<String, Map<String, Int>>?,
                    movieIds = doc.data?.get("movie_ids") as Map<String, Boolean>?,
                    watchedIds = doc.data?.get("watched_ids") as Map<String, Boolean>?
                )
            }
            .addOnFailureListener { e -> Log.w(tag, "Error receiving log document", e) }

        if (logData == null) {
            return
        }

        val watchedIds = logData?.watchedIds!!.toMutableMap()
        if (!watchedIds.containsKey(movieId)) {
            Log.d(tag, "Movie not found in log!")
            return
        }
        watchedIds.remove(movieId)

        val movieIds: MutableMap<String, Boolean> = logData!!.movieIds!!.toMutableMap()
        if (movieIds.containsKey(movieId)) {
            Log.d(tag, "Movie already found in log!")
            return
        }
        movieIds[movieId] = true

        db.collection("logs").document(logId).update(
            mapOf(
                "movie_ids" to movieIds,
                "watched_ids" to watchedIds,
                "last_modified_date" to System.currentTimeMillis()
            )
        )
            .addOnSuccessListener { Log.d(tag, "Log successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing log document", e) }
    }

    fun searchMovie(query: String, page: Int, onResponse: (MovieSearchData?) -> Unit, onFailure: (String) -> Unit) {
        val formattedQuery = query.replace(" ", "%20")
        val includeAdult = false
        val language = "en-US"
        Log.d("Movies", "here")

        val call = movieApiService.searchMovies(formattedQuery, includeAdult, language, page)

        call.enqueue(object : Callback<MovieSearchData> {
            override fun onResponse(call: Call<MovieSearchData>, response: Response<MovieSearchData>) {
                if (response.isSuccessful) {
                    val movieSearchData = response.body()
                    Log.d("Movies", "$movieSearchData")
                    onResponse(movieSearchData)
                } else {
                    // Handle error
                    Log.d("Movies", "Error: ${response.code()} - ${response.message()}")
                    println("Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MovieSearchData>, t: Throwable) {
                Log.d("Movies", "Failure: ${t.message}")
                onFailure("Failure: ${t.message}")
            }
        })
    }

    fun getWatchNextMovie(userId: String): Map<String, MovieData> {
        // Log Id -> Movie Data
        val logRepository = LogRepository()
        val logs: List<LogData> = logRepository.getLogs(userId)

        if (logs.isEmpty()) {
            return emptyMap()
        }

        var priorityLog: LogData? = null
        var highestPriority = Int.MAX_VALUE

        logs.forEach { log ->
            val userPriority: Int = if (log.owner?.get("userId") == userId)
                log.owner["priority"] as Int
            else (log.collaborators?.get(userId)?.toMap()?.get("priority") as Int)

            if (userPriority < highestPriority && !log.movieIds.isNullOrEmpty()) {
                highestPriority = userPriority
                priorityLog = log
            }
        }

        // No movies in any of the logs
        if (priorityLog == null) {
            return emptyMap()
        }

        val movieId = priorityLog!!.movieIds!!.keys.first()

        // Call getMovieById function with appropriate callbacks
        val result = mutableMapOf<String, MovieData>()

        getMovieById(
            movieId,
            onResponse = { movieData ->
                result[priorityLog!!.logId] to movieData
            },
            onFailure = { error ->
                Log.d(tag, error)
            }
        )

        // Return the result map
        return result
    }

    private fun getMovieById(movieId: String, onResponse: (MovieData?) -> Unit, onFailure: (String) -> Unit) {
        val call = movieApiService.getMovieDetails(
            movieId = movieId,
            appendToResponse = "images,release_dates,watch/providers,credits",
            authorization = "Bearer " + BuildConfig.MOVIE_SECRET
        )

        call.enqueue(object : Callback<MovieData> {
            override fun onResponse(call: Call<MovieData>, response: Response<MovieData>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    onResponse(movieResponse)
                } else {
                    onFailure("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MovieData>, t: Throwable) {
                onFailure("Failure: ${t.message}")
            }
        })
    }
}
