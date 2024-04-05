package com.tabka.backblogapp.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.ui.shared.AddToLogMenu
import com.tabka.backblogapp.ui.shared.NewLogMenu
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel
import kotlinx.coroutines.launch

private val TAG = "MovieDetailsScreen"


@Composable
fun MovieDetailsScreen(navController: NavController, movieId: String?, logId: String?, logViewModel: LogViewModel, movieIsWatched: Int, movieDetailsViewModel: MovieDetailsViewModel = viewModel(), friendsViewModel: FriendsViewModel) {
    val movie = movieDetailsViewModel.movie.collectAsState().value

    Log.d(TAG, "Here")
    LaunchedEffect(Unit) {
        movieDetailsViewModel.setMovie(movieId ?: "")
    }

    val hasBackButton = true

    Foundation(navController, hasBackButton, movie, logViewModel, movieIsWatched, logId, friendsViewModel)
}

@Composable
fun Foundation(
    navController: NavController,
    isBackButtonVisible: Boolean,
    movie: MovieData?,
    logViewModel: LogViewModel,
    movieIsWatched: Int,
    logId: String?,
    friendsViewModel: FriendsViewModel
) {
    val lightGrey = Color(0xFF37414A)
    val darkGrey = Color(0xFF191919)

    /*val gradientColors = listOf(lightGrey, darkGrey)*/
    val gradientColors = listOf(lightGrey, darkGrey)

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        if (movie != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val imageBaseURL =
                    "https://image.tmdb.org/t/p/w500/${movie.backdropPath}"
                Image(
                    painter = rememberAsyncImagePainter(imageBaseURL, error = painterResource(id = R.drawable.backplaceholder)),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(
                            radius = (scrollState.value / 80).dp,
                        )
                        .testTag("IMAGE_BACKGROUND"),
                    contentScale = ContentScale.FillWidth,
                    )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .offset(y = 195.dp),
                    shape = RoundedCornerShape(5.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(Brush.verticalGradient(gradientColors)),
                    ) {
                        MovieInfo(movie, logViewModel, movieIsWatched, logId, friendsViewModel)
                    }
                }
            }
        }
        Box(modifier = Modifier.offset(x = 16.dp, y = 20.dp)) {
            BackButton(navController, isBackButtonVisible)
        }
        /*backButton(navController, isBackButtonVisible)*/
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MovieInfo(movie: MovieData?, logViewModel: LogViewModel, movieIsWatched: Int, logId: String?, friendsViewModel: FriendsViewModel) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        if (movie != null) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                /*verticalAlignment = Alignment.CenterVertically*/
            ) {
                // Poster
                Column(
                    modifier = Modifier.weight(2F),
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .height(190.dp)
                            .width(130.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                    )
                    {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            /*       Image(
                                       painter = painterResource(id = R.drawable.creator),
                                       contentDescription = null,
                                       contentScale = ContentScale.Crop,
                                   )*/
                            val imageBaseURL =
                                "https://image.tmdb.org/t/p/w500/${movie.posterPath}"
                            Image(
                                painter = rememberAsyncImagePainter(imageBaseURL, error = painterResource(R.drawable.noposter1)),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                // Info
                Column(
                    modifier = Modifier
                        .weight(4F)
                        .padding(start = 15.dp)
                ) {
                    Row {
                        Text(movie.title!!, style = MaterialTheme.typography.headlineMedium)
                    }
                    Row {
                        // Rating
                        Column {
                            /* androidx.compose.material3.Text(
                                }, style = MaterialTheme.typography.bodySmall,
                            )*/
                            val usRelease =
                                movie.releaseDates?.results?.find { it.iso31661 == "US" }
                            /*val usRating =
                                usRelease?.releaseDates?.find { it.iso6391 == "US" }?.certification.orEmpty()*/
                            val usRating =
                                usRelease?.releaseDates?.get(0)?.certification.orEmpty()

                            Text(text = usRating.ifEmpty { "Not Rated" }, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        // Release Date
                        Column {
                            movie.releaseDate?.let { releaseDate ->
                                if (releaseDate.isNotEmpty()) {
                                    val year = releaseDate.substring(0, 4)
                                    Text(
                                        year,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }

                    Row {
                        val runtime = movie.runtime

                     /*   if (runtime == 0) {
                            Text("Runtime unknown", style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray)
                        }*/
                        if (runtime != 0) {
                            Text(
                                "$runtime minutes".ifEmpty { "Unknown" },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }

                    // Genres
                    /* Row() {
                         movie.genres?.forEach { genre ->
                             genre.name?.let { GenreContainer(it) }
                         }
                     }*/

                    Spacer(modifier = Modifier.height(20.dp))

                    /*LazyRow {
                        movie.genres?.let { genres ->
                            items(genres.size) { index ->
                                genres[index].name?.let { GenreContainer(it) }
                            }
                        }
                    }*/
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow {
                movie.genres?.let { genres ->
                    items(genres.size) { index ->
                        genres[index].name?.let { GenreContainer(it) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            val allLogs by logViewModel.allLogs.collectAsState()
            val scope = rememberCoroutineScope()
            val sheetState = rememberModalBottomSheetState(/*skipPartiallyExpanded = false*/)
            var isSheetOpen by rememberSaveable {
                mutableStateOf(false)
            }

            var isNewLogSheetOpen by rememberSaveable {
                mutableStateOf(false)
            }
            val newLogSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            // Add to Log button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                var isClicked by remember { mutableStateOf(false) }
                if (movieIsWatched == 1 && !isClicked) {
                    val context = LocalContext.current
                    val haptic = LocalHapticFeedback.current
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast
                                .makeText(
                                    context,
                                    "Successfully marked movie as watched!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()

                            /*val movieId = movie.id.toString()

                            val removedMovieData =
                                logDetailsViewModel.movies.value?.let { movies ->
                                    val updatedMovies = movies.toMutableMap().apply {
                                        remove(movieId)
                                    }
                                    logDetailsViewModel.movies.value = updatedMovies
                                    movies[movieId] // Get the removed movie data to add it to movies
                                }

                            removedMovieData?.let { movieData ->
                                val currentMovies =
                                    logDetailsViewModel.watchedMovies.value ?: mapOf()
                                val updatedMovies = currentMovies.toMutableMap().apply {
                                    this[movieId] = movieData
                                }
                                logDetailsViewModel.watchedMovies.value = updatedMovies
                            }
                            if (logId != null) {
                                logViewModel.markMovieAsWatched(logId, movie.id.toString())
                            }

                            if (logId != null) {
                                Log.d("THIS IS THE LOG ID:", logId.toString())
                                logViewModel.markMovieAsWatched(logId, movie.id.toString())
                            }*/
                            isClicked = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.sky_blue),
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        androidx.compose.material3.Text(
                            "ADD TO WATCHED",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else if (movieIsWatched == 2 && !isClicked) {
                    val context = LocalContext.current
                    val haptic = LocalHapticFeedback.current
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast
                                .makeText(
                                    context,
                                    "Successfully marked movie as unwatched!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            if (logId != null) {
                                Log.d("THIS IS THE LOG ID:", logId.toString())
                                /*logViewModel.unmarkMovieAsWatched(logId, movie.id.toString())
                                val watchedMovieId = movie.id.toString()
*/
                              /*  val removedMovieData =
                                    logDetailsViewModel.watchedMovies.value?.let { watchedMovies ->
                                        val updatedWatchedMovies =
                                            watchedMovies.toMutableMap().apply {
                                                remove(watchedMovieId)
                                            }
                                        logDetailsViewModel.watchedMovies.value =
                                            updatedWatchedMovies
                                        watchedMovies[watchedMovieId] // Get the removed movie data to add it to movies
                                    }

                                removedMovieData?.let { movieData ->
                                    val currentMovies = logDetailsViewModel.movies.value ?: mapOf()
                                    val updatedMovies = currentMovies.toMutableMap().apply {
                                        this[watchedMovieId] = movieData
                                    }
                                    logDetailsViewModel.movies.value = updatedMovies
                                }*/
                            }
                            isClicked = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.sky_blue),
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        androidx.compose.material3.Text(
                            "ADD TO UNWATCHED",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else {
                    Button(
                        onClick = {
                            isSheetOpen = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.sky_blue),
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        androidx.compose.material3.Text(
                            "ADD TO LOG",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

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
                    AddToLogMenu(logViewModel = logViewModel, movie, onCreateNewLog = {
                        isNewLogSheetOpen = true
                    }, onCloseAddMenu = {
                        isSheetOpen = false
                    })
                }
            }

            val isLoggedIn by logViewModel.isLoggedIn.collectAsState()

            if (isNewLogSheetOpen) {
                ModalBottomSheet(
                    sheetState = newLogSheetState,
                    onDismissRequest = {
                        isNewLogSheetOpen = false },
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
                    })
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            LazyRow(modifier = Modifier.fillMaxWidth()) {
                val flatrateProviders = movie.watchProviders?.results?.filter { it.key == "US" }
                    ?.flatMap { it.value.flatrate ?: emptyList() }
                    ?.map { it.logoPath.orEmpty() }
                    .orEmpty()

                val uniqueLogoPaths = HashSet<String>()

                flatrateProviders.forEach { logoPath ->
                    if (logoPath !in uniqueLogoPaths) {
                        uniqueLogoPaths.add(logoPath)
                        item {
                            Card(modifier = Modifier.padding(end = 5.dp)) {
                                Box(
                                    modifier = Modifier
                                        .width(50.dp)
                                        .height(50.dp)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/original/$logoPath"),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Plot Summary
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)) {
                Text("Plot Summary", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    movie.overview!!.ifEmpty { "Unknown" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Directors
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)) {
                Text("Directors", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
            }
            Row {
                movie.credits?.crew?.let { crew ->
                    val directors = crew.filter { it.job == "Director" }
                    Text(
                        directors.joinToString { it.name.orEmpty() }.ifEmpty { "Unknown" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stars
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)) {
                Text("Stars", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
            }

            Row {
                movie.credits?.cast?.let { cast ->
                    Text(
                        cast.take(3).joinToString { it.name.orEmpty() }.ifEmpty { "Unknown" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(275.dp))
        }
    }
}


@Composable
fun GenreContainer(genre: String) {
    Card(
        modifier = Modifier
            /*.height(35.dp)
            .width(85.dp)*/
            .height(35.dp)
            .padding(end = 5.dp)
            .background(color = Color.Transparent)
            .border(2.dp, Color(0xFF9F9F9F), shape = RoundedCornerShape(30.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                genre.uppercase(),
                /*color = Color.White,*/
                color = Color.LightGray,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
    }
}