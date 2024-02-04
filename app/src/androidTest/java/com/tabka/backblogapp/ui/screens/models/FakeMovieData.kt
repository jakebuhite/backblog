package com.tabka.backblogapp.ui.screens.models

import com.tabka.backblogapp.network.models.tmdb.Image
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.models.tmdb.MovieImages

fun createFakeMovieData(
    id: Int = 1, // Default values for simplicity
    title: String = "Fake Movie Title",
    releaseDate: String = "2022-01-01",
    imagePath: String = "/fakeImagePath.jpg"
): MovieData {
    val fakeImage = Image(
        aspectRatio = 1.77,
        height = 1080,
        filePath = imagePath,
        width = 1920,
        iso6391 = null,
        voteAverage = null,
        voteCount = null
    )

    val fakeImages = MovieImages(
        backdrops = listOf(fakeImage),
        posters = listOf(fakeImage),
        logos = listOf(fakeImage)
    )

    return MovieData(
        id = id,
        title = title,
        releaseDate = releaseDate,
        images = fakeImages,
        // Fill in the minimum required fields or use defaults for optional ones
        adult = null,
        backdropPath = null,
        belongsToCollection = null,
        budget = null,
        genres = null,
        homepage = null,
        imdbId = null,
        originalLanguage = null,
        originalTitle = null,
        overview = null,
        popularity = null,
        posterPath = imagePath,
        productionCompanies = null,
        productionCountries = null,
        revenue = null,
        runtime = null,
        spokenLanguages = null,
        status = null,
        tagline = null,
        video = null,
        voteAverage = null,
        voteCount = null,
        releaseDates = null,
        watchProviders = null,
        credits = null
    )
}