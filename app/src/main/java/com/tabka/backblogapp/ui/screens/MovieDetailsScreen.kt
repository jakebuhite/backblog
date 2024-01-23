package com.tabka.backblogapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel

private val TAG = "MovieDetailsScreen"


@Composable
fun MovieDetailsScreen(navController: NavController, logId: String?) {
    val movieDetailsViewModel: MovieDetailsViewModel = viewModel()
    val movie = movieDetailsViewModel.movie!!

    val hasBackButton = true
    val pageTitle = movie

    BaseScreen(navController, hasBackButton, pageTitle) {
    }
}