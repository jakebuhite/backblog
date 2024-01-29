package com.tabka.backblogapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.tmdb.MovieSearchData
import com.tabka.backblogapp.network.models.tmdb.MovieSearchResult
import com.tabka.backblogapp.network.repository.MovieRepository
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.SearchResultsViewModel

private val TAG = "SearchResultsScreen"

@Composable
fun SearchResultsScreen(navController: NavController) {
    val hasBackButton = true
    val pageTitle = "Results"

    BaseScreen(navController, hasBackButton, pageTitle) {
        SearchBar(navController)
/*        Text("Click here to go to movie details page",
            modifier = Modifier.clickable { navController.navigate("search_movie_details_128") }
        )*/
    }
}

/*
@Composable
fun SearchBar(navController: NavController) {
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
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Modify background color
            .padding(8.dp) // Add padding for better appearance
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
    )

    val movieResults =  searchResultsViewModel.movieResults.collectAsState().value
    if (!movieResults.isNullOrEmpty()) {
        ListMovieResults(navController, movieResults)
    } else if (text.isNotEmpty()){
        Text("No results")
    }
}

@Composable
fun ListMovieResults(navController: NavController, movieResults: List<MovieSearchResult>) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        movieResults.forEach { movie ->
            Text(
                "${movie.originalTitle}",
                modifier = Modifier
                    .height(100.dp)
                    .clickable { navController.navigate("search_movie_details_${movie.id}") }
            )
        }
}*/

@Composable
fun SearchBar(navController: NavController) {
    val searchResultsViewModel: SearchResultsViewModel = viewModel()
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(5.dp)
        ) {
            Row() {
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
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray, // Adjust the tint color
                            modifier = Modifier.padding(8.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White) // Modify background color
                    /*.padding(8.dp) // Add padding for better appearance
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))*/
                )
            }
        }
    }


    val movieResults = searchResultsViewModel.movieResults.collectAsState().value
    if (!movieResults.isNullOrEmpty()) {
        Box(modifier = Modifier.height(500.dp)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
            ) {
                items(movieResults) { movie ->
                    MovieResult(movie)
                }
            }
        }
    } else if (text.isNotEmpty()){
        NoResults()
    }
}

@Composable
fun MovieResult(movie: MovieSearchResult) {
    Row(modifier = Modifier.padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {

        // Movie Image
        Column(modifier = Modifier
            .weight(2F)
            .fillMaxHeight()) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(70.dp)
            ) {
                val imageBaseURL =
                    "https://image.tmdb.org/t/p/w500/${movie.backdropPath}"
                Image(
                    painter = rememberAsyncImagePainter(imageBaseURL),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Movie Title
        Column(modifier = Modifier
            .weight(3F)
            .fillMaxHeight()
            .height(70.dp)
            .padding(start = 8.dp),
            verticalArrangement = Arrangement.Center){
            Text("${movie.originalTitle}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
        }

        // Add Button
        Column(modifier = Modifier
            .weight(1F)
            .height(70.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add Icon",
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Composable
fun NoResults() {
    Row(modifier = Modifier
        .fillMaxSize()
        .padding(top = 100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            "No results",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        )
    }
}
