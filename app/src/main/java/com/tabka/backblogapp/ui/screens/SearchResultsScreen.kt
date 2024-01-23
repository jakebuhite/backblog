package com.tabka.backblogapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.SearchResultsViewModel

private val TAG = "SearchResultsScreen"

@Composable
fun SearchResultsScreen(navController: NavController) {
    val hasBackButton = true
    val pageTitle = "Results"

    BaseScreen(navController, hasBackButton, pageTitle) {
        SearchBar()
/*        Text("Click here to go to movie details page",
            modifier = Modifier.clickable { navController.navigate("search_movie_details_128") }
        )*/
    }
}

@Composable
fun SearchBar() {

    val searchResultsViewModel: SearchResultsViewModel = viewModel()
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = {
            text = it

            // If there is something
            if (!text.isNullOrBlank()) {
                Log.d(TAG, "$text")
                searchResultsViewModel.getMovieResults(text)
            }
        },
        placeholder = { Text("Search for a movie") },
        maxLines = 1
    )

    val movieResults =  searchResultsViewModel.movieResults.collectAsState().value
    if (!movieResults.isNullOrEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            movieResults.forEach { movie ->
                Text("${movie.originalTitle}")
            }
        }
    } else if (text.isNotEmpty()){
        Text("No results")
    }
}