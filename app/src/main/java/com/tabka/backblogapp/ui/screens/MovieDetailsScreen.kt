package com.tabka.backblogapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.tmdb.Genre
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.models.tmdb.Credits
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel

private val TAG = "MovieDetailsScreen"


@Composable
fun MovieDetailsScreen(navController: NavController, logId: String?) {
    val movieDetailsViewModel: MovieDetailsViewModel = viewModel()
    val movie = movieDetailsViewModel.movie.collectAsState().value

    val hasBackButton = true
    val isMovieDetails = true
    // Empty bc the BaseScreen placeholder is in the wrong spot for this screen's title
    val pageTitle = ""


    /*   BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) {
           if (movie != null) {
               Text(movie.title!!, modifier = Modifier.testTag("MOVIE_DETAILS_MOVIE"))
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
       }*/

    Foundation(navController, hasBackButton, movie)
}

@Composable
fun Foundation(navController: NavController, isBackButtonVisible: Boolean, movie: MovieData?) {
    val lightGrey = Color(0xFF37414A)
    val darkGrey = Color(0xFF191919)

    val gradientColors = listOf(lightGrey, darkGrey)

    var scrollState = rememberScrollState()

    if (movie != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Adjust the height as needed
        ) {
            /*  Image(
        painter = painterResource(id = R.drawable.tenetdefault),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillWidth
    )*/
            val imageBaseURL =
                "https://image.tmdb.org/t/p/w500/${movie.backdropPath}"
            Image(
                painter = rememberAsyncImagePainter(imageBaseURL),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        radius = (scrollState.value / 80).dp,
                    ),
                contentScale = ContentScale.FillWidth,

                )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(900.dp)
                    .offset(y = 195.dp),
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(gradientColors)),
                ) {
                    MovieInfo(movie)
                }
            }
        }
    }
    Box(modifier = Modifier.offset(x = 20.dp, y = 20.dp)) {
        backButton(navController, isBackButtonVisible)
    }
    /*backButton(navController, isBackButtonVisible)*/
}


@Composable
fun MovieInfo(movie: MovieData?) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                    modifier = Modifier.weight(3F),
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .height(195.dp)
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
                                painter = rememberAsyncImagePainter(imageBaseURL),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Info
                Column(modifier = Modifier.weight(4F)) {
                    Row() {
                        Text(movie.title!!, style = MaterialTheme.typography.headlineMedium)
                    }
                    Row() {
                        // Rating
                        Column() {
                            androidx.compose.material3.Text(
                                text = "PG-13", style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        // Release Date
                        Column(
                        ) {
                            movie.releaseDate?.let { it1 ->
                                Text(
                                    it1,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Genres
                   /* Row() {
                        movie.genres?.forEach { genre ->
                            genre.name?.let { GenreContainer(it) }
                        }
                    }*/

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyRow {
                        movie.genres?.let { genres ->
                            items(genres.size) { index ->
                                genres[index].name?.let { GenreContainer(it) }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add to Log button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Create Button
            Button(
                onClick = {
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
                    "Add to Log",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Streaming Providers
        Row(modifier = Modifier.fillMaxWidth()) {
            /*if ((movie?.watchProviders != null) && (movie.watchProviders.results != null)) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    movie.watchProviders.results.forEach { provider ->
                        Text(
                            "${provider.key}"
                        )
                    }
                }
            }*/
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Plot Summary
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                movie?.overview!!,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Directors
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Directors", style = MaterialTheme.typography.bodyMedium)
        }
        /*Text(movie?.!!, style = MaterialTheme.typography.bodyMedium)*/

        Spacer(modifier = Modifier.height(20.dp))

        // Stars
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Stars", style = MaterialTheme.typography.bodyMedium)
           /* Text()*/
            /*Text(, style = MaterialTheme.typography.bodyMedium)
            movie?.credits?.cast!!*/
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
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
    }


}