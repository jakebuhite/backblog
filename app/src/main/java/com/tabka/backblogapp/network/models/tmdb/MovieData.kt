package com.tabka.backblogapp.network.models.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieData(
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
    val revenue: Int?,
    val runtime: Int?,
    @SerialName("spoken_languages") val spokenLanguages: List<Map<String, String?>>?,
    val status: String?,
    val tagline: String?,
    val title: String?,
    val video: Boolean?,
    @SerialName("vote_average") val voteAverage: Double?,
    @SerialName("vote_count") val voteCount: Int?,
    val images: MovieImages?,
    @SerialName("release_dates") val releaseDates: ReleaseDates?,
    @SerialName("watch/providers") val watchProviders: WatchProviders?,
    val credits: Credits?
)

@Serializable
data class Collection(
    val id: Int?,
    val name: String?,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("backdrop_path") val backdropPath: String?
)

@Serializable
data class Genre(
    val id: Int?,
    val name: String?
)

@Serializable
data class ProductionCompany(
    val id: Int?,
    @SerialName("logo_path") val logoPath: String?,
    val name: String?,
    @SerialName("origin_country") val originCountry: String?
)

@Serializable
data class MovieImages(
    val backdrops: List<Image>?,
    val logos: List<Image>?,
    val posters: List<Image>?
)

@Serializable
data class Image(
    @SerialName("aspect_ratio") val aspectRatio: Double?,
    val height: Int?,
    @SerialName("iso_639_1") val iso6391: String?,
    @SerialName("file_path") val filePath: String?,
    @SerialName("vote_average") val voteAverage: Double?,
    @SerialName("vote_count") val voteCount: Int?,
    val width: Int?
)

@Serializable
data class ReleaseDates(
    val results: List<ReleaseDate>?
)

@Serializable
data class ReleaseDate(
    @SerialName("iso_3166_1") val iso31661: String?,
    @SerialName("release_dates") val releaseDates: List<ReleaseDateItem>?
)

@Serializable
data class ReleaseDateItem(
    val certification: String?,
    val descriptors: List<String?>?,
    @SerialName("iso_639_1") val iso6391: String?,
    val note: String?,
    @SerialName("release_date") val releaseDate: String?,
    val type: Int
)

@Serializable
data class WatchProviders(
    val results: Map<String?, WatchProviderResult>?
)

@Serializable
data class WatchProviderResult(
    val link: String?,
    val buy: List<Service>?,
    val rent: List<Service>?,
    val flatrate: List<Service>?
)

@Serializable
data class Service(
    @SerialName("logo_path") val logoPath: String?,
    @SerialName("provider_id") val providerId: Int?,
    @SerialName("provider_name") val providerName: String?,
    @SerialName("display_priority") val displayPriority: Int?
)

@Serializable
data class Credits(
    val cast: List<Cast>?,
    val crew: List<Crew>?
)

@Serializable
data class Cast(
    val adult: Boolean?,
    val gender: Int?,
    val id: Int?,
    @SerialName("known_for_department") val knownForDepartment: String?,
    val name: String?,
    @SerialName("original_name") val originalName: String?,
    val popularity: Double?,
    @SerialName("profile_path") val profilePath: String?,
    @SerialName("cast_id") val castId: Int?,
    val character: String?,
    @SerialName("credit_id") val creditId: String?,
    val order: Int?
)

@Serializable
data class Crew(
    val adult: Boolean?,
    val gender: Int?,
    val id: Int?,
    @SerialName("known_for_department") val knownForDepartment: String?,
    val name: String?,
    @SerialName("original_name") val originalName: String?,
    val popularity: Double?,
    @SerialName("profile_path") val profilePath: String?,
    @SerialName("credit_id") val creditId: String?,
    val department: String?,
    val job: String?
)