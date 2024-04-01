package com.tabka.backblogapp.mocks

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.tabka.backblogapp.network.ApiClient
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchResult
import com.tabka.backblogapp.network.repository.MovieRepository

class FakeMovieRepository(db: FirebaseFirestore = Firebase.firestore) : MovieRepository(db, movieApiService = ApiClient.movieApiService) {
    private var shouldGetMovieByIdSucceed = true
    private var shouldSearchMovieSucceed = true
    private var shouldGetMovieHalfSheetSucceed = true

    fun shouldGetMovieByIdNotSucceed() {
        shouldGetMovieByIdSucceed = false
    }

    fun shouldSearchMovieNotSucceed() {
        shouldSearchMovieSucceed = false
    }

    fun shouldGetMovieHalfSheetNotSucceed() {
        shouldGetMovieHalfSheetSucceed = false
    }

    override fun getMovieById(movieId: String, onResponse: (MovieData?) -> Unit, onFailure: (String) -> Unit) {
        if (shouldGetMovieByIdSucceed) {
            val movieData = MovieData(
                adult = false,
                belongsToCollection = null,
                budget = null,
                credits = null,
                backdropPath = "backdrop.png",
                genres = null,
                homepage = null,
                id = 11,
                images = null,
                imdbId = null,
                originalLanguage = null,
                originalTitle = null,
                overview = null,
                popularity = null,
                posterPath = "poster123",
                productionCompanies = null,
                productionCountries = null,
                releaseDate = null,
                releaseDates = null,
                revenue = null,
                runtime = null,
                spokenLanguages = null,
                status = null,
                tagline = null,
                title = null,
                video = null,
                voteAverage = 10.0,
                voteCount = 10,
                watchProviders = null
            )
            onResponse(movieData)
        } else {
            onFailure("FAILED")
        }
    }

    override fun searchMovie(query: String, page: Int, onResponse: (MovieSearchData?) -> Unit, onFailure: (String) -> Unit) {
        if (shouldSearchMovieSucceed) {
            val movieSearchData = MovieSearchData(
                page = 1,
                results = listOf(MovieSearchResult(
                    adult = false,
                    backdropPath = "backdrop.png",
                    genreIds = null,
                    id = 11,
                    originalLanguage = null,
                    originalTitle = null,
                    overview = null,
                    popularity = null,
                    posterPath = "poster123",
                    releaseDate = null,
                    title = null,
                    video = null,
                    voteAverage = 10.0,
                    voteCount = 10
                )),
                totalPages = 1,
                totalResults = 1
            )
            onResponse(movieSearchData)
        } else {
            onFailure("FAILED")
        }
    }

    override fun searchMoviesByGenre(page: Int, genreId: String, onResponse: (MovieSearchData?) -> Unit, onFailure: (String) -> Unit) {
        if (shouldSearchMovieSucceed) {
            val movieSearchData = MovieSearchData(
                page = 1,
                results = listOf(MovieSearchResult(
                    adult = false,
                    backdropPath = "backdrop.png",
                    genreIds = null,
                    id = 11,
                    originalLanguage = null,
                    originalTitle = null,
                    overview = null,
                    popularity = null,
                    posterPath = "poster123",
                    releaseDate = null,
                    title = null,
                    video = null,
                    voteAverage = 10.0,
                    voteCount = 10
                )),
                totalPages = 1,
                totalResults = 1
            )
            onResponse(movieSearchData)
        } else {
            onFailure("FAILED")
        }
    }

    override fun getMovieHalfSheet(
        movieId: String,
        onResponse: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (shouldGetMovieHalfSheetSucceed) {
            onResponse("fake/file/path.png")
        } else {
            onFailure("FAILED")
        }
    }
}