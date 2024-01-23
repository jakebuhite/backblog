package com.tabka.backblogapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.repository.LogLocalRepository

class MovieDetailsViewModel(savedStateHandle: SavedStateHandle): ViewModel() {
    private val movieId: String = checkNotNull(savedStateHandle["movieId"])
    val movie = "TENET BABY"
    /*val movie: MovieData? = LogLocalRepository().getLogById(logId)*/
}