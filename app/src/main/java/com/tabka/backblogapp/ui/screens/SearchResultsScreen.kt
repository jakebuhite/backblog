package com.tabka.backblogapp.ui.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.tmdb.MovieSearchResult
import com.tabka.backblogapp.ui.shared.AddToLogMenu
import com.tabka.backblogapp.ui.shared.CircleCheckbox
import com.tabka.backblogapp.ui.shared.NewLogMenu
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.SearchResultsViewModel
import kotlinx.coroutines.launch

private val TAG = "SearchResultsScreen"
val searchResultsViewModel: SearchResultsViewModel = SearchResultsViewModel()


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchResultsScreen(navController: NavHostController, logViewModel: LogViewModel, friendsViewModel: FriendsViewModel, movieDetailsViewModel: MovieDetailsViewModel) {
    val hasBackButton = true
    val isMovieDetails = false
    val pageTitle = "Results"
    val isLogMenu = false
    val logId = null

    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) {
        SearchBar(navController, logViewModel, isLogMenu, logId, friendsViewModel, movieDetailsViewModel)
    }
    Box(modifier = Modifier.offset(x = 16.dp, y = 20.dp)) {
        BackButton(navController = navController, visible = true)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchBar(
    navController: NavHostController,
    logViewModel: LogViewModel,
    isLogMenu: Boolean,
    logId: String?,
    friendsViewModel: FriendsViewModel,
    movieDetailsViewModel: MovieDetailsViewModel
) {
    var text by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val coroutineScope = rememberCoroutineScope()
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(5.dp)
        ) {
            Row {
                TextField(
                    value = text,
                    onValueChange = {
                        text = it

                        Log.d(TAG, "TeXT: $text")
                        // If there is something
                        if (text.isNotBlank()) {
                            Log.d(TAG, text)
                            coroutineScope.launch {
                                searchResultsViewModel.getMovieResults(text)
                            }
                        } else {
                            Log.d(TAG, "Text is blank!")
                            searchResultsViewModel.resetMovieResults()
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.freeFocus()
                            keyboardController?.hide()
                        }
                    ),
                    placeholder = {
                        Text(
                            "Search for a movie",
                            modifier = Modifier.testTag("SEARCH_BAR_LABEL")
                        )
                    },
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
    val halfSheets = searchResultsViewModel.halfSheet.collectAsState().value

    val isLoadingState = searchResultsViewModel.isLoading.observeAsState()
    val isLoading = isLoadingState.value ?: false
    Log.d(TAG, "Loading: $isLoading")

    AnimatedContent(
        targetState = isLoading,
        label = "",
        transitionSpec = {
            fadeIn(animationSpec = tween(800)) togetherWith fadeOut(
                animationSpec = tween(800)
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { targetState ->
        when (targetState) {
            true -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .offset(y = 20.dp)
                            .fillMaxWidth(.15f),
                        color = Color(0xFF3891E1)
                    )
                }
            }

            false -> {
                Log.d(TAG, "Movie results size: ${movieResults?.size}")
                if (movieResults?.isNotEmpty() == true) {
                    Row {
                        Box(modifier = Modifier.height(if (isLogMenu) 650.dp else 570.dp)) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 15.dp)
                                    .testTag("MOVIE_RESULTS_LIST")
                            ) {
                                items(movieResults.size) { index ->
                                    val movie = movieResults[index]
                                    val halfSheet = halfSheets[movie.id.toString()] ?: ""
                                    MovieResult(
                                        navController,
                                        movie,
                                        halfSheet,
                                        logViewModel,
                                        isLogMenu,
                                        logId,
                                        friendsViewModel,
                                        movieDetailsViewModel
                                    )
                                }
                            }
                        }
                    }
                } else if (text.isNotEmpty() && !isLoading) {
                    NoResults()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieResult(
    navController: NavHostController,
    movie: MovieSearchResult,
    halfSheet: String,
    logViewModel: LogViewModel,
    isLogMenu: Boolean,
    logId: String?,
    friendsViewModel: FriendsViewModel,
    movieDetailsViewModel: MovieDetailsViewModel
) {
    //val logViewModel: LogViewModel = backStackEntry.logViewModel(navController)
    val allLogs by logViewModel.allLogs.collectAsState()

    Row(
        modifier = Modifier.padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        // Movie Image
        Column(
            modifier = Modifier
                .weight(2F)
                .fillMaxHeight()
                .clickable { navController.navigate("search_movie_details_${movie.id}_${0}") },
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {

                val imageBaseURL = if (halfSheet != "") {
                    "https://image.tmdb.org/t/p/w500/${halfSheet}"
                } else {
                    "https://image.tmdb.org/t/p/w500/${movie.backdropPath}"
                }

                Image(
                    painter = rememberAsyncImagePainter(
                        imageBaseURL,
                        error = painterResource(R.drawable.nophoto1)
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Movie Title
        Column(modifier = Modifier
            .weight(3F)
            .fillMaxHeight()
            .height(70.dp)
            .padding(start = 8.dp)
            .clickable { navController.navigate("search_movie_details_${movie.id}_${0}") }
            .testTag("MOVIE_RESULT"),
            verticalArrangement = Arrangement.Center) {
            Text(
                "${movie.title}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }

        var isNewLogSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        val newLogSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        val context = LocalContext.current
        var isClicked by remember { mutableStateOf(false) }
        val haptic = LocalHapticFeedback.current

        // Add Button
        Column(modifier = Modifier
            .weight(1F)
            .height(70.dp)
            .clickable {
                if (isLogMenu && !isClicked) {
                    logViewModel.addMovieToLog(logId, movie.id.toString())
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast
                        .makeText(
                            context,
                            "Movie added to log!",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                    isClicked = true
                } else if (!isClicked) {
                    isSheetOpen = true
                }
            }
            .testTag("ADD_MOVIE_TO_LOG_BUTTON"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            if (isClicked) {
                Image(
                    painter = painterResource(id = R.drawable.checkbutton2),
                    contentDescription = "Movie is added",
                    modifier = Modifier.size(25.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add Icon",
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        val movieDetail = movieDetailsViewModel.movie.collectAsState().value

        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { isSheetOpen = false },
                dragHandle = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BottomSheetDefaults.DragHandle()
                        Text("Add to Log", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(thickness = 1.dp, color = Color(0xFF303437))
                    }
                },
                containerColor = colorResource(id = R.color.bottomnav),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("ADD_MOVIE_TO_LOG_POPUP")
            ) {
                if (allLogs != null) {
                    val checkedStates =
                        remember { mutableStateListOf<Boolean>().apply { addAll(List(allLogs!!.size) { false }) } }

                    Spacer(modifier = Modifier.height(5.dp))

                    Box(modifier = Modifier.height(150.dp)) {
                        LazyColumn(
                            modifier = Modifier.padding(start = 20.dp)
                        ) {
                            items(allLogs!!.size) { index ->
                                val log = allLogs!![index]

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        checkedStates[index] = !checkedStates[index]
                                    }
                                ) {
                                    Column(modifier = Modifier.weight(3F)) {
                                        androidx.compose.material.Text(
                                            log.name!!,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1F),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
//                            Checkbox(
//                                checked = checkedStates[index],
//                                onCheckedChange = { isChecked ->
//                                    checkedStates[index] = isChecked
//                                },
//                                modifier = Modifier
//                                    .testTag("LOG_CHECKBOX")
//                            )
                                        CircleCheckbox(selected = checkedStates[index], onChecked = {
                                                isChecked -> checkedStates[index] = isChecked as Boolean
                                        })
                                    }
                                }
                            }
                        }
                    }

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
                        var isAnyChecked by remember { mutableStateOf(false) }
                        isAnyChecked = checkedStates.any { it }
                        // Add Button
                        if (isAnyChecked) {
                            androidx.compose.material3.Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .padding(horizontal = 24.dp)
                                    .testTag("ADD_TO_LOG_BUTTON"),
                                onClick = {
                                    // Use the checkedStates list to find out which checkboxes are checked
                                    if (isAnyChecked) {
                                        val checkedItems =
                                            allLogs!!.indices.filter { checkedStates[it] }
                                        //Log.d(com.tabka.backblogapp.ui.screens.TAG, "Checked Items: $checkedItems")
                                        for (checkedItem in checkedItems) {
                                            val log = allLogs!![checkedItem]

                                            logViewModel.addMovieToLog(
                                                log.logId,
                                                movie.id.toString()
                                            )
                                            /*Log.d(TAG, allLogs)*/
                                        }
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        Toast
                                            .makeText(
                                                context,
                                                "Movie added to log!",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                        isSheetOpen = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.sky_blue),
                                    disabledContainerColor = Color.LightGray
                                )
                            ) {
                                androidx.compose.material.Text(
                                    /*"Add"*/
                                    "ADD TO LOG",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            androidx.compose.material3.Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .padding(horizontal = 24.dp)
                                    .testTag("CREATE_NEW_LOG_BUTTON"),
                                onClick = {
                                    isNewLogSheetOpen = true
                                    isSheetOpen = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.sky_blue),
                                    disabledContainerColor = Color.LightGray
                                )
                            ) {
                                androidx.compose.material.Text(
                                    /*"Add"*/
                                    "CREATE NEW LOG",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
                                "CANCEL",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    //Spacer(modifier = Modifier.height(10.dp))

                }
            }
        }

        val isLoggedIn by logViewModel.isLoggedIn.collectAsState()

        if (isNewLogSheetOpen) {
            ModalBottomSheet(
                sheetState = newLogSheetState,
                onDismissRequest = { isNewLogSheetOpen = false },
                containerColor = colorResource(id = R.color.bottomnav),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("CREATE_NEW_LOG_SEARCH_POPUP")
            ) {
                NewLogMenu(friendsViewModel = friendsViewModel, logViewModel, isLoggedIn, onCreateClick = {
                    isNewLogSheetOpen = false
                    logViewModel.loadLogs()
                }, onCloseClick = {
                    isNewLogSheetOpen = false
                }, navController)
            }
        }
    }
}

@Composable
fun NoResults() {
    Row(
        modifier = Modifier
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

