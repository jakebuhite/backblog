package com.tabka.backblogapp.network.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.BuildConfig
import com.tabka.backblogapp.network.ApiService
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchData
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.FirebaseError
import com.tabka.backblogapp.util.FirebaseExceptionType
import com.tabka.backblogapp.util.toJsonElement
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieRepository(private val movieApiService: ApiService) {

    private val db = Firebase.firestore
    private val tag = "MoviesRepo"

    suspend fun addMovie(logId: String, movieId: String): DataResult<Boolean> {
        return try {
            val updates = mapOf(
                "movie_ids.${movieId}" to true,
                "last_modified_date" to System.currentTimeMillis().toString()
            )

            db.collection("logs").document(logId).update(updates).await()

            Log.d(tag, "Log successfully updated!")
            DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error writing log document", e)
            DataResult.Failure(e)
        }
    }

    suspend fun markMovie(logId: String, movieId: String, watched: Boolean): DataResult<Boolean> {
        try {
            val updates = if (watched) {
                // Mark as watched
                mapOf(
                    "movie_ids.${movieId}" to FieldValue.delete(),
                    "watched_ids.${movieId}" to true,
                    "last_modified_date" to System.currentTimeMillis().toString()
                )
            } else {
                // Unmark as watched
                mapOf(
                    "movie_ids.${movieId}" to true,
                    "watched_ids.${movieId}" to FieldValue.delete(),
                    "last_modified_date" to System.currentTimeMillis().toString()
                )
            }

            db.collection("logs").document(logId).update(updates).await()

            Log.d(tag, "Log successfully updated!")
            return DataResult.Success(true)
        } catch (e: Exception) {
            Log.w(tag, "Error marking movie", e)
            return DataResult.Failure(e)
        }
    }

    suspend fun getWatchNextMovie(userId: String): DataResult<String> {
        try {
            // Log Id -> Movie Data
            val logRepository = LogRepository()
            when (val logsResult = logRepository.getLogs(userId, true)) {
                is DataResult.Success -> {
                    val logs: List<LogData> = logsResult.item

                    if (logs.isEmpty()) {
                        return DataResult.Failure(FirebaseError(FirebaseExceptionType.DOES_NOT_EXIST))
                    }

                    var priorityLog: LogData? = null
                    var highestPriority = Int.MAX_VALUE

                    logs.forEach { log ->
                        val userPriority: Int = if (log.owner?.userId == userId) {
                            log.owner.priority!!
                        } else {
                            log.collaborators?.get(userId)?.toMap()?.get("priority") as Int
                        }

                        if (userPriority < highestPriority && !log.movieIds.isNullOrEmpty()) {
                            highestPriority = userPriority
                            priorityLog = log
                        }
                    }

                    // No movies in any of the logs
                    if (priorityLog == null) {
                        return DataResult.Failure(FirebaseError(FirebaseExceptionType.DOES_NOT_EXIST))
                    }

                    return DataResult.Success(priorityLog!!.movieIds!!.keys.first())
                }
                is DataResult.Failure -> {
                    // Propagate the failure result
                    return logsResult
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "Error getting watch next movie", e)
            return DataResult.Failure(e)
        }
    }

    fun getMovieById(movieId: String, onResponse: (MovieData?) -> Unit, onFailure: (String) -> Unit) {
        val call = movieApiService.getMovieDetails(
            movieId = movieId,
            appendToResponse = "images,release_dates,watch/providers,credits",
            authorization = "Bearer " + BuildConfig.MOVIE_SECRET
        )

        call.enqueue(object : Callback<MovieData> {
            override fun onResponse(call: Call<MovieData>, response: Response<MovieData>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    val movieData = Json.decodeFromString<MovieData>(Json.encodeToString(movieResponse.toJsonElement()))
                    onResponse(movieData)
                } else {
                    onFailure("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MovieData>, t: Throwable) {
                onFailure("Failure: ${t.message}")
            }
        })
    }

    fun searchMovie(query: String, page: Int, onResponse: (MovieSearchData?) -> Unit, onFailure: (String) -> Unit) {
        val includeAdult = false
        val language = "en-US"

        Log.d(tag, query)

        val call = movieApiService.searchMovies(query, includeAdult, language, page, "Bearer " + BuildConfig.MOVIE_SECRET)

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
}
