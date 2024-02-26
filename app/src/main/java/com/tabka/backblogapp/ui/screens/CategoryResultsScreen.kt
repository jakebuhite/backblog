package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tabka.backblogapp.ui.viewmodels.LogViewModel

@Composable
fun CategoryResultsScreen(navController: NavHostController, logViewModel: LogViewModel, genreId: String?, genreName: String?) {
    val hasBackButton = true
    val isMovieDetails = false
    val pageTitle = "Results for $genreName"
    val isLogMenu = false
    val logId = null

    LaunchedEffect(Unit) {
        searchResultsViewModel.getMovieResultsByGenre(genreId ?: "28")
    }

    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) {
        MovieResults(navController, logViewModel, isLogMenu, logId)
    }
}

@Composable
fun MovieResults(navController: NavHostController, logViewModel: LogViewModel, isLogMenu: Boolean, logId: String?) {
    val movieResults = searchResultsViewModel.movieResults.collectAsState().value
    val halfSheets = searchResultsViewModel.halfSheet.collectAsState().value

    if (movieResults?.isNotEmpty() == true) {
        Box(modifier = Modifier.height(if (isLogMenu) 700.dp else 600.dp)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
                    .testTag("MOVIE_RESULTS_LIST")
            ) {
                items(movieResults.size) { index ->
                    val movie = movieResults[index]
                    val halfSheet = halfSheets[movie.id.toString()] ?: ""
                    MovieResult(navController, movie, halfSheet, logViewModel, isLogMenu, logId)
                }
            }
        }
    } else {
        NoResults()
    }
}