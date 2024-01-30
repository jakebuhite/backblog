package com.tabka.backblogapp.network.models.tmdb

import kotlinx.serialization.Serializable

@Serializable
data class MovieImageData (
    val backdrops: List<Image>?,
    val logos: List<Image>?,
    val posters: List<Image>?,
    val id: Int?
)