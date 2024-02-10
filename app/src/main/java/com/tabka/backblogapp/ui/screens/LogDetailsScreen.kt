package com.tabka.backblogapp.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel
import com.tabka.backblogapp.util.getAvatarResourceId

private val TAG = "LogDetailsScreen"


@Composable
fun LogDetailsScreen(
    navController: NavHostController,
    logId: String?,
    friendsViewModel: FriendsViewModel,
    logDetailsViewModel: LogDetailsViewModel = viewModel()
) {
    val hasBackButton = true

    // Movies
    val movieState = logDetailsViewModel.movies.observeAsState()
    val movies = movieState.value ?: emptyList()

    // Watched Movies
    val watchedMovieState = logDetailsViewModel.watchedMovies.observeAsState()
    val watchedMovies = watchedMovieState.value ?: emptyList()

    // Owner
    val ownerState = logDetailsViewModel.owner.observeAsState()
    val owner = ownerState.value ?: UserData()

    // Collaborators
    val collaboratorsState = logDetailsViewModel.collaboratorsList.observeAsState()
    val collaborators = collaboratorsState.value ?: emptyList()

    // Log
    val logState = logDetailsViewModel.logData.observeAsState()
    val log = logState.value
    val pageTitle = log?.name ?: ""

    // Get data
    LaunchedEffect(key1 = logId) {
        logDetailsViewModel.getLogData(logId!!)
        Log.d(TAG, "Launching the effect with logId: $logId")
    }

    BaseScreen(navController, hasBackButton, pageTitle) {
        DetailBar(movies.size, owner, collaborators)
        Spacer(modifier = Modifier.height(20.dp))
        LogButtons(pageTitle)
        Spacer(modifier = Modifier.height(20.dp))
        LogList(navController, movies, watchedMovies)
    }
}

@Composable
fun DetailBar(movieCount: Int, owner: UserData, collaborators: List<UserData>){
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {

        // Creator Picture
        Column(modifier = Modifier.padding(end = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = getAvatarResourceId(owner.avatarPreset ?: 1).second),
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
                    .testTag("CREATOR_PICTURE"),
            )
        }

        // Collaborator Pictures
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LazyRow {
                val itemsToShow = if (collaborators.size > 4) 4 else collaborators.size
                items(count = itemsToShow) { index ->
                    val collaborator = collaborators[index]
                    Image(
                        painter = painterResource(id = getAvatarResourceId(collaborator.avatarPreset ?: 1).second),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .testTag("COLLABS_PICTURE"), // Unique tag for each image
                    )
                }
                if (collaborators.size > 4) {
                    item {
                        Button(onClick = { /* TODO: Implement your click action here */ }) {
                            Text("+${collaborators.size - 4} more")
                        }
                    }
                }
            }
        }

        // Number of Movies
        Column(modifier = Modifier.padding(start = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$movieCount Movies", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LogButtons(logName: String) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sheetContent by remember { mutableStateOf<@Composable ColumnScope.() -> Unit>({}) }
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var logs by remember { mutableStateOf(
        listOf(
            "Aquaman and the Lost Kingdom",
            "NOPE",
            "The Batman",
            "Get Out",
            "Interstellar",
            "Joker",
            "The Creator",
            "Spider-Man"
        )
    )}

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
                        .clickable(onClick = {
                            sheetContent = { CollaboratorsSheetContent(logName) }
                            isSheetOpen = true
                        })
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
                        .clickable(onClick = {
                            sheetContent = { EditSheetContent(logName) }
                            isSheetOpen = true
                        })
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
                        .clickable(onClick = { })
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
                        .clickable(onClick = {
                            sheetContent = { AddMovieMenu() }
                            isSheetOpen = true
                        })
                )
            }
        }
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            content = sheetContent,
            tonalElevation = 10.dp,
            onDismissRequest = { isSheetOpen = false },
            containerColor = colorResource(id = R.color.bottomnav),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LogList(navController: NavHostController, movies: List<MovieData>, watchedMovies: List<MovieData>) {
    Log.d(TAG, "Movies: $movies")

    if (movies.isNotEmpty()) {
        // Height of image and padding times number of movies
        val moviesHeight: Dp = (80 * movies.size).dp

        LazyColumn(userScrollEnabled = false, modifier = Modifier.height(moviesHeight)) {
            items(movies.size) { index ->
                val movie = movies[index]
                MovieEntry(movie)
            }
        }
    }

    if (watchedMovies.isNotEmpty()) {
        Spacer(modifier = Modifier.height(50.dp))
        // Watched Movie Section
        val watchedMoviesHeight: Dp = (80 * watchedMovies.size).dp

        RequestHeader(title = "Watched Movies")
        LazyColumn(userScrollEnabled = false, modifier = Modifier.height(watchedMoviesHeight)) {
            items(watchedMovies.size) { index ->
                val movie = watchedMovies[index]
                MovieEntry(movie)
            }
        }
    }
    Log.d(TAG, "Watched List: $watchedMovies")
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


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CollaboratorsSheetContent(logName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Text(
            logName,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Spacer(modifier = Modifier.height(20.dp))

    // Collaborators Heading
    Row(modifier = Modifier.padding(start = 14.dp)) {
        androidx.compose.material3.Text(
            "Collaborators",
            style = MaterialTheme.typography.headlineMedium
        )
    }

    Spacer(modifier = Modifier.height(15.dp))

    val userList = listOf(
        "Nick Abegg",
        "Josh Altmeyer",
        "Christian Totaro",
        "Jake Buhite"
    )

    LazyRow(modifier = Modifier.padding(start = 24.dp)) {
        items(userList) { index ->
            Column() {
                Image(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp),
                    colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Add Collaborators Heading
    Row(modifier = Modifier.padding(start = 14.dp)) {
        androidx.compose.material3.Text(
            "Add Collaborators",
            style = MaterialTheme.typography.headlineMedium
        )
    }

    Spacer(modifier = Modifier.height(15.dp))

/*    Box(modifier = Modifier) {
        LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
            items(userList) { displayName ->
                NewLogCollaborator(displayName)
            }
        }
    }*/

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom) {

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
            // Save Button
            Button(
                onClick = {
                    /*if (!logName.isNullOrEmpty()) {
                    onCreateClick(logName)
                }*/
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.sky_blue),
                    disabledContainerColor = colorResource(id = R.color.sky_blue)
                ),
            ) {
                androidx.compose.material3.Text(
                    "Save",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Cancel Button
            Button(
                onClick = {
                    /*if (!logName.isNullOrEmpty()) {
                    onCreateClick(logName)
                }*/
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
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun EditSheetContent(logName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Text(
            logName,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Row(
        modifier = Modifier.padding(horizontal = 50.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Log Name
        /*TextField(
            value = logName,
            *//*onValueChange = { logName = it },*//*
            label = { androidx.compose.material3.Text(logName) },
            singleLine = true,
           *//* keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }*//*
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF373737),
                focusedLabelColor = Color(0xFF979C9E),
                unfocusedLabelColor = Color(0xFF979C9E),
                unfocusedBorderColor = Color(0xFF373737),
                backgroundColor = Color(0xFF373737)
            ),
        )*/
    }

    Spacer(modifier = Modifier.height(20.dp))

    Spacer(modifier = Modifier.height(20.dp))

    val userList = listOf(
        "Aquaman and the Lost Kingdom",
        "NOPE",
        "The Batman",
        "Get Out",
        "Interstellar",
        "Joker",
        "The Creator",
        "Spider-Man"
    )

    Box(modifier = Modifier.height(450.dp)) {
        LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
            items(userList) { movieName ->
                EditLogEntry(movieName)
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Divider(thickness = 1.dp, color = Color(0xFF303437))
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Save Button
            Button(
                onClick = {
                    /*if (!logName.isNullOrEmpty()) {
                    onCreateClick(logName)
                }*/
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.sky_blue),
                    disabledContainerColor = colorResource(id = R.color.sky_blue)
                ),
            ) {
                androidx.compose.material3.Text(
                    "Save",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Delete Button
            Button(
                onClick = {
                    /*if (!logName.isNullOrEmpty()) {
                    onCreateClick(logName)
                }*/
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(horizontal = 24.dp)
                    .background(color = Color.Transparent)
                    .border(1.dp, Color(0xFFDC3545), shape = RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
            ) {
                androidx.compose.material3.Text(
                    "Delete",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC3545)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Cancel Button
            Button(
                onClick = {
                    /*if (!logName.isNullOrEmpty()) {
                    onCreateClick(logName)
                }*/
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
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
        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Composable
fun EditLogEntry(movieName: String) {
    Row(
        modifier = Modifier.padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Remove Icon
        Column(modifier = Modifier.weight(1F)) {
            Image(
                painter = painterResource(id = R.drawable.remove),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(35.dp),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
            )
        }

        // Movie Name
        Column(
            modifier = Modifier
                .weight(3F)
                .height(60.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(movieName, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        }

        // Drag Icon
        Column(
            modifier = Modifier
                .weight(1F)
                .width(40.dp)
                .height(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = Icons.Default.DragHandle,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(35.dp),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
            )
        }
    }
}

@Composable
fun AddMovieMenu() {
    Text("Add Movie Menu")
    /*SearchBar(navController, backStackEntry)*/
}