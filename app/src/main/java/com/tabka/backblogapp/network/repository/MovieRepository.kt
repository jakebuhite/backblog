//
//  MovieRepository.kt
//  backblog
//
//  Created by Jake Buhite on 2/9/24.
//
package com.tabka.backblogapp.network.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.BuildConfig
import com.tabka.backblogapp.network.ApiService
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.models.tmdb.MovieImageData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchData
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.FirebaseError
import com.tabka.backblogapp.util.FirebaseExceptionType
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieRepository(private val movieApiService: ApiService) {

    private val db = Firebase.firestore
    private val tag = "MoviesRepo"

    suspend fun addMovie(logId: String, movieId: String): DataResult<Boolean> {
        return try {
            val updates = mapOf(
                "movie_ids" to FieldValue.arrayUnion(movieId),
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
                    "movie_ids" to FieldValue.arrayRemove(movieId),
                    "watched_ids" to FieldValue.arrayUnion(movieId),
                    "last_modified_date" to System.currentTimeMillis().toString()
                )
            } else {
                // Unmark as watched
                mapOf(
                    "movie_ids" to FieldValue.arrayUnion(movieId),
                    "watched_ids" to FieldValue.arrayRemove(movieId),
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
                    Log.d("Movies", movieResponse.toString())
                    onResponse(movieResponse)
                } else {
                    onFailure("Error: $response")
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

    fun searchMoviesByGenre(page: Int, genreId: String, onResponse: (MovieSearchData?) -> Unit, onFailure: (String) -> Unit) {
        val includeAdult = false
        val includeVideo = false
        val language = "en-US"
        val sortBy = "popularity.desc"

        val call = movieApiService.searchMoviesByGenre(includeAdult, includeVideo, language, page, sortBy, genreId, "Bearer " + BuildConfig.MOVIE_SECRET)

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

    fun getMovieHalfSheet(
        movieId: String,
        onResponse: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val language = "en"
        val call = movieApiService.getMovieHalfSheet(movieId, language, "Bearer " + BuildConfig.MOVIE_SECRET)

        call.enqueue(object : Callback<MovieImageData> {
            override fun onResponse(call: Call<MovieImageData>, response: Response<MovieImageData>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    if (!movieResponse?.backdrops.isNullOrEmpty()) {
                        val filePath = movieResponse?.backdrops?.get(0)?.filePath ?: ""
                        onResponse(filePath)
                    } else {
                        onFailure("No backdrops available.")
                    }
                } else {
                    onFailure("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MovieImageData>, t: Throwable) {
                onFailure("Failure: ${t.message}")
            }
        })
    }
}
