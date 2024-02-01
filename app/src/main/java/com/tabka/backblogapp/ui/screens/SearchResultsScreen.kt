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
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
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
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.SearchResultsViewModel
import java.util.Collections.addAll

private val TAG = "SearchResultsScreen"
private val logViewModel: LogViewModel = LogViewModel()

@Composable
fun SearchResultsScreen(navController: NavController) {
    val hasBackButton = true
    val pageTitle = "Results"

    BaseScreen(navController, hasBackButton, pageTitle) {
        SearchBar(navController)
    }
}

@Composable
fun SearchBar(navController: NavController) {
    val searchResultsViewModel: SearchResultsViewModel = viewModel()
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

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
                        .background(Color.White)
                        .focusRequester(focusRequester)
                    /*.padding(8.dp) // Add padding for better appearance
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))*/
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
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
                    MovieResult(navController, movie)
                }
            }
        }
    } else if (text.isNotEmpty()){
        NoResults()
    }
}


@Composable
fun MovieResult(navController: NavController, movie: MovieSearchResult) {
    Row(modifier = Modifier.padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {

        // Movie Image
        Column(modifier = Modifier
            .weight(2F)
            .fillMaxHeight()
            .clickable { navController.navigate("search_movie_details_${movie.id}") },
        ) {
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
            .padding(start = 8.dp)
            .clickable { navController.navigate("search_movie_details_${movie.id}") },
            verticalArrangement = Arrangement.Center){
            Text("${movie.originalTitle}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
        }

/*        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }*/

        // Add Button
        Column(modifier = Modifier
            .weight(1F)
            .height(70.dp),
            //.clickable { isSheetOpen = true },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add Icon",
                modifier = Modifier.size(25.dp)
            )
        }

        /*


        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {isSheetOpen = false },
                containerColor = colorResource(id = R.color.bottomnav),
                modifier = Modifier.fillMaxSize()
            ){

                if (allLogs != null) {
                    val checkedStates = remember { mutableStateListOf<Boolean>().apply { addAll(List(allLogs.size) { false }) } }

                    LazyColumn(
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        items(allLogs.size) { index ->
                            val log = allLogs[index]
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    log.name!!,
                                    color = Color.White
                                )
                                Checkbox(
                                    checked = checkedStates[index],
                                    onCheckedChange = { isChecked ->
                                        checkedStates[index] = isChecked
                                    },
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            // Use the checkedStates list to find out which checkboxes are checked
                            val checkedItems = allLogs.indices.filter { checkedStates[it] }
                            Log.d(TAG, "Checked Items: $checkedItems")
                        }
                    ) {
                        Text(
                            "Add to Log",
                            color = Color.White
                        )
                    }
                    *//*LazyColumn(
                        modifier = Modifier
                            .padding(start = 20.dp)
                    ) {

                        items(allLogs.size) { index ->
                            val checkedState = remember { mutableStateOf(false) }
                            val log = allLogs[index]
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    log.name!!,
                                    color = Color.White
                                )
                                Checkbox(
                                    checked = checkedState.value,
                                    onCheckedChange = { checkedState.value = it },
                                )
                            }
                        }
                    }
                    Button(
                        onClick = { Log.d(TAG, "Button clicked! $") }
                    ) {
                        Text(
                            "Add to Log",
                            color = Color.White
                        )
                    }*//*
                }
            }
        }*/
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
