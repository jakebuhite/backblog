package com.tabka.backblogapp.ui.screens

/*import com.tabka.backblogapp.ui.bottomnav.logViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel*/
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.tmdb.MovieSearchResult
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.ui.viewmodels.SearchResultsViewModel

private val TAG = "SearchResultsScreen"
val searchResultsViewModel: SearchResultsViewModel = SearchResultsViewModel()


@Composable
fun SearchResultsScreen(navController: NavHostController, logViewModel: LogViewModel) {
    val hasBackButton = true
    val isMovieDetails = false
    val pageTitle = "Results"

    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) {
        SearchBar(navController, logViewModel)
        //DisplayMovieResults
    }
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(navController: NavHostController, logViewModel: LogViewModel) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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
                        if (text.isNotBlank()) {
                            Log.d(TAG, text)
                            searchResultsViewModel.getMovieResults(text)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.freeFocus()
                            keyboardController?.hide()
                        }
                    ),
                    placeholder = { Text("Search for a movie", modifier = Modifier.testTag("SEARCH_BAR_LABEL")) },
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
                        .testTag("SEARCH_BAR_INPUT")
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
    val movieResults = searchResultsViewModel.movieResults.collectAsState().value
    if (movieResults.first?.isNotEmpty() == true) {
        Box(modifier = Modifier.height(500.dp)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
                    .testTag("MOVIE_RESULTS_LIST")
            ) {
                items(movieResults.first?.size ?: 0) { index ->
                    val movie = movieResults.first?.get(index)
                    if (movie != null) {
                        val halfSheet = movieResults.second.getOrNull(index) ?: ""
                        MovieResult(navController, movie, halfSheet, logViewModel)
                    }
                }
            }
        }
    } else if (text.isNotEmpty()){
        NoResults()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieResult(navController: NavHostController, movie: MovieSearchResult, halfSheet: String, logViewModel: LogViewModel) {
    //val logViewModel: LogViewModel = backStackEntry.logViewModel(navController)
    val allLogs by logViewModel.allLogs.collectAsState()

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

                val imageBaseURL = if (halfSheet != "") {
                    "https://image.tmdb.org/t/p/w500/${halfSheet}"
                } else {
                    "https://image.tmdb.org/t/p/w500/${movie.backdropPath}"
                }

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
            .clickable { navController.navigate("search_movie_details_${movie.id}") }
            .testTag("MOVIE_RESULT"),
            verticalArrangement = Arrangement.Center){
            Text("${movie.originalTitle}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
        }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }

        // Add Button
        Column(modifier = Modifier
            .weight(1F)
            .height(70.dp)
            .clickable { isSheetOpen = true }
            .testTag("ADD_MOVIE_TO_LOG_BUTTON"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add Icon",
                modifier = Modifier.size(25.dp)
            )
        }


        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {isSheetOpen = false },
                containerColor = colorResource(id = R.color.bottomnav),
                modifier = Modifier.fillMaxSize().testTag("ADD_MOVIE_TO_LOG_POPUP")
            ){

                //val allLogs = logViewModel.allLogs.collectAsState().value
                if (allLogs != null) {
                    val checkedStates = remember { mutableStateListOf<Boolean>().apply { addAll(List(allLogs!!.size) { false }) } }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp)) {
                        Text("Add to Log", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Box(modifier = Modifier.height(150.dp)) {
                        LazyColumn(
                            modifier = Modifier.padding(start = 20.dp)
                        ) {
                            items(allLogs!!.size) { index ->
                                val log = allLogs!![index]
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(3F)) {
                                        Text(
                                            log.name!!,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1F), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Checkbox(
                                            checked = checkedStates[index],
                                            onCheckedChange = { isChecked ->
                                                checkedStates[index] = isChecked
                                            },
                                            modifier = Modifier.testTag("LOG_CHECKBOX")
                                        )
                                    }
                                }
                            }
                        }
                    }
                    /*Button(
                        onClick = {
                            // Use the checkedStates list to find out which checkboxes are checked
                            val checkedItems = allLogs!!.indices.filter { checkedStates[it] }
                            Log.d(TAG, "Checked Items: $checkedItems")
                            for (checkedItem in checkedItems) {
                                val log = allLogs!![checkedItem]

                                logViewModel.addMovieToLog(log.logId, movie.id.toString())
                                logViewModel.loadLogs()
                                /*Log.d(TAG, allLogs)*/
                            }
                        },
                        modifier = Modifier.testTag("ADD_TO_LOG_BUTTON")
                    ) {
                        Text(
                            "Add to Log",
                            color = Color.White
                        )
                    }*/

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Divider(thickness = 1.dp, color = Color(0xFF303437))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Add Button
                        androidx.compose.material3.Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(horizontal = 24.dp)
                                .testTag("ADD_TO_LOG_BUTTON"),
                            onClick = {
                                // Use the checkedStates list to find out which checkboxes are checked
                                val checkedItems = allLogs!!.indices.filter { checkedStates[it] }
                                Log.d(TAG, "Checked Items: $checkedItems")
                                for (checkedItem in checkedItems) {
                                    val log = allLogs!![checkedItem]

                                    logViewModel.addMovieToLog(log.logId, movie.id.toString())
                                    /*Log.d(TAG, allLogs)*/
                                }
                                isSheetOpen = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.sky_blue),
                                disabledContainerColor = Color.LightGray
                            )
                        ) {
                            Text(
                                "Add",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Cancel Button
                        androidx.compose.material3.Button(
                            onClick = {
                                isSheetOpen = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(horizontal = 24.dp)
                                .background(color = Color.Transparent)
                                .border(1.dp, Color(0xFF9F9F9F), shape = RoundedCornerShape(30.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                        ) {
                            androidx.compose.material3.Text(
                                "Cancel",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                }
            }
        }
    }
}

@Composable
fun NoResults() {
    Row(modifier = Modifier
        .fillMaxSize()
        .padding(top = 100.dp)
        .testTag("NO_RESULTS_ROW"),
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
