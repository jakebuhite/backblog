//
//  MinimalMovieData.kt
//  backblog
//
//  Created by Christian Totaro on 2/27/24.
//
package com.tabka.backblogapp.network.models.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class MinimalMovieData(
    val id: String? = "",
    var image: String? = null,
    val title: String? = "",
)

@Serializable
data class MinimalMovieDataResult(
    val adult: Boolean?,
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("belongs_to_collection") val belongsToCollection: Collection?,
    val budget: Int?,
    val genres: List<Genre>?,
    val homepage: String?,
    val id: Int?,
    @SerialName("imdb_id") val imdbId: String?,
    @SerialName("original_language") val originalLanguage: String?,
    @SerialName("original_title") val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("production_companies") val productionCompanies: List<ProductionCompany>?,
    @SerialName("production_countries") val productionCountries: List<Map<String, String?>>?,
    @SerialName("release_date") val releaseDate: String?,
    val revenue: Long?,
    val runtime: Int?,
    @SerialName("spoken_languages") val spokenLanguages: List<Map<String, String?>>?,
    val status: String?,
    val tagline: String?,
    val title: String?,
    val video: Boolean?,
    @SerialName("vote_average") val voteAverage: Double?,
    @SerialName("vote_count") val voteCount: Int?,
    val images: MovieImages?,
)