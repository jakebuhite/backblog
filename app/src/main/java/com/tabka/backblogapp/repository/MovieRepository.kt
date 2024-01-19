package com.tabka.backblogapp.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MovieRepository {

    private val db = Firebase.firestore
    private val tag = "MoviesRepo"

    suspend fun addMovie(logId: String, movieId: String) {
        val logDoc = db.collection("logs").document(logId).get().await()

        if (!logDoc.exists()) {
            return
        }

        val logData = logDoc.data
        val updatedMovieIds = logData?.get("movie_ids") as? MutableMap<String, Any> ?: mutableMapOf()

        updatedMovieIds[movieId] = true

        db.collection("logs").document(logId).update(
            mapOf(
                "movie_ids" to updatedMovieIds,
                "last_modified_date" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun markMovieAsWatched(logId: String, movieId: String): String {
        try {
            val logDoc = db.collection("logs").document(logId).get().await()

            if (!logDoc.exists()) {
                return "Log not found."
            }

            val logData = logDoc.data
            val movieIds = logData?.get("movie_ids") as? MutableMap<String, Any> ?: mutableMapOf()

            if (!movieIds.containsKey(movieId)) {
                return "Movie Id not found."
            }

            val updatedMovieIds = movieIds.toMutableMap()
            updatedMovieIds.remove(movieId)

            val watchedIds = logData["watched_ids"] as? MutableMap<String, Any> ?: mutableMapOf()
            watchedIds[movieId] = true

            db.collection("logs").document(logId).update(
                mapOf(
                    "movie_ids" to updatedMovieIds,
                    "watched_ids" to watchedIds,
                    "last_modified_date" to System.currentTimeMillis()
                )
            ).await()

            return "Movie Id $movieId marked as watched."
        } catch (error: Exception) {
            return "Internal server error: ${error.message}"
        }
    }

    suspend fun unMarkMovieAsWatched(logId: String, movieId: String): String {
        try {
            val logDoc = db.collection("logs").document(logId).get().await()

            if (!logDoc.exists()) {
                return "Log not found."
            }

            val logData = logDoc.data
            val watchedIds = logData?.get("watched_ids") as? MutableMap<String, Any> ?: mutableMapOf()

            if (!watchedIds.containsKey(movieId)) {
                return "Movie Id not found."
            }

            val updatedWatchedIds = watchedIds.toMutableMap()
            updatedWatchedIds.remove(movieId)

            val movieIds = logData["movie_ids"] as? MutableMap<String, Any> ?: mutableMapOf()
            movieIds[movieId] = true

            db.collection("logs").document(logId).update(
                mapOf(
                    "movie_ids" to movieIds,
                    "watched_ids" to updatedWatchedIds,
                    "last_modified_date" to System.currentTimeMillis()
                )
            ).await()

            return "Movie Id $movieId unmarked as watched."
        } catch (error: Exception) {
            return "Internal server error: ${error.message}"
        }
    }

    suspend fun searchMovie(query: String, page: Int): Map<String, Any> {
        try {
            if (query.isEmpty()) {
                return mapOf("error" to "Query is required in the request body.")
            }

            val formattedQuery = query.replace(" ", "%20")
            val url = "https://api.themoviedb.org/3/search/movie?query=$formattedQuery&include_adult=false&language=en-US&page=$page"
            val options = mapOf(
                "method" to "GET",
                "headers" to mapOf(
                    "accept" to "application/json",
                    "Authorization" to "Bearer ${System.getenv("AUTHORIZATION_KEY")}"
                )
            )

            val response = /* perform HTTP request and get response */
            val jsonResponse = /* parse JSON response */

            // Add half-sheet movie image
                // ...

                return mapOf("data" to jsonResponse)
        } catch (error: Exception) {
            return mapOf("error" to "Internal server error: ${error.message}")
        }
    }

    suspend fun getMovieById(movieId: String): Map<String, Any> {
        try {
            if (movieId.isEmpty()) {
                return mapOf("error" to "Movie Id is required in the request body.")
            }

            val movieData = movieDataById(movieId, true)

            return mapOf("data" to movieData)
        } catch (error: Exception) {
            return mapOf("error" to "Internal server error: ${error.message}")
        }
    }

    suspend fun getWatchNextMovie(userId: String): Map<String, Any> {
        try {
            if (userId.isEmpty()) {
                return mapOf("error" to "User Id is required in the request body.")
            }

            val querySnapshot = db.collection("logs")
                .whereEqualTo("owner.user_id", userId)
                .orWhere("collaborators.$userId", "!=", null)
                .get().await()

            if (querySnapshot.isEmpty) {
                return mapOf("error" to "No logs found for the user.")
            }

            var priorityLog: Map<String, Any>? = null
            var highestPriority = Int.MAX_VALUE

            querySnapshot.forEach { doc ->
                val log = doc.data
                val userPriority = if (log["owner.user_id"] == userId)
                    log["owner.priority"] as Int
                else
                    (log["collaborators.$userId"] as Map<String, Any>)["priority"] as Int

                if (userPriority < highestPriority &&
                    log["movie_ids"] != null &&
                    (log["movie_ids"] as Map<String, Any>).isNotEmpty()
                ) {
                    highestPriority = userPriority
                    priorityLog = log
                }
            }

            if (priorityLog == null) {
                return mapOf("error" to "The watch next movie could not be found.")
            }

            val movieId = (priorityLog["movie_ids"] as Map<String, Any>).keys.first()
            val movieData = movieDataById(movieId, true)

            return mapOf("data" to movieData)
        } catch (error: Exception) {
            return mapOf("error" to "Internal server error: ${error.message}")
        }
    }

    // Other functions...

    private suspend fun movieDataById(movieId: String, isAdvanced: Boolean): Map<String, Any> {
        val url = "https://api.themoviedb.org/3/movie/$movieId?language=en-US" +
                if (isAdvanced) "&append_to_response=images,release_dates,watch/providers,credits" else ""

        val options = mapOf(
            "method" to "GET",
            "headers" to mapOf(
                "accept" to "application/json",
                "Authorization" to "Bearer ${System.getenv("AUTHORIZATION_KEY")}"
            )
        )

        val response = /* perform HTTP request and get response */
        val movieData = /* parse JSON response */

            return movieData
    }

    private suspend fun getMovieHalfSheet(movieId: String): String? {
        val url = "https://api.themoviedb.org/3/movie/$movieId/images?include_image_language=en"

        val options = mapOf(
            "method" to "GET",
            "headers" to mapOf(
                "accept" to "application/json",
                "Authorization" to "Bearer ${System.getenv("AUTHORIZATION_KEY")}"
            )
        )

        val response = /* perform HTTP request and get response */
        val data = /* parse JSON response */

            return if (data["backdrops"] != null && (data["backdrops"] as List<*>).isNotEmpty()) {
                (data["backdrops"] as List<*>)[0]["file_path"] as String?
            } else {
                null
            }
    }
}
