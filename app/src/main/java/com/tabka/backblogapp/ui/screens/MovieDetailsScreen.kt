package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel

private val TAG = "MovieDetailsScreen"


@Composable
fun MovieDetailsScreen(navController: NavController, logId: String?) {
    val movieDetailsViewModel: MovieDetailsViewModel = viewModel()
    val movie = movieDetailsViewModel.movie.collectAsState().value

    val hasBackButton = true
    // Empty bc the BaseScreen placeholder is in the wrong spot for this screen's title
    val pageTitle = ""


    BaseScreen(navController, hasBackButton, pageTitle) {
        if (movie != null) {
            Text(movie.title!!)
            movie.releaseDate?.let { it1 -> Text(it1) }
            if ((movie.watchProviders != null) && (movie.watchProviders.results != null)) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    movie.watchProviders.results.forEach { provider ->
                        Text(
                            "${provider.key}"
                        )
                    }
                }
            }
        }
    }
}