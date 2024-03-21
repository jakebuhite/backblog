//
//  LogDetailsScreen.kt
//  backblog
//
//  Created by Tom Krusinski on 2/1/24.
//
package com.tabka.backblogapp.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.Accept
import com.tabka.backblogapp.network.models.AlertDialog
import com.tabka.backblogapp.network.models.Dismiss
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MinimalMovieData
import com.tabka.backblogapp.ui.shared.RequestHeader
import com.tabka.backblogapp.ui.shared.ShowAlertDialog
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.util.getAvatarResourceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

private val TAG = "LogDetailsScreen"

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LogDetailsScreen(
    navController: NavHostController,
    logId: String?,
    friendsViewModel: FriendsViewModel,
    logViewModel: LogViewModel,
    logDetailsViewModel: LogDetailsViewModel = viewModel()
) {
    val hasBackButton = true

    // Movies
    val movieState = logDetailsViewModel.movies.observeAsState()
    val movies = movieState.value ?: emptyMap()

    Log.d(TAG, "Movies: $movies")

    // Watched Moviess
    val watchedMovieState = logDetailsViewModel.watchedMovies.observeAsState()
    val watchedMovies = watchedMovieState.value ?: emptyMap()

    // Owner
    val ownerState = logDetailsViewModel.owner.observeAsState()
    val owner = ownerState.value ?: UserData()

    // Is Current User Owner
    val isOwnerState = logDetailsViewModel.isOwner.observeAsState()
    val isOwner = isOwnerState.value ?: true

    // Is Current User Collaborator
    val isCollaboratorState = logDetailsViewModel.isCollaborator.observeAsState()
    val isCollaborator = isCollaboratorState.value ?: false

    val isLoadingState = logDetailsViewModel.isLoading.observeAsState()
    val isLoading = isLoadingState.value ?: true

    // Collaborators
    val collaboratorsState = logDetailsViewModel.collaboratorsList.observeAsState()
    val collaborators = collaboratorsState.value ?: emptyList()
    Log.d(TAG, "List of collaborators: $collaborators")

    // Log
    val logState = logDetailsViewModel.logData.observeAsState()
    val log = logState.value
    val pageTitle = log?.name ?: ""
    val isMovieDetails = false

    // Alert Dialog
    var alertDialogState by remember { mutableStateOf(AlertDialog()) }
    val setAlertDialogState = { dialog: AlertDialog ->
        alertDialogState = dialog
    }


    // Get data
    LaunchedEffect(Unit) {
        logDetailsViewModel.getLogData(logId!!)
    }


    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) {

        Spacer(modifier = Modifier.height(3.dp))
        DetailBar(movies.size, owner, collaborators)
        Spacer(modifier = Modifier.height(20.dp))

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
                                .fillMaxWidth(.15f)
                                .testTag("LOADING_PROGRESS"),
                            color = Color(0xFF3891E1)
                        )
                    }
                }

                false -> {
                    Column {
                        if (logId != null) {
                            LogButtons(
                                navController,
                                pageTitle,
                                movies,
                                isOwner,
                                isCollaborator,
                                collaborators,
                                logDetailsViewModel,
                                logViewModel,
                                friendsViewModel,
                                alertDialogState,
                                setAlertDialogState,
                                logId
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        LogList(
                            navController,
                            logId!!,
                            movies,
                            watchedMovies,
                            logDetailsViewModel,
                            logViewModel,
                            collaborators
                        )
                    }
                }
            }
        }
    }

    Log.d(TAG, "Is visible screen? ${alertDialogState.isVisible}")
    ShowAlertDialog(alertDialogState, setAlertDialogState)
    Box(modifier = Modifier.offset(x = 16.dp, y = 20.dp)) {
        BackButton(navController = navController, visible = true)
    }
}


@Composable
fun DetailBar(movieCount: Int, owner: UserData, collaborators: List<UserData>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val isOwnerVisible = owner.userId?.isNotEmpty() ?: false

        // Owner Picture with Slide Animations
        AnimatedVisibility(
            visible = isOwnerVisible,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            Column(
                modifier = Modifier.padding(end = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = getAvatarResourceId(
                            owner.avatarPreset ?: 1
                        ).second
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp)
                        .testTag("OWNER_PICTURE"),
                )
            }
        }

        // Collaborator Pictures with Conditional Visibility and Slide Animations
        val isVisible = collaborators.isNotEmpty()
        AnimatedVisibility(
            visible = isVisible,
            enter = //slideInVertically(initialOffsetY = { -it })
            slideInHorizontally(initialOffsetX = { -it })
                    + fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LazyRow {
                    val itemsToShow = if (collaborators.size > 4) 4 else collaborators.size
                    items(count = itemsToShow) { index ->
                        val collaborator = collaborators[index]
                        Image(
                            painter = painterResource(
                                id = getAvatarResourceId(collaborator.avatarPreset ?: 1).second
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .testTag("COLLAB_PICTURE_$index"),
                        )
                    }
                    if (collaborators.size > 4) {
                        item {
                            Button(
                                onClick = { /* Implement click action */ },
                                modifier = Modifier.testTag("VIEW_ALL_COLLABS_BUTTON")
                            ) {
                                Text("+${collaborators.size - 4} more")
                            }
                        }
                    }
                }
            }
        }

        // Number of Movies with Slide Animation
        /*        AnimatedVisibility(
                    visible = isOwnerVisible,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + slideInHorizontally(initialOffsetX = { -it }) +
                    fadeIn(animationSpec = tween(1000)),
                    exit = fadeOut(animationSpec = tween(
                        durationMillis = 3000,
                        //easing = LinearOutSlowInEasing
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(start = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$movieCount Movies", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }*/

        Crossfade(
            targetState = if (isOwnerVisible) movieCount else null,
            modifier = Modifier.padding(start = 8.dp),
            animationSpec = tween(durationMillis = 1000)
        ) { targetCount ->
            if (targetCount != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "$targetCount Movies",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.LightGray,
                        modifier = Modifier.testTag("MOVIE_COUNT")
                    )
                }
            }
        }
        /*        AnimatedVisibility(
                    visible = isOwnerVisible,
                    enter = slideInHorizontally(initialOffsetX = { -it }) + slideInHorizontally(initialOffsetX = { -it }) +
                            fadeIn(animationSpec = tween(1000)),
                    exit = fadeOut(animationSpec = tween(
                        durationMillis = 3000))
                ) {
                    Crossfade(targetState = movieCount, label = "") { targetCount ->
                        Column(
                            modifier = Modifier.padding(start = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "$targetCount Movies",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }*/
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LogButtons(
    navController: NavHostController,
    logName: String,
    movies: Map<String, MinimalMovieData>,
    isOwner: Boolean,
    isCollaborator: Boolean,
    collaborators: List<UserData>,
    logDetailsViewModel: LogDetailsViewModel,
    logViewModel: LogViewModel,
    friendsViewModel: FriendsViewModel,
    alertDialogState: AlertDialog,
    setAlertDialogState: (AlertDialog) -> Unit,
    logId: String
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sheetContent by remember { mutableStateOf<@Composable ColumnScope.() -> Unit>({}) }
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isAddSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    if (isCollaborator || isOwner) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(modifier = Modifier.weight(1F)) {
                // Collaborators Icon
                if (isOwner) {
                    Column(
                        modifier = Modifier
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
                                    sheetContent = {
                                        CollaboratorsSheetContent(
                                            logName,
                                            collaborators,
                                            onDismiss = { isSheetOpen = false },
                                            logDetailsViewModel,
                                            friendsViewModel
                                        )
                                    }
                                    isSheetOpen = true
                                })
                        )
                    }
                }

                // Edit Log Icon
                Column(
                    modifier = Modifier
                        .weight(3F)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit",
                        modifier = Modifier
                            .size(35.dp)
                            .testTag("EDIT_ICON")
                            .clickable(onClick = {
                                sheetContent = {
                                    EditSheetContent(
                                        navController,
                                        isSheetOpen,
                                        onDismiss = { isSheetOpen = false },
                                        alertDialogState,
                                        setAlertDialogState,
                                        isOwner,
                                        logName,
                                        movies,
                                        logDetailsViewModel,
                                        logViewModel
                                    )
                                }
                                isSheetOpen = true
                            })
                    )
                }
            }

            Row(modifier = Modifier.weight(1F)) {
                // Shuffle Icon
                Column(
                    modifier = Modifier
                        .weight(2F)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shuffle_arrow),
                        contentDescription = "Shuffle",
                        modifier = Modifier
                            .size(35.dp)
                            .fillMaxHeight()
                            .testTag("SHUFFLE_ICON")
                            .clickable {
                                setAlertDialogState(
                                    AlertDialog(
                                        isVisible = true,
                                        header = "Shuffle?",
                                        message = "Are you sure you want to shuffle the movies?",
                                        dismiss = Dismiss(text = "Cancel"),
                                        accept = Accept(
                                            text = "Shuffle",
                                            action = {
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    logDetailsViewModel.shuffleMovies()
                                                    logViewModel.loadLogs()
                                                    logViewModel.resetMovie()
                                                }
                                            }
                                        )
                                    )
                                )
                                Log.d(TAG, "Is Visible? ${alertDialogState.isVisible}")
                            }
                    )
                }

                // Add Movie Icon
                Column(
                    modifier = Modifier
                        .weight(1F)
                        .padding(start = 10.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.End
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Add Icon",
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("ADD_MOVIE_ICON")
                            .clickable(onClick = {
                                isAddSheetOpen = true
                            })
                    )
                    if (isAddSheetOpen) {
                        ModalBottomSheet(
                            sheetState = sheetState,
                            onDismissRequest = { isAddSheetOpen = false },
                            containerColor = colorResource(id = R.color.bottomnav),
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    top = 40.dp,
                                    start = 12.dp,
                                    end = 12.dp
                                )
                            ) {
                                SearchBar(navController, logViewModel, isLogMenu = true, logId)
                            }
                        }
                    }
                }
            }

            LaunchedEffect(isSheetOpen, isAddSheetOpen) {
                if (!isSheetOpen || !isAddSheetOpen) {
                    logDetailsViewModel.getLogData(logId)
                }
            }

            if (isSheetOpen) {
                ModalBottomSheet(
                    sheetState = sheetState,
                    content = sheetContent,
                    tonalElevation = 10.dp,
                    onDismissRequest = { isSheetOpen = false },
                    containerColor = colorResource(id = R.color.bottomnav),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("SHEET_CONTENT")
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogList(
    navController: NavHostController,
    logId: String,
    movies: Map<String, MinimalMovieData>,
    watchedMovies: Map<String, MinimalMovieData>,
    logDetailsViewModel: LogDetailsViewModel,
    logViewModel: LogViewModel,
    collaboratorsList: List<UserData>
) {

    val moviesList = movies.values.toList()
    val watchedMoviesList = watchedMovies.values.toList()

    // Extra height is for the "Watched Movies" header and the spacer
    val extraHeight = if (watchedMoviesList.isNotEmpty()) 100.dp else 0.dp
    val colHeight: Dp = (80 * (movies.size + watchedMovies.size)).dp + extraHeight

    LazyColumn(
        userScrollEnabled = false,
        modifier = Modifier
            .height(colHeight)
            .testTag("MOVIES_LIST")
    ) {
        Log.d(TAG, "Movies: ${movies.values}\nWatched movies: ${watchedMovies.values}")

        if (moviesList.isNotEmpty()) {
            items(moviesList, key = { it.id ?: 0 }) { movie ->

                val addToWatched = SwipeAction(
                    background = colorResource(R.color.sky_blue),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(start = 40.dp)
                        )
                    },
                    onSwipe = {
                        val movieId = movie.id.toString()

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
                        logViewModel.markMovieAsWatched(logId, movie.id.toString())
                    },
                )

                SwipeableActionsBox(
                    endActions = listOf(addToWatched),
                    modifier = Modifier
                        .animateItemPlacement(
                            animationSpec = tween(
                                durationMillis = 1000
                            )
                        )
                ) {
                    MovieEntry(navController, movie, logId, collaboratorsList)
                }
            }
        }

        if (watchedMoviesList.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(50.dp))
                RequestHeader(title = "Watched Movies")
            }

            items(watchedMoviesList, key = { it.id ?: 0 }) { watchedMovie ->

                val addToMovies = SwipeAction(
                    background = colorResource(R.color.sky_blue),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(start = 40.dp)
                        )
                    },
                    onSwipe = {
                        val watchedMovieId = watchedMovie.id.toString()

                        val removedMovieData =
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
                        }

                        Log.d(
                            TAG,
                            "Watched movies after swipe: ${logDetailsViewModel.watchedMovies.value}"
                        )
                        logViewModel.unmarkMovieAsWatched(logId, watchedMovie.id.toString())
                    },
                )

                SwipeableActionsBox(
                    endActions = listOf(addToMovies),
                    modifier = Modifier
                        .animateItemPlacement(
                            animationSpec = tween(
                                durationMillis = 1000
                            )
                        )
                ) {
                    MovieEntry(navController, watchedMovie, logId, collaboratorsList)
                }
            }
        }
    }
}

@Composable
fun MovieEntry(
    navController: NavHostController,
    movie: MinimalMovieData,
    logId: String,
    collaboratorsList: List<UserData>
) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 5.dp, bottom = 5.dp)
        .clickable { navController.navigate("home_movie_details_${movie.id}_${logId}") }
        .testTag("MOVIE_ENTRY"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {

        // Movie Image
        Column(
            modifier = Modifier
                .weight(2F)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                if(movie.image != null) {
                    val imageBaseURL = "https://image.tmdb.org/t/p/w500/${movie.image}"
                    Image(
                        painter = rememberAsyncImagePainter(imageBaseURL, error = painterResource(R.drawable.nophoto)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else {
                    Box(modifier = Modifier.border(width = 2.dp, color = Color(0xFF9F9F9F)),
                        contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.nophoto),
                            contentDescription = "no photo"
                        )
                    }
                }
            }
        }

        // Movie Title
        Column(
            modifier = Modifier
                .weight(3F)
                .fillMaxHeight()
                .height(70.dp)
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = movie.title ?: "",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Collaborator icon
        Column(
            modifier = Modifier
                .weight(1F)
                .height(70.dp)
                .width(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (collaboratorsList.isNotEmpty()) {
                Image(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
                )
            }
        }
    }
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CollaboratorsSheetContent(
    logName: String,
    collaborators: List<UserData>,
    onDismiss: () -> Unit,
    logDetailsViewModel: LogDetailsViewModel,
    friendsViewModel: FriendsViewModel
) {
    Log.d(TAG, "Collaborators at top function: $collaborators")
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            logName,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("ADD_COLLAB_LOG_NAME")
        )
    }

    Spacer(modifier = Modifier.height(40.dp))

    val userList = friendsViewModel.friendsData.collectAsState()

    // All of the collaborators
    val collaboratorsList = remember {
        mutableStateListOf<String?>().apply {
            addAll(collaborators.map { it.userId })
        }
    }

    val existingUserIds = collaborators.map { it.userId }.toSet()

    val addCollabs = collaboratorsList.filterNotNull().filter { userId ->
        userId !in existingUserIds
    }

    val removeCollabs = collaborators.filter {
        it.userId !in collaboratorsList.filterNotNull().toSet()
    }.map {
        it.userId ?: ""
    }

    Log.d(TAG, "Collabs to add: $addCollabs")
    Log.d(TAG, "Collabs to remove: $removeCollabs")

    val sortedUserList = userList.value.sortedByDescending { user ->
        collaboratorsList.contains(user.userId.toString())
    }

    // Collaborators Heading
    Row(modifier = Modifier.padding(start = 14.dp)) {
        Text(
            "Collaborators",
            style = MaterialTheme.typography.headlineMedium
        )
    }

    // Collaborators Heading
    if (collaboratorsList.isEmpty()) {
        Spacer(modifier = Modifier.height(75.dp))
    } else {
        Spacer(modifier = Modifier.height(15.dp))
        // Current collaborators sections
        LazyRow(
            modifier = Modifier
                .padding(start = 24.dp)
                .testTag("COLLABS_LIST_ADD_SHEET")
        ) {
            items(collaboratorsList.size) { index ->
                val userId = collaboratorsList[index]
                Log.d(TAG, "Current userId of collab: $userId")
                val friend = userList.value.find { it.userId == userId }

                Column() {
                    Image(
                        painter = painterResource(
                            id = getAvatarResourceId(
                                friend?.avatarPreset ?: 1
                            ).second
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Add Collaborators Heading
    Row(modifier = Modifier.padding(start = 14.dp)) {
        Text(
            "Add Collaborators",
            style = MaterialTheme.typography.headlineMedium
        )
    }

    Spacer(modifier = Modifier.height(15.dp))

    // Add collaborators section
    Box(modifier = Modifier.height(200.dp)) {
        LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
            items(sortedUserList.size) { index ->
                val friend = sortedUserList[index]
                if (collaboratorsList.contains(friend.userId)) {
                    NewLogCollaborator(friend, collaboratorsList, true)
                } else {
                    NewLogCollaborator(friend, collaboratorsList, false)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom
    ) {

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
                    CoroutineScope(Dispatchers.Main).launch {
                        logDetailsViewModel.updateLogCollaborators(
                            addCollabs,
                            removeCollabs
                        )
                        onDismiss()
                    }
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
                    "SAVE",
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
                    onDismiss()
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
                Text(
                    "CANCEL",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditSheetContent(
    navController: NavHostController,
    isSheetOpen: Boolean,
    onDismiss: () -> Unit,
    alertDialogState: AlertDialog,
    setAlertDialogState: (AlertDialog) -> Unit,
    isOwner: Boolean,
    logName: String,
    movies: Map<String, MinimalMovieData>,
    logDetailsViewModel: LogDetailsViewModel,
    logViewModel: LogViewModel
) {

    var editedLogName by remember { mutableStateOf(logName) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("EDIT_SHEET_CONTENT"),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val focusManager = LocalFocusManager.current

            TextField(
                value = editedLogName,
                onValueChange = { editedLogName = it },
                singleLine = true,
                modifier = Modifier.testTag("LOG_NAME_INPUT"),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF373737),
                    focusedLabelColor = Color(0xFF979C9E),
                    unfocusedLabelColor = Color(0xFF979C9E),
                    unfocusedBorderColor = Color(0xFF373737),
                    backgroundColor = Color(0xFF373737)
                ),
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        val editedMovies = remember { mutableStateOf(movies.values.toList()) }

        // Box holding the list of movies
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            val state = rememberReorderableLazyListState(onMove = { from, to ->
                Log.d(TAG, "Gotta move!")
                editedMovies.value = editedMovies.value.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                }
            })
            LazyColumn(
                state = state.listState,
                modifier = Modifier
                    .reorderable(state)
                    .detectReorderAfterLongPress(state)
            ) {
                items(editedMovies.value, { it.id ?: 0 }) { movie ->
                    ReorderableItem(state, key = movie.id) { isDragging ->
                        val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                        Column(
                            modifier = Modifier
                                .shadow(elevation.value)
                        ) {
                            EditLogEntry(movie) // Your custom item UI
                        }
                    }
                }
            }
        }

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            //.weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
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
                        setAlertDialogState(
                            AlertDialog(
                                isVisible = true,
                                header = "Save Log?",
                                message = "Are you sure you want to save changes to this log?",
                                dismiss = Dismiss(text = "Cancel"),
                                accept = Accept(
                                    text = "Save",
                                    action = {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            logDetailsViewModel.updateLog(
                                                editedLogName,
                                                editedMovies.value
                                            )
                                            logViewModel.loadLogs()
                                            logViewModel.resetMovie()
                                            onDismiss()
                                        }
                                    }
                                )
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(horizontal = 24.dp)
                        .testTag("EDIT_SAVE_BUTTON"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.sky_blue),
                        disabledContainerColor = Color.Gray
                    ),
                ) {
                    Text(
                        "SAVE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val context = LocalContext.current

                if (isOwner) {
                    // Delete Button
                    Button(
                        onClick = {
                            setAlertDialogState(
                                AlertDialog(
                                    isVisible = true,
                                    header = "Delete log",
                                    message = "Are you sure you want to permanently delete this log?",
                                    dismiss = Dismiss(text = "Cancel"),
                                    accept = Accept(
                                        text = "Delete",
                                        textColor = Color(0xFFDC3545),
                                        action = {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                val asyncJob =
                                                    logDetailsViewModel.deleteLog()
                                                asyncJob?.join()

                                                logViewModel.loadLogs()
                                                navController.navigate("home")
                                                Toast.makeText(
                                                    context,
                                                    "Successfully deleted $logName!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    )
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .padding(horizontal = 24.dp)
                            .background(color = Color.Transparent)
                            .border(
                                1.dp,
                                Color(0xFFDC3545),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .testTag("EDIT_DELETE_BUTTON"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                    ) {
                        Text(
                            "DELETE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC3545)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Cancel Button
                Button(
                    onClick = { onDismiss() },
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
                    Text(
                        "CANCEL",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}


@Composable
fun EditLogEntry(movie: MinimalMovieData) {
    Row(
        modifier = Modifier
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Remove Icon
        Column(
            modifier = Modifier.weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.remove),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .testTag("REMOVE_MOVIE_ICON"),
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
            Text(
                movie.title ?: "",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("EDIT_LOG_MOVIE_TITLE"),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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
                modifier = Modifier
                    .size(35.dp)
                    .testTag("DRAG_ICON"),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.white))
            )
        }
    }
}

/*
@Composable
fun AddMovieMenu(navController: NavHostController, logViewModel: LogViewModel, logId: String) {
    Column(modifier = Modifier.padding(top = 40.dp, start = 12.dp, end = 12.dp)) {
        SearchBar(navController, logViewModel, isLogMenu = true, logId)
    }
}*/
