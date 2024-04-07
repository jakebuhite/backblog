//
//  HomeScreen.kt
//  backblog
//
//  Created by Jake Buhite on 2/10/24.
//
package com.tabka.backblogapp.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.ui.shared.NewLogMenu
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.util.getAvatarResourceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.NoDragCancelledAnimation
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable
import kotlin.math.ceil


private val TAG = "HomeScreen"


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    logViewModel: LogViewModel,
    friendsViewModel: FriendsViewModel
) {
    val allLogs by logViewModel.allLogs.collectAsState()
    val isLoggedIn by logViewModel.isLoggedIn.collectAsState()


    val hasBackButton = false
    val isMovieDetails = false
    val pageTitle = "What's Next?"


    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) { scrollState ->

        // If logs exist
        if (!allLogs.isNullOrEmpty()) {
            WatchNextCard(navController, allLogs!![0], logViewModel)
            Spacer(Modifier.height(40.dp))
            MyLogsSection(navController, allLogs, scrollState, logViewModel, friendsViewModel, isLoggedIn)
        } else {
            Spacer(modifier = Modifier.height(150.dp))
            NoLogs(friendsViewModel, logViewModel, isLoggedIn)
        }
        /*Spacer(Modifier.height(40.dp))
        MyLogsSection(navController, allLogs, scrollState, logViewModel, friendsViewModel)*/
    }
}


@Composable
fun WatchNextCard(
    navController: NavHostController,
    priorityLog: LogData,
    logViewModel: LogViewModel
) {

    //val logViewModel: LogViewModel = backStackEntry.logViewModel(navController)
    val movie = logViewModel.movie.collectAsState().value
    LaunchedEffect(priorityLog.movieIds?.firstOrNull()) {
        priorityLog.movieIds?.firstOrNull()?.let { movieId ->
            logViewModel.getMovieById(movieId)
        } ?: run {
            logViewModel.loadLogs()
            logViewModel.resetMovie()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        PriorityLogTitle(priorityLog.name ?: "")

        Spacer(modifier = Modifier.height(5.dp))

        //val movieId = priorityLog.movieIds?.keys?.firstOrNull()
        movie.first?.let {
            var image: String? = null
            if (movie.second != "") {
                image = movie.second
            } else if (it.backdropPath != null) {
                image = it.backdropPath
            }

            val usRelease = it.releaseDates?.results?.find { result -> result.iso31661 == "US" }
            val usRating = usRelease?.releaseDates?.get(0)?.certification.orEmpty()

            NextMovie(navController, image, it.id, priorityLog.logId)
            Spacer(modifier = Modifier.height(5.dp))
            NextMovieInfo(
                it.id,
                it.title,
                it.releaseDate,
                usRating,
                priorityLog.logId ?: "",
                logViewModel
            )
        } ?: run {
            NextMovie(navController, null, null, null)
            Spacer(modifier = Modifier.height(63.dp))
        }
    }
}


@Composable
fun PriorityLogTitle(logName: String) {
    Row {
        Text(
            "From $logName",
            style = MaterialTheme.typography.titleSmall,
            color = Color.LightGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("PRIORITY_LOG_TITLE")
        )
    }
}


@Composable
fun NextMovie(navController: NavController, image: String?, movieId: Int?, priorityLogId: String?) {

    val imageUrl = image?.let { "https://image.tmdb.org/t/p/w500/$it" }
    val painter = if (imageUrl != null) {
        rememberAsyncImagePainter(model = imageUrl)
    } else {
        painterResource(id = R.drawable.caughtup) // Placeholder image
    }

    var cardModifier = Modifier.fillMaxWidth()
    movieId?.let {
        cardModifier =
            cardModifier.clickable { navController.navigate("home_movie_details_${it}_${priorityLogId}_${1}") }
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = cardModifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.height(200.dp)) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("MOVIE_IMAGE")
            )
        }
    }
}

@Composable
fun NextMovieInfo(
    movieId: Int?,
    title: String?,
    releaseDate: String?,
    usRating: String?,
    logId: String,
    logViewModel: LogViewModel,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Movie information
        Column(
            modifier = Modifier
                .weight(4f)
                .fillMaxHeight()
        )
        {
            // Title
            Row {
                Text(
                    text = title ?: "", style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    modifier = Modifier
                        .basicMarquee(
                            iterations = Int.MAX_VALUE
                        )
                        .testTag("MOVIE_TITLE")
                )
            }

            Row {
                Column {
                    usRating?.ifEmpty { "Not Rated" }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(5.dp))

                // Release Date
                Column {
                    Text(
                        text = releaseDate?.substring(0, 4) ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        modifier = Modifier.testTag("MOVIE_YEAR")
                    )
                }
            }
        }

        // Complete button
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.End
        ) {
            val context = LocalContext.current

            var isClicked by remember { mutableStateOf(false) }
            val scaleFactor = if (isClicked) 1.1f else 1f

            val haptic = LocalHapticFeedback.current
            Image(
                painter = painterResource(id = R.drawable.checkbutton2),
                contentDescription = "Check icon",
                modifier = Modifier
                    .size(40.dp)
                    .scale(scaleFactor)
                    .testTag("CHECK_ICON")
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        Toast
                            .makeText(
                                context,
                                "Successfully marked movie as watched!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                        logViewModel.markMovieAsWatched(logId, movieId.toString())
                    }
            )

            /*Button(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                Toast
                .makeText(
                    context,
                    "Successfully marked movie as watched!",
                    Toast.LENGTH_SHORT
                )
                .show()
                Log.d("This is the Log ID:", logId.toString())
                logViewModel.markMovieAsWatched(logId, movieId.toString())},
                modifier = Modifier.size(40.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.checkbutton2),
                    contentDescription = "Check icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scaleFactor)
                        .testTag("CHECK_ICON")
                )
            }*/
        }
    }
}


// This function creates the "My Logs" header, as well as the button to create a new log
// This function then calls ListLogs, which will list each log the user has
@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLogsSection(
    navController: NavHostController,
    allLogs: List<LogData>?,
    scrollState: ScrollState,
    logViewModel: LogViewModel,
    friendsViewModel: FriendsViewModel,
    isLoggedIn: Boolean
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    // My logs heading
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                "My Logs",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.testTag("MY_LOGS_HEADER")
            )
        }

        // Add Log Button
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.End
        ) {
            var isClicked by remember { mutableStateOf(false) }
            val haptic = LocalHapticFeedback.current
            Image(
                imageVector = Icons.Default.LibraryAdd,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .scale(scaleX = -1f, scaleY = 1f)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isClicked = true
                        isSheetOpen = true
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(100) // Delay in milliseconds to simulate the click effect
                            isClicked = false
                        }
                    }
                    .testTag("ADD_LOG_BUTTON"),
                colorFilter = tint(color = colorResource(id = R.color.sky_blue))
            )
        }
    }

    // New Log Menu
    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false },
            containerColor = colorResource(id = R.color.bottomnav),
            modifier = Modifier
                .fillMaxSize()
                .testTag("ADD_LOG_POPUP")
        ) {
            NewLogMenu(friendsViewModel = friendsViewModel, logViewModel, isLoggedIn, onCreateClick = {
                    isSheetOpen = false
                    logViewModel.loadLogs()
            }, onCloseClick = {
                isSheetOpen = false
            })
        }
    }

    Spacer(modifier = Modifier.height(15.dp))

    if (!allLogs.isNullOrEmpty()) {
        DisplayLogsWithDrag(navController, scrollState, allLogs, logViewModel)
    }
}


@Composable
fun NewLogCollaborator(
    friend: UserData,
    collaboratorsList: SnapshotStateList<String?>,
    currentCollab: Boolean
) {
    Row(
        modifier = Modifier.padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // User Icon
        Column(modifier = Modifier.weight(1F)) {
            Image(
                painter = painterResource(
                    id = getAvatarResourceId(
                        friend.avatarPreset ?: 1
                    ).second
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .testTag("NEW_LOG_COLLABORATOR_AVATAR"),
            )
        }

        // Username
        Column(
            modifier = Modifier
                .weight(3F)
                .height(60.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                friend.username ?: "",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("NEW_LOG_COLLABORATOR_USERNAME")
            )
        }

        if (currentCollab) {
            // Minus Button
            Column(
                modifier = Modifier
                    .width(40.dp)
                    .height(60.dp)
                    .clickable {
                        if (collaboratorsList.contains(friend.userId)) {
                            collaboratorsList.remove(friend.userId)
                        }
                    }
                    .testTag("REMOVE_COLLABORATOR_BUTTON"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = Icons.Default.RemoveCircle,
                    contentDescription = "Add Icon",
                    colorFilter = tint(color = Color.Red),
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("REMOVE_COLLABORATOR_ICON")
                )
            }
        } else {
            // Add Button
            Column(
                modifier = Modifier
                    .width(40.dp)
                    .height(60.dp)
                    .clickable {
                        if (!collaboratorsList.contains(friend.userId)) {
                            collaboratorsList.add(friend.userId)
                        }

                    }
                    .testTag("ADD_COLLABORATOR_BUTTON"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add Icon",
                    modifier = Modifier
                        .size(25.dp)
                        .testTag("ADD_COLLABORATOR_ICON")
                )
            }
        }
    }
}

@Composable
fun DisplayLogsWithDrag(
    navController: NavHostController,
    scrollState: ScrollState,
    allLogs: List<LogData>?,
    logViewModel: LogViewModel
) {
    val state =
        rememberReorderableLazyGridState(dragCancelledAnimation = NoDragCancelledAnimation(),
            onMove = { from, to ->
                Log.d(TAG, "Gotta move $from to $to!")

                logViewModel.onMove(from.index, to.index)
            })

    val multiplier = ceil(allLogs!!.size / 2.0).toInt()
    val containerHeight: Dp = (190 * multiplier).dp

    Log.d(TAG, "ALL LOGS: $allLogs")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state.gridState,
        contentPadding = PaddingValues(top = 5.dp, bottom = 5.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        userScrollEnabled = false,
        modifier = Modifier
            .reorderable(state)
            .height(containerHeight)
            .detectReorderAfterLongPress(state)
    ) {
        items(allLogs, { it.logId ?: 0 }) { log ->
            ReorderableItem(state, key = log.logId) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "")
                Box(
                    modifier = Modifier
                        .shadow(elevation.value)
                        .aspectRatio(1f)
                    //.background(Color.White)
                ) {
                    var painter by remember { mutableStateOf<Painter?>(null) }
                    var movieData by remember { mutableStateOf<Pair<MovieData?, String>>(null to "") }
                    val movieId = log.movieIds?.firstOrNull()

                    LaunchedEffect(log.logId, movieId) {
                        movieId?.let {
                            logViewModel.fetchMovieDetails(it) { result ->
                                result.data?.let { data ->
                                    movieData = data
                                }
                            }
                        }
                    }

                    painter = if (movieData.first != null) {
                        rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500/${movieData.second}")
                    } else {
                        painterResource(id = R.drawable.emptylog)
                    }
                    LogEntry(
                        navController = navController,
                        log.logId ?: "",
                        log.name ?: "",
                        painter!!
                    )
                }
            }
        }
    }
}

@Composable
fun LogEntry(navController: NavHostController, logId: String, logName: String, painter: Painter) {

    Card(
        modifier = Modifier
            .size(180.dp)
            .clickable { navController.navigate("home_log_details_$logId") },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
        /*elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)*/
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Black.copy(alpha = 0.75f), // Transparent black color
                        shape = RoundedCornerShape(5.dp)
                    )
            )

            // Text overlay
            Text(
                text = logName,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoLogs(friendsViewModel: FriendsViewModel, logViewModel: LogViewModel, isLoggedIn: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1F))
        Row {
            Image(
                painter = painterResource(id = R.drawable.nologs),
                contentDescription = "No logs",
                modifier = Modifier.size(100.dp)
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Row {
            Text(
                "You have no logs",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row {
            Text(
                "Create one below to get started.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.LightGray
            )
        }
        Spacer(modifier = Modifier.height(150.dp))

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }

        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { isSheetOpen = false },
                containerColor = colorResource(id = R.color.bottomnav),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("ADD_LOG_POPUP")
            ) {
                NewLogMenu(friendsViewModel, logViewModel, isLoggedIn, onCreateClick = {
                    isSheetOpen = false
                    logViewModel.loadLogs()
                }, onCloseClick = {
                    isSheetOpen = false
                })
            }
        }

        Button(
            onClick = {
                isSheetOpen = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.sky_blue)
            )
        ) {
            Text(
                "CREATE NEW LOG",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}