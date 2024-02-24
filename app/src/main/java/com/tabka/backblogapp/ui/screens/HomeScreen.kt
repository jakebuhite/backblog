package com.tabka.backblogapp.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.util.getAvatarResourceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.roundToInt


private val TAG = "HomeScreen"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    logViewModel: LogViewModel,
    friendsViewModel: FriendsViewModel
) {
    val allLogs by logViewModel.allLogs.collectAsState()
    Log.d(TAG, "Here are the logs: $allLogs")

    val hasBackButton = false
    val isMovieDetails = false
    val pageTitle = "What's Next?"

    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) { scrollState ->

        // If logs exist
        if (!allLogs.isNullOrEmpty()) {
            Log.d(TAG, "This is the first Log: ${allLogs!![0]}")
            WatchNextCard(navController, allLogs!![0], logViewModel)
        }
        Spacer(Modifier.height(50.dp))
        MyLogsSection(navController, allLogs, scrollState, logViewModel, friendsViewModel)
    }
}


@Composable
fun WatchNextCard(navController: NavHostController, priorityLog: LogData, logViewModel: LogViewModel) {

    Log.d(TAG, "Priority Log: $priorityLog")
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
            Log.d(TAG, "This is the movie if it exists: $it")
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
            NextMovieInfo(it.id, it.title, it.releaseDate, usRating, it.posterPath, priorityLog.logId ?: "", logViewModel)
        } ?: run {
            NextMovie(navController, null, null, null)
            Spacer(modifier = Modifier.height(63.dp))
        }
    }
}


@Composable
fun PriorityLogTitle(logName: String) {
    Row() {
        Text("From $logName", style = MaterialTheme.typography.titleSmall, color = Color.LightGray,
            modifier = Modifier.testTag("PRIORITY_LOG_TITLE"))
    }
}


@Composable
fun NextMovie(navController: NavController, image: String?, movieId: Int?, priorityLogId: String?) {

    val imageUrl = image?.let { "https://image.tmdb.org/t/p/w500/$it" }
    val painter = if (imageUrl != null) {
        rememberAsyncImagePainter(model = imageUrl)
    } else {
        painterResource(id = R.drawable.icon_empty_log) // Placeholder image
    }

    var cardModifier = Modifier.fillMaxWidth()
    movieId?.let {
        cardModifier = cardModifier.clickable { navController.navigate("home_movie_details_${it}_$priorityLogId") }
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
    /*val painter = if (image == null) {
        // Use painterResource for local drawable resources
        painterResource(id = R.drawable.icon_empty_log)
    } else {
        // Use rememberAsyncImagePainter for remote images
        val imageUrl = "https://image.tmdb.org/t/p/w500/$image"
        rememberAsyncImagePainter(model = imageUrl)
    }

    // Next movie image
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(5.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            modifier = Modifier.clickable{ navController.navigate("home_movie_details_0") },
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier.height(200.dp)
            ) {
                Image(
                    //painter = painterResource(id = image),
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("MOVIE_IMAGE")
                )
            }
        }
    }*/
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NextMovieInfo(
    movieId: Int?,
    title: String?,
    releaseDate: String?,
    usRating: String?,
    image: String?,
    logId: String,
    logViewModel: LogViewModel
) {

    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        // Movie information
        Column(modifier = Modifier
            .weight(4f)
            .fillMaxHeight()
        )
        {
            // Title
            Row() {
                Text(text = title ?: "", style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    modifier = Modifier
                        .basicMarquee(
                            iterations = Int.MAX_VALUE
                        )
                        .testTag("MOVIE_TITLE"))
            }

            Row() {
                Column() {
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
                Column(
                ) {
                    Text(text = releaseDate?.substring(0, 4) ?: "", style = MaterialTheme.typography.bodySmall, color = Color.LightGray,
                        modifier = Modifier.testTag("MOVIE_YEAR"))
                }
            }
        }

        // Complete button
        Column(modifier = Modifier
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
                        Log.d("This is the Log ID:", logId.toString())
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MyLogsSection(navController: NavHostController, allLogs: List<LogData>?, scrollState: ScrollState, logViewModel: LogViewModel, friendsViewModel: FriendsViewModel) {
    //val logViewModel: LogViewModel = backStackEntry.logViewModel(navController)
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
            Text("My Logs",
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
            val scaleFactor = if (isClicked) 1.1f else 1f
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

    // Add Log Menu
    var logName by remember { mutableStateOf("") }

    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false },
            containerColor = colorResource(id = R.color.bottomnav),
            modifier = Modifier
                .fillMaxSize()
                .testTag("ADD_LOG_POPUP")
        ) {
            val focusManager = LocalFocusManager.current
            var logIsVisible by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "New Log",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.padding(start = 50.dp, end = 15.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Log Name
                TextField(
                    value = logName,
                    onValueChange = { logName = it },
                    label = { Text(
                        "Log Name",
                        modifier = Modifier.testTag("ADD_LOG_POPUP_LOG_NAME_LABEL")
                    ) },
                    singleLine = true,
                    modifier = Modifier
                        .testTag("LOG_NAME_INPUT"),
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
                        backgroundColor = Color(0xFF373737),
                        cursorColor = Color.White,
                        textColor = Color.White
                    ),
                )
                val icon = if (logIsVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(
                    onClick = {
                        Log.d(TAG, "On click!")
                        logIsVisible = !logIsVisible },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            val userList = friendsViewModel.friendsData.collectAsState()
            val collaboratorsList = remember { mutableStateListOf<String?>() }

            val currentCollab = userList.value.filter { user ->
                collaboratorsList.contains(user.userId.toString())
            }.map { user ->
                user.userId.toString()
            }

            val sortedUserList = userList.value.sortedByDescending { user ->
                currentCollab.contains(user.userId.toString())
            }

            // Collaborators Heading
            Row(modifier = Modifier.padding(start = 14.dp)) {
                Text("Collaborators", style = MaterialTheme.typography.headlineMedium)
            }

            if (currentCollab.isEmpty()) {
                Spacer(modifier = Modifier.height(75.dp))
            } else {
                Spacer(modifier = Modifier.height(15.dp))
                // Current collaborators sections
                LazyRow(modifier = Modifier.padding(start = 24.dp)) {
                    items(collaboratorsList.size) { index ->
                        val userId = collaboratorsList[index]
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
                Text("Add Collaborators", style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Add collaborators section
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
                    items(sortedUserList.size) { index ->
                        val friend = sortedUserList[index]
                        if (currentCollab.contains(friend.userId)) {
                            NewLogCollaborator(friend, collaboratorsList, true)
                        } else {
                            NewLogCollaborator(friend, collaboratorsList, false)
                        }
                    }
                }
            }

            // Create Button tab
            NewLogBottomSection(navController, logName, onCreateClick = { createdLogName ->
                CoroutineScope(Dispatchers.Main).launch {
                    logViewModel.createLog(createdLogName, currentCollab, logIsVisible)
                    isSheetOpen = false
                    logName = ""
                    logViewModel.loadLogs()
                }
            }, onCloseClick = {
                isSheetOpen = false
            })
            Spacer(modifier = Modifier.height(50.dp))
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
                painter = painterResource(id = getAvatarResourceId(friend.avatarPreset ?: 1).second),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
            )
        }

        // Username
        Column(
            modifier = Modifier
                .weight(3F)
                .height(60.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(friend.username ?: "", style = MaterialTheme.typography.bodyLarge)
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
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = Icons.Default.RemoveCircle,
                    contentDescription = "Add Icon",
                    colorFilter = tint(color = Color.Red),
                    modifier = Modifier.size(25.dp)
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

                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add Icon",
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

@Composable
fun NewLogBottomSection(navController: NavController, logName: String, onCreateClick: (String) -> Unit, onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Divider(thickness = 1.dp, color = Color(0xFF303437))
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        val haptic = LocalHapticFeedback.current
        // Create Button
        Button(
            onClick = {
                if (logName.isNotEmpty()) {
                    onCreateClick(logName)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast
                        .makeText(
                            context,
                            "New Log Created!",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 24.dp)
                .testTag("CREATE_LOG_BUTTON"),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.sky_blue),
                disabledContainerColor = Color.LightGray
            ),
            enabled = logName.isNotEmpty()
        ) {
            Text(
                "Create",
                style = MaterialTheme.typography.headlineSmall,
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
        Button(
            onClick = {
                onCloseClick()
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
                "Cancel",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DisplayLogsWithDrag(navController: NavHostController, scrollState: ScrollState, allLogs: List<LogData>?, logViewModel: LogViewModel) {
    //val logViewModel: LogViewModel = backStackEntry.logViewModel(navController)
    //val allLogs by logViewModel.allLogs.collectAsState()


    data class LogPosition(
        var index: Int,
        var logId: String,
        var name: String?,
        val initialX: Float,
        val initialY: Float,
        val top: Float,
        val bottom: Float,
        val left: Float,
        val right: Float,
        var imageUrl: MutableState<String?>
    )

    val draggedItem = remember { mutableStateOf<LogPosition?>(null) }
    val logPositions = remember { mutableStateListOf<LogPosition>() }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxOverlayAlpha by remember { mutableStateOf(0f) }
    var boxOverlayX by remember { mutableStateOf(-1f) }
    var boxOverlayY by remember { mutableStateOf(-1f) }
    var boxShown by remember { mutableStateOf(false) }
    var top by remember { mutableStateOf(0f) }
    var bottom by remember { mutableStateOf(0f) }
    var left by remember { mutableStateOf(0f) }
    var right by remember { mutableStateOf(0f) }
    var middleHorizontal by remember { mutableStateOf(0f) }
    var middleVertical by remember { mutableStateOf(0f) }
    var boxTopInViewPort by remember { mutableStateOf(0f) }
    var boxBottomInViewPort by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0f) }
    /*    val coroutineScope = rememberCoroutineScope()
        var isScrollingNeeded by remember { mutableStateOf(false) }
        var scrollDown by remember { mutableStateOf(true) }*/

    var swapNeeded = false
    var firstIndexToSwap = -1
    var secondIndexToSwap = -1

    val multiplier = ceil(allLogs!!.size / 2.0).toInt()
    val containerHeight: Dp = (185 * multiplier).dp

    Box(
        modifier = Modifier
            .height(containerHeight)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .zIndex(1000f)
        ) {
            if (draggedItem.value != null) {
                val selectedLog = draggedItem.value

                Log.d(TAG, "Selected Log: $selectedLog")

                val painter = if (selectedLog!!.imageUrl.value != null) {
                    rememberAsyncImagePainter(model = selectedLog.imageUrl.value)
                } else {
                    painterResource(id = R.drawable.icon_empty_log) // Default image if URL is null
                }

                Card(
                    //shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .offset { IntOffset(boxOverlayX.roundToInt(), boxOverlayY.roundToInt()) }
                        .onGloballyPositioned { coordinates ->
                            boxTopInViewPort = coordinates.positionInRoot().y
                            boxBottomInViewPort = coordinates.positionInRoot().y + height
                        }
                        .size(175.dp)
                        .alpha(boxOverlayAlpha),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                            /*.align(Alignment.Center)*/
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
                            text = "${selectedLog.name}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .align(Alignment.Center)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                        )
                    }
                    for (log in logPositions) {
                        if (log.logId != selectedLog.logId) {
                            // Selected log is in the first column
                            if (selectedLog.index % 2 == 0) {
                                if (middleHorizontal > log.top) {
                                    if (middleHorizontal < log.bottom) {
                                        if (middleVertical > log.left) {
                                            swapNeeded = true
                                            firstIndexToSwap = selectedLog.index
                                            secondIndexToSwap = log.index
                                            break
                                        }
                                    }
                                }
                                // Selected log is in the second column
                            } else {
                                if ((middleHorizontal > log.top) && (middleHorizontal < log.bottom)) {
                                    if (middleVertical < log.right) {
                                        swapNeeded = true
                                        firstIndexToSwap = selectedLog.index
                                        secondIndexToSwap = log.index
                                        break
                                    }
                                }
                            }
                        }
                    }
                    if (swapNeeded) {
                        val movingLogId = logPositions[firstIndexToSwap].logId
                        val movingLogName = logPositions[firstIndexToSwap].name
                        val movingLogImage = logPositions[firstIndexToSwap].imageUrl

                        if (firstIndexToSwap < secondIndexToSwap) {
                            // Move each logId one position up in the range from firstIndexToSwap to secondIndexToSwap
                            for (i in firstIndexToSwap until secondIndexToSwap) {
                                logPositions[i].logId = logPositions[i + 1].logId
                                logPositions[i].name = logPositions[i + 1].name
                                logPositions[i].imageUrl = logPositions[i + 1].imageUrl
                            }
                        } else {
                            // Move each logId one position down in the range from secondIndexToSwap to firstIndexToSwap
                            for (i in firstIndexToSwap downTo secondIndexToSwap + 1) {
                                logPositions[i].logId = logPositions[i - 1].logId
                                logPositions[i].name = logPositions[i - 1].name
                                logPositions[i].imageUrl = logPositions[i - 1].imageUrl
                            }
                        }

                        logPositions[secondIndexToSwap].logId = movingLogId
                        logPositions[secondIndexToSwap].name = movingLogName
                        logPositions[secondIndexToSwap].imageUrl = movingLogImage
                        draggedItem.value = logPositions[secondIndexToSwap]
                        logViewModel.onMove(firstIndexToSwap, secondIndexToSwap)
                    }
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.matchParentSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(allLogs.size, key = { index -> allLogs[index].logId!! }) { index ->

                var alpha by remember { mutableStateOf(1f) }

                val log = allLogs[index]
                var movieData by remember(log.movieIds?.firstOrNull()) { mutableStateOf<Pair<MovieData?, String>>(null to "") }

                val movieId = log.movieIds?.firstOrNull()
                Log.d(TAG, "First Movie ID: $movieId")
                var painter = painterResource(id = R.drawable.icon_empty_log)

                LaunchedEffect(movieId) {
                    movieId?.let {
                        logViewModel.fetchMovieDetails(it) { result ->
                            result.data?.let { data ->
                                movieData = data
                                // Find the corresponding LogPosition and update its imageUrl
                                logPositions.find { position -> position.logId == log.logId }?.imageUrl?.value = "https://image.tmdb.org/t/p/w500/${movieData.second}"
                            }
                        }
                    } ?: run {
                        logPositions.find { position -> position.logId == log.logId }?.imageUrl?.value = null
                    }
                }

                movieData.first?.let {
                    if (movieData.second != "") {
                        val imageUrl = "https://image.tmdb.org/t/p/w500/${movieData.second}"
                        painter = rememberAsyncImagePainter(model = imageUrl)
                    } else if (it.backdropPath != null) {
                        val imageUrl = "https://image.tmdb.org/t/p/w500/${it.backdropPath}"
                        painter = rememberAsyncImagePainter(model = imageUrl)
                    }
                }
                Log.d(TAG, "Here is the painter: $painter")

                Card(
                    modifier = Modifier
                        .size(175.dp)
                        .clickable { navController.navigate("home_log_details_${log.logId}") }
                        .alpha(alpha)
                        .onGloballyPositioned { coordinates ->
                            height = coordinates.size.height.toFloat()
                            width = coordinates.size.width.toFloat()

                            val currTop = coordinates.positionInParent().y
                            val currBottom = currTop + height
                            val currLeft = coordinates.positionInParent().x
                            val currRight = currLeft + width

                            if (logPositions.none { it.logId == log.logId }) {
                                val newLog = LogPosition(
                                    index,
                                    log.logId!!,
                                    log.name,
                                    coordinates.positionInParent().x,
                                    coordinates.positionInParent().y,
                                    currTop,
                                    currBottom,
                                    currLeft,
                                    currRight,
                                    mutableStateOf(null)
                                )
                                Log.d(TAG, newLog.toString())
                                logPositions.add(newLog)
                            }
                        }
                        /*.clickable { draggedItem.value = logPositions[index] }*/
                        .pointerInput(log) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { _ ->
                                    val currentIndex =
                                        logPositions.indexOfFirst { it.logId == log.logId }
                                    draggedItem.value = logPositions[currentIndex]
                                    offset = offset
                                    alpha = 0f
                                    boxOverlayAlpha = 1f

                                    Log.d(TAG, "Beginning to Drag: ${logPositions[index]}")
                                    Log.d(TAG, "New index we found $currentIndex")
                                    boxOverlayX = logPositions[currentIndex].initialX
                                    boxOverlayY = logPositions[currentIndex].initialY
                                    boxShown = true

                                    Log.d(TAG, "We have begun")
                                },
                                onDrag = { change, dragAmount ->
                                    //change.consume()

                                    // Update positions if within bounds
                                    offset = Offset(
                                        x = offset.x + dragAmount.x,
                                        y = offset.y + dragAmount.y
                                    )

                                    //Log.d(TAG, "Offset: $offset")
                                    boxOverlayX += dragAmount.x
                                    //boxOverlayX = (boxOverlayX + dragAmount.x).coerceIn(0f, parentWidth - width)
                                    //boxOverlayY = (boxOverlayY + dragAmount.y).coerceIn(0f, parentHeight - height)
                                    boxOverlayY += dragAmount.y

                                    /*top = boxOverlayY
                                    if (top < 0) {
                                        middleHorizontal = 0f
                                        middleVertical = 0f
                                        alpha = 1f
                                        boxOverlayAlpha = 0f
                                        boxOverlayX = -1f
                                        boxOverlayY = -1f
                                        offset = Offset(0f, 0f)
                                    } else {*/
                                    top = boxOverlayY
                                    bottom = top + height
                                    left = boxOverlayX
                                    right = left + width

                                    // Calculate the midpoints
                                    middleVertical = (left + right) / 2
                                    middleHorizontal = (top + bottom) / 2

                                    /*if (boxBottomInViewPort > screenHeight) {
                                    isScrollingNeeded = true
                                    } else {
                                        isScrollingNeeded = false
                                    }*/
                                },
                                onDragEnd = {
                                    middleHorizontal = 0f
                                    middleVertical = 0f
                                    alpha = 1f
                                    boxOverlayAlpha = 0f
                                    boxOverlayX = -1f
                                    boxOverlayY = -1f
                                    offset = Offset(0f, 0f)
                                }
                            )
                        },
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
                            text = "${log.name}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .align(Alignment.Center)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                        )
                    }
                }
                //logViewModel.resetNextMovie()
            }
        }
    }
}

