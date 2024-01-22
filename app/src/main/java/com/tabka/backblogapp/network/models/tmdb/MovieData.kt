package com.tabka.backblogapp.network.models.tmdb

import com.google.gson.annotations.SerializedName

data class MovieData(
    val adult: Boolean?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("belongs_to_collection") val belongsToCollection: Any?,
    val budget: Int?,
    val genres: List<Genre>?,
    val homepage: String?,
    val id: Int?,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("original_title") val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("production_companies") val productionCompanies: List<ProductionCompany>?,
    @SerializedName("production_countries") val productionCountries: List<Map<String, String?>>,
    @SerializedName("release_date") val releaseDate: String?,
    val revenue: Int?,
    val runtime: Int?,
    @SerializedName("spoken_languages") val spokenLanguages: List<Map<String, String?>>,
    val status: String?,
    val tagline: String?,
    val title: String?,
    val video: Boolean?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    val images: MovieImages?,
    @SerializedName("release_dates") val releaseDates: ReleaseDates?,
    @SerializedName("watch/providers") val watchProviders: WatchProviders?,
    val credits: Credits?
)

data class Genre(
    val id: Int?,
    val name: String?
)

data class ProductionCompany(
    val id: Int?,
    @SerializedName("logo_path") val logoPath: String?,
    val name: String?,
    @SerializedName("origin_country") val originCountry: String?
)

data class MovieImages(
    val backdrops: List<Map<String, Any?>>,
    val logos: List<Map<String, Any?>>?,
    val posters: List<Map<String, Any?>>?
)

data class ReleaseDates(
    val results: List<Map<String, Any?>>
)

data class WatchProviders(
    val results: Map<String?, WatchProviderResult>?
)

data class WatchProviderResult(
    val link: String?,
    val flatrate: List<Flatrate>?
)

data class Flatrate(
    @SerializedName("logo_path") val logoPath: String?,
    @SerializedName("provider_id") val providerId: Int?,
    @SerializedName("provider_name") val providerName: String?,
    @SerializedName("display_priority") val displayPriority: Int?
)

data class Credits(
    val cast: List<Cast>?
)

data class Cast(
    val adult: Boolean?,
    val gender: Int?,
    val id: Int?,
    @SerializedName("known_for_department") val knownForDepartment: String?,
    val name: String?,
    @SerializedName("original_name") val originalName: String?,
    val popularity: Double?,
    @SerializedName("profile_path") val profilePath: String?,
    @SerializedName("cast_id") val castId: Int?,
    val character: String?,
    @SerializedName("credit_id") val creditId: String?,
    val order: Int?
)