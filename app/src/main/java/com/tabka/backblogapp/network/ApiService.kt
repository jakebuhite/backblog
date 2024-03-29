//
//  ApiService.kt
//  backblog
//
//  Created by Jake Buhite on 2/14/24.
//
package com.tabka.backblogapp.network

import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.models.tmdb.MovieImageData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("movie/{movieId}")
    fun getMovieDetails(
        @Path("movieId") movieId: String,
        @Query("append_to_response") appendToResponse: String,
        @Header("Authorization") authorization: String
    ): Call<MovieData>

    @GET("movie/{movieId}/images")
    fun getMovieHalfSheet(
        @Path("movieId") movieId: String,
        @Query("include_image_language") movieLanguage: String,
        @Header("Authorization") authorization: String
    ): Call<MovieImageData>

    @GET("search/movie")
    fun searchMovies(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Header("Authorization") authorization: String
    ): Call<MovieSearchData>

    @GET("discover/movie")
    fun searchMoviesByGenre(
        @Query("include_adult") includeAdult: Boolean,
        @Query("include_video") includeVideo: Boolean,
        @Query("language") language: String,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String,
        @Query("with_genres") withGenres: String,
        @Header("Authorization") authorization: String
    ): Call<MovieSearchData>
}
