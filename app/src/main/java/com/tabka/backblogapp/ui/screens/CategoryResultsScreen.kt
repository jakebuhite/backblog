package com.tabka.backblogapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.tabka.backblogapp.ui.viewmodels.LogViewModel

@Composable
fun CategoryResultsScreen(navController: NavHostController, logViewModel: LogViewModel, genreId: String?, genreName: String?) {
    val hasBackButton = true
    val isMovieDetails = false
    val pageTitle = "Results - $genreName"
    val isLogMenu = false
    val logId = null

    val isLoadingState = searchResultsViewModel.isLoading.observeAsState()
    val isLoading = isLoadingState.value ?: true

    LaunchedEffect(Unit) {
        searchResultsViewModel.getMovieResultsByGenre(genreId ?: "28")
    }

    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) {
        AnimatedContent(
            targetState = isLoading,
            label = "",
            transitionSpec = {
                fadeIn(animationSpec = tween(1000, delayMillis = 1000)) togetherWith fadeOut(
                    animationSpec = tween(1000, delayMillis = 1000)
                )
            },
            modifier = Modifier
                .fillMaxSize()
        ) { targetState ->
            when (targetState) {
                true -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .zIndex(10f)
                                .offset(y = 250.dp)
                                .fillMaxWidth(.15f),
                            color = Color(0xFF3891E1)
                        )
                    }
                }

                false -> {
                    MovieResults(navController, logViewModel, isLogMenu, logId)
                }
            }
            }
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