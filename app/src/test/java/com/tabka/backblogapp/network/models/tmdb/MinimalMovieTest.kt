package com.tabka.backblogapp.network.models.tmdb

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test

class MinimalMovieTest {

    private val testJson = """{"adult":true,"backdrop_path":"/backdrop.jpg","belongs_to_collection":{"id":1,"name":"Collection 1","poster_path":"/poster.jpg","backdrop_path":"/collection_backdrop.jpg"},"budget":10000000,"genres":[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}],"homepage":"http://example.com","id":123,"imdb_id":"tt1234567","original_language":"en","original_title":"Original Title","overview":"Movie overview","popularity":123.45,"poster_path":"/poster.jpg","production_companies":[{"id":1,"logo_path":"/company_logo.jpg","name":"Company 1","origin_country":"US"},{"id":2,"logo_path":"/company_logo.jpg","name":"Company 2","origin_country":"CA"}],"production_countries":[{"iso_3166_1":"US","name":"United States"},{"iso_3166_1":"CA","name":"Canada"}],"release_date":"2023-01-01","revenue":50000000,"runtime":120,"spoken_languages":[{"iso_639_1":"en","name":"English"},{"iso_639_1":"fr","name":"French"}],"status":"Released","tagline":"Movie Tagline","title":"Movie Title","video":true,"vote_average":8.5,"vote_count":1000,"images":{"backdrops":[{"aspect_ratio":1.78,"height":720,"iso_639_1":"en","file_path":"/backdrop1.jpg","vote_average":7.5,"vote_count":100,"width":1280}],"logos":[{"aspect_ratio":2.0,"height":200,"iso_639_1":"en","file_path":"/logo1.jpg","vote_average":8.0,"vote_count":200,"width":400}],"posters":[{"aspect_ratio":0.67,"height":1000,"iso_639_1":"en","file_path":"/poster1.jpg","vote_average":9.0,"vote_count":300,"width":670}]}}""".trimMargin()

    private val movieData = MinimalMovieDataResult(
        adult = true,
        backdropPath = "/backdrop.jpg",
        belongsToCollection = Collection(id = 1, name = "Collection 1", posterPath = "/poster.jpg", backdropPath = "/collection_backdrop.jpg"),
        budget = 10000000,
        genres = listOf(Genre(id = 28, name = "Action"), Genre(id = 12, name = "Adventure")),
        homepage = "http://example.com",
        id = 123,
        imdbId = "tt1234567",
        originalLanguage = "en",
        originalTitle = "Original Title",
        overview = "Movie overview",
        popularity = 123.45,
        posterPath = "/poster.jpg",
        productionCompanies = listOf(
            ProductionCompany(id = 1, logoPath = "/company_logo.jpg", name = "Company 1", originCountry = "US"),
            ProductionCompany(id = 2, logoPath = "/company_logo.jpg", name = "Company 2", originCountry = "CA")
        ),
        productionCountries = listOf(
            mapOf("iso_3166_1" to "US", "name" to "United States"),
            mapOf("iso_3166_1" to "CA", "name" to "Canada")
        ),
        releaseDate = "2023-01-01",
        revenue = 50000000,
        runtime = 120,
        spokenLanguages = listOf(
            mapOf("iso_639_1" to "en", "name" to "English"),
            mapOf("iso_639_1" to "fr", "name" to "French")
        ),
        status = "Released",
        tagline = "Movie Tagline",
        title = "Movie Title",
        video = true,
        voteAverage = 8.5,
        voteCount = 1000,
        images = MovieImages(
            backdrops = listOf(Image(aspectRatio = 1.78, height = 720, iso6391 = "en", filePath = "/backdrop1.jpg", voteAverage = 7.5, voteCount = 100, width = 1280)),
            logos = listOf(Image(aspectRatio = 2.0, height = 200, iso6391 = "en", filePath = "/logo1.jpg", voteAverage = 8.0, voteCount = 200, width = 400)),
            posters = listOf(Image(aspectRatio = 0.67, height = 1000, iso6391 = "en", filePath = "/poster1.jpg", voteAverage = 9.0, voteCount = 300, width = 670))
        )
    )

    @Test
    fun testSerializationSuccess() {
        val jsonString = Json.encodeToString(movieData)

        Assert.assertEquals(testJson, jsonString)
    }

    @Test
    fun testDeserializationSuccess() {
        val testData = Json.decodeFromString<MinimalMovieDataResult>(testJson)

        Assert.assertEquals(movieData.adult, testData.adult)
        Assert.assertEquals(movieData.backdropPath, testData.backdropPath)
        Assert.assertEquals(movieData.belongsToCollection, testData.belongsToCollection)
        Assert.assertEquals(movieData.budget, testData.budget)
        Assert.assertEquals(movieData.genres, testData.genres)
        Assert.assertEquals(movieData.homepage, testData.homepage)
        Assert.assertEquals(movieData.id, testData.id)
        Assert.assertEquals(movieData.imdbId, testData.imdbId)
        Assert.assertEquals(movieData.originalLanguage, testData.originalLanguage)
        Assert.assertEquals(movieData.originalTitle, testData.originalTitle)
        Assert.assertEquals(movieData.overview, testData.overview)
        Assert.assertEquals(movieData.popularity, testData.popularity)
        Assert.assertEquals(movieData.posterPath, testData.posterPath)
        Assert.assertEquals(movieData.productionCompanies, testData.productionCompanies)
        Assert.assertEquals(movieData.productionCountries, testData.productionCountries)
        Assert.assertEquals(movieData.releaseDate, testData.releaseDate)
        Assert.assertEquals(movieData.revenue, testData.revenue)
        Assert.assertEquals(movieData.runtime, testData.runtime)
        Assert.assertEquals(movieData.spokenLanguages, testData.spokenLanguages)
        Assert.assertEquals(movieData.status, testData.status)
        Assert.assertEquals(movieData.tagline, testData.tagline)
        Assert.assertEquals(movieData.title, testData.title)
        Assert.assertEquals(movieData.video, testData.video)
        Assert.assertEquals(movieData.voteAverage, testData.voteAverage)
        Assert.assertEquals(movieData.voteCount, testData.voteCount)
        Assert.assertEquals(movieData.images, testData.images)
    }
}