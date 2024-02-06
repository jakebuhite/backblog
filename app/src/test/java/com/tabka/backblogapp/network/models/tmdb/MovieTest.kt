package com.tabka.backblogapp.network.models.tmdb

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test

class MovieDataTest {

    private val testJson = """{"adult":true,"backdrop_path":"/backdrop.jpg","belongs_to_collection":{"id":1,"name":"Collection 1","poster_path":"/poster.jpg","backdrop_path":"/collection_backdrop.jpg"},"budget":10000000,"genres":[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}],"homepage":"http://example.com","id":123,"imdb_id":"tt1234567","original_language":"en","original_title":"Original Title","overview":"Movie overview","popularity":123.45,"poster_path":"/poster.jpg","production_companies":[{"id":1,"logo_path":"/company_logo.jpg","name":"Company 1","origin_country":"US"},{"id":2,"logo_path":"/company_logo.jpg","name":"Company 2","origin_country":"CA"}],"production_countries":[{"iso_3166_1":"US","name":"United States"},{"iso_3166_1":"CA","name":"Canada"}],"release_date":"2023-01-01","revenue":50000000,"runtime":120,"spoken_languages":[{"iso_639_1":"en","name":"English"},{"iso_639_1":"fr","name":"French"}],"status":"Released","tagline":"Movie Tagline","title":"Movie Title","video":true,"vote_average":8.5,"vote_count":1000,"images":{"backdrops":[{"aspect_ratio":1.78,"height":720,"iso_639_1":"en","file_path":"/backdrop1.jpg","vote_average":7.5,"vote_count":100,"width":1280}],"logos":[{"aspect_ratio":2.0,"height":200,"iso_639_1":"en","file_path":"/logo1.jpg","vote_average":8.0,"vote_count":200,"width":400}],"posters":[{"aspect_ratio":0.67,"height":1000,"iso_639_1":"en","file_path":"/poster1.jpg","vote_average":9.0,"vote_count":300,"width":670}]},"release_dates":{"results":[{"iso_3166_1":"US","release_dates":[{"certification":"PG-13","descriptors":["3D"],"iso_639_1":"en","note":"Note 1","release_date":"2023-01-01","type":1},{"certification":"R","descriptors":null,"iso_639_1":"en","note":"Note 2","release_date":"2023-01-02","type":1}]},{"iso_3166_1":"CA","release_dates":[{"certification":"PG","descriptors":["IMAX"],"iso_639_1":"en","note":"Note 3","release_date":"2023-01-03","type":1}]}]},"watch/providers":{"results":{"CA":{"link":"http://watch.ca","buy":[{"logo_path":"/buy_logo.jpg","provider_id":1,"provider_name":"Buy Provider 1","display_priority":1}],"rent":[{"logo_path":"/rent_logo.jpg","provider_id":2,"provider_name":"Rent Provider 1","display_priority":2}],"flatrate":[{"logo_path":"/flatrate_logo.jpg","provider_id":3,"provider_name":"Flatrate Provider 1","display_priority":3}]},"US":{"link":"http://watch.us","buy":[{"logo_path":"/buy_logo.jpg","provider_id":4,"provider_name":"Buy Provider 2","display_priority":4}],"rent":null,"flatrate":[{"logo_path":"/flatrate_logo.jpg","provider_id":5,"provider_name":"Flatrate Provider 2","display_priority":5}]}}},"credits":{"cast":[{"adult":false,"gender":1,"id":101,"known_for_department":"Acting","name":"Actor 1","original_name":"Actor Original 1","popularity":70.5,"profile_path":"/actor1.jpg","cast_id":1,"character":"Character 1","credit_id":"credit1","order":1},{"adult":true,"gender":2,"id":102,"known_for_department":"Acting","name":"Actor 2","original_name":"Actor Original 2","popularity":80.5,"profile_path":"/actor2.jpg","cast_id":2,"character":"Character 2","credit_id":"credit2","order":2}],"crew":[{"adult":false,"gender":1,"id":201,"known_for_department":"Directing","name":"Director 1","original_name":"Director Original 1","popularity":60.5,"profile_path":"/director1.jpg","credit_id":"credit3","department":"Directing","job":"Director"},{"adult":true,"gender":2,"id":202,"known_for_department":"Writing","name":"Writer 1","original_name":"Writer Original 1","popularity":50.5,"profile_path":"/writer1.jpg","credit_id":"credit4","department":"Writing","job":"Writer"}]}}"""

    private val movieData = MovieData(
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
        ),
        releaseDates = ReleaseDates(
            results = listOf(
                ReleaseDate(
                    iso31661 = "US",
                    releaseDates = listOf(
                        ReleaseDateItem(certification = "PG-13", descriptors = listOf("3D"), iso6391 = "en", note = "Note 1", releaseDate = "2023-01-01", type = 1),
                        ReleaseDateItem(certification = "R", descriptors = null, iso6391 = "en", note = "Note 2", releaseDate = "2023-01-02", type = 1)
                    )
                ),
                ReleaseDate(
                    iso31661 = "CA",
                    releaseDates = listOf(
                        ReleaseDateItem(certification = "PG", descriptors = listOf("IMAX"), iso6391 = "en", note = "Note 3", releaseDate = "2023-01-03", type = 1)
                    )
                )
            )
        ),
        watchProviders = WatchProviders(
            results = mapOf(
                "CA" to WatchProviderResult(
                    link = "http://watch.ca",
                    buy = listOf(Service(logoPath = "/buy_logo.jpg", providerId = 1, providerName = "Buy Provider 1", displayPriority = 1)),
                    rent = listOf(Service(logoPath = "/rent_logo.jpg", providerId = 2, providerName = "Rent Provider 1", displayPriority = 2)),
                    flatrate = listOf(Service(logoPath = "/flatrate_logo.jpg", providerId = 3, providerName = "Flatrate Provider 1", displayPriority = 3))
                ),
                "US" to WatchProviderResult(
                    link = "http://watch.us",
                    buy = listOf(Service(logoPath = "/buy_logo.jpg", providerId = 4, providerName = "Buy Provider 2", displayPriority = 4)),
                    rent = null,
                    flatrate = listOf(Service(logoPath = "/flatrate_logo.jpg", providerId = 5, providerName = "Flatrate Provider 2", displayPriority = 5))
                )
            )
        ),
        credits = Credits(
            cast = listOf(
                Cast(adult = false, gender = 1, id = 101, knownForDepartment = "Acting", name = "Actor 1", originalName = "Actor Original 1", popularity = 70.5, profilePath = "/actor1.jpg", castId = 1, character = "Character 1", creditId = "credit1", order = 1),
                Cast(adult = true, gender = 2, id = 102, knownForDepartment = "Acting", name = "Actor 2", originalName = "Actor Original 2", popularity = 80.5, profilePath = "/actor2.jpg", castId = 2, character = "Character 2", creditId = "credit2", order = 2)
            ),
            crew = listOf(
                Crew(adult = false, gender = 1, id = 201, knownForDepartment = "Directing", name = "Director 1", originalName = "Director Original 1", popularity = 60.5, profilePath = "/director1.jpg", creditId = "credit3", department = "Directing", job = "Director"),
                Crew(adult = true, gender = 2, id = 202, knownForDepartment = "Writing", name = "Writer 1", originalName = "Writer Original 1", popularity = 50.5, profilePath = "/writer1.jpg", creditId = "credit4", department = "Writing", job = "Writer")
            )
        )
    )

    @Test
    fun testSerializationSuccess() {
        val jsonString = Json.encodeToString(movieData)

        Assert.assertEquals(testJson, jsonString)
    }

    @Test
    fun testDeserializationSuccess() {
        val testData = Json.decodeFromString<MovieData>(testJson)

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
        Assert.assertEquals(movieData.releaseDates, testData.releaseDates)
        Assert.assertEquals(movieData.watchProviders, testData.watchProviders)
        Assert.assertEquals(movieData.credits, testData.credits)
    }
}
