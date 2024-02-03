package com.tabka.backblogapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel

private val TAG = "LogDetailsScreen"


@Composable
fun LogDetailsScreen(
    navController: NavHostController,
    logDetailsViewModel: LogDetailsViewModel,
    logId: String?
) {
    val hasBackButton = true

    // Movies
    val movieState = logDetailsViewModel.movies.observeAsState()
    val movies = movieState.value ?: emptyList()

    // Logs
    val logState = logDetailsViewModel.logData.observeAsState()
    val log = logState.value
    val pageTitle = log?.name ?: ""

    // Get data
    LaunchedEffect(key1 = logId) {
        logDetailsViewModel.getLogData(logId!!)
        logDetailsViewModel.getMovies()
    }

    BaseScreen(navController, hasBackButton, pageTitle) {
        DetailBar(movies.size)
        Spacer(modifier = Modifier.height(20.dp))
        LogButtons()
        Spacer(modifier = Modifier.height(20.dp))
        LogList(navController, movies)
    }
}

@Composable
fun DetailBar(movieCount: Int) {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        // Creator Picture
        Column(modifier = Modifier.padding(end = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .testTag("CREATOR_PICTURE"),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
            )
        }

        // Collaborator Pictures
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .testTag("COLLABS_PICTURE"),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
            )
        }

        // Number of Movies
        Column(modifier = Modifier.padding(start = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$movieCount Movies", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun LogButtons() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(horizontal = 7.dp),
        verticalAlignment = Alignment.CenterVertically) {

        Row(modifier = Modifier.weight(1F)) {
            // Collaborators Icon
            Column(modifier = Modifier
                .weight(1F)
                /*.width(60.dp)*/
                .fillMaxHeight()
                .padding(end = 10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user_add),
                    contentDescription = "Add Icon",
                    modifier = Modifier
                        .size(35.dp)
                        .testTag("ADD_ICON")
                )
            }

            // Edit Log Icon
            Column(modifier = Modifier
                .weight(3F)
                .fillMaxHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(35.dp)
                        .testTag("EDIT_ICON")
                )
            }
        }

        Row(modifier = Modifier.weight(1F)) {
            // Shuffle Icon
            Column(modifier = Modifier
                .weight(2F)
                .fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = R.drawable.shuffle_arrow),
                    contentDescription = "Shuffle",
                    modifier = Modifier
                        .size(35.dp)
                        .fillMaxHeight()
                        .testTag("SHUFFLE_ICON")
                )
            }

            // Add Movie Icon
            Column(modifier = Modifier
                .weight(1F)
                .padding(start = 10.dp)
                .fillMaxHeight(),
                horizontalAlignment = Alignment.End) {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("ADD_MOVIE_ICON")
                )
            }
        }
    }
}

@Composable
fun LogList(navController: NavHostController, movies: List<MovieData>) {
    Log.d(TAG, "Movies: $movies")

    if (movies.isNotEmpty()) {
        // Height of image and padding times number of movies
        val height: Dp = (80 * movies.size).dp

        LazyColumn(userScrollEnabled = false, modifier = Modifier.height(height)) {
            items(movies.size) { index ->
                val movie = movies[index]
                MovieEntry(movie)
            }
        }
    }
}

@Composable
fun MovieEntry(movie: MovieData) {
    /*Row(modifier = swipeableModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

    }*/
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {

        // Movie Image
        Column(modifier = Modifier
            .weight(2F)
            .fillMaxHeight()) {
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(5.dp))
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
            Text(text = movie.title!!, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
        }

        // Add Button
        Column(modifier = Modifier
            .weight(1F)
            .height(70.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(35.dp),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
            )
        }
    }
}