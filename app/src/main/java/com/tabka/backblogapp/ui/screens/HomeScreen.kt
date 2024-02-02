package com.tabka.backblogapp.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.ui.bottomnav.logViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import kotlin.math.ceil
import kotlin.math.roundToInt


private val TAG = "HomeScreen"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavHostController, backStackEntry: NavBackStackEntry) {
    val logViewModel: LogViewModel = backStackEntry.logViewModel(navController)
    val allLogs by logViewModel.allLogs.collectAsState()

    val hasBackButton = false
    val pageTitle = "What's Next?"


    BaseScreen(navController, hasBackButton, pageTitle) { scrollState ->

        // If logs exist
        if (!allLogs.isNullOrEmpty()) {
            WatchNextCard(navController, allLogs!![0])

        }
        Spacer(Modifier.height(40.dp))
        MyLogsSection(navController, backStackEntry, allLogs, scrollState)

    }
}


@Composable
fun WatchNextCard(navController: NavController, priorityLog: LogData) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        PriorityLogTitle(priorityLog.name!!)

        Spacer(modifier = Modifier.height(5.dp))

        val movieId = priorityLog.movieIds?.keys?.firstOrNull()
        if (movieId != null) {
            // val movie: MovieData = Get movie by Id

            // Change to movie
            NextMovie(navController, movieId)
            Spacer(modifier = Modifier.height(5.dp))
            NextMovieInfo(movieId)
        } else {
            NextMovie(navController, null)
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}


@Composable
fun PriorityLogTitle(logName: String) {
    Row() {
        Text("From $logName", style = MaterialTheme.typography.titleSmall)
    }
}


@Composable
fun NextMovie(navController: NavController, movie: String?) {

    val image = R.drawable.icon_empty_log

    if (movie != null) {
     /*   if (movie.backdrop != null) {
            image = URL of movie.backdrop
        } else {
            //image = URL of movie.half_sheet
        }*/
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
                    painter = painterResource(id = image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Composable
fun NextMovieInfo(movie: String) {

    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        // Movie information
        Column(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        )
        {
            // Title
            Row() {
                Text(text = "Tenet", style = MaterialTheme.typography.headlineMedium)
            }

            Row() {
                // Rating
                Column() {
                    Text(text = "PG-13", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.width(5.dp))

                // Release Date
                Column(
                ) {
                    Text(text = "2022", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Complete button
        Column(modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
            horizontalAlignment = Alignment.End
        ) {
            Image(
                painter = painterResource(id = R.drawable.checkbutton2),
                contentDescription = "Check icon",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


// This function creates the "My Logs" header, as well as the button to create a new log
// This function then calls ListLogs, which will list each log the user has
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MyLogsSection(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    allLogs: List<LogData>?,
    scrollState: ScrollState
) {
    val logViewModel: LogViewModel = backStackEntry.logViewModel(navController)
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
            Text("My Logs", style = MaterialTheme.typography.headlineMedium)
        }

        // Add Log Button
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
            /*.padding(end = 5.dp)*/,
            horizontalAlignment = Alignment.End
        ) {
            Image(
                imageVector = Icons.Default.AddToPhotos,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(35.dp)
                    .clickable { isSheetOpen = true },
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
            modifier = Modifier.fillMaxSize()
        ) {
            val focusManager = LocalFocusManager.current

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
                modifier = Modifier.padding(horizontal = 50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Log Name
                TextField(
                    value = logName,
                    onValueChange = { logName = it },
                    label = { Text("Log Name") },
                    singleLine = true,
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

            Spacer(modifier = Modifier.height(20.dp))

            // Collaborators Heading
            Row(modifier = Modifier.padding(start = 14.dp)) {
                Text("Collaborators", style = MaterialTheme.typography.headlineMedium)
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
                            colorFilter = tint(color = colorResource(id = R.color.white))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Add Collaborators Heading
            Row(modifier = Modifier.padding(start = 14.dp)) {
                Text("Add Collaborators", style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(modifier = Modifier.height(15.dp))

            Box(modifier = Modifier.height(265.dp)) {
                LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
                    items(userList) { displayName ->
                        NewLogCollaborator(displayName)
                    }
                }
            }

/*            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                // Create Button
                Button(
                    onClick = {
                        if (logName.isNotEmpty()) {
                            logViewModel.createLog(logName)
                            isSheetOpen = false
                            logName = ""
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
                    Text("Create", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }*/
          
            Spacer(modifier = Modifier.height(10.dp))

            // Create Button tab
            NewLogBottomSection(logName) {createdLogName ->
                logViewModel.createLog(createdLogName)
                isSheetOpen = false
                logName = ""

            }
        }
    }

    Spacer(modifier = Modifier.height(15.dp))

    if (!allLogs.isNullOrEmpty()) {
        DisplayLogsWithDrag(navController, backStackEntry, scrollState, allLogs)
        Log.d(TAG, "ALL LOGS: $allLogs")
    }
}

@Composable
fun DisplayLogsWithDrag(navController: NavHostController, backStackEntry: NavBackStackEntry, scrollState: ScrollState, allLogs: List<LogData>?) {
    val logViewModel: LogViewModel = backStackEntry.logViewModel(navController)
    val allLogs by logViewModel.allLogs.collectAsState()

    data class LogPosition(
        var index: Int,
        var logId: String,
        var name: String?,
        val initialX: Float,
        val initialY: Float,
        val top: Float,
        val bottom: Float,
        val left: Float,
        val right: Float
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
    val coroutineScope = rememberCoroutineScope()
    var isScrollingNeeded by remember { mutableStateOf(false) }
    var scrollDown by remember { mutableStateOf(true) }

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
                Log.d(TAG, "SELECTED LOG: $selectedLog")

                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .offset { IntOffset(boxOverlayX.roundToInt(), boxOverlayY.roundToInt()) }
                        .onGloballyPositioned { coordinates ->
                            boxTopInViewPort = coordinates.positionInRoot().y
                            boxBottomInViewPort = coordinates.positionInRoot().y + height
                        }
                        .size(175.dp)
                        .alpha(boxOverlayAlpha)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.creator),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
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
                            text = "${selectedLog!!.name}",
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
                        if (log.logId != selectedLog!!.logId) {
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

                        if (firstIndexToSwap < secondIndexToSwap) {
                            // Move each logId one position up in the range from firstIndexToSwap to secondIndexToSwap
                            for (i in firstIndexToSwap until secondIndexToSwap) {
                                logPositions[i].logId = logPositions[i + 1].logId
                                logPositions[i].name = logPositions[i + 1].name
                            }
                        } else {
                            // Move each logId one position down in the range from secondIndexToSwap to firstIndexToSwap
                            for (i in firstIndexToSwap downTo secondIndexToSwap + 1) {
                                logPositions[i].logId = logPositions[i - 1].logId
                                logPositions[i].name = logPositions[i - 1].name
                            }
                        }

                        logPositions[secondIndexToSwap].logId = movingLogId
                        logPositions[secondIndexToSwap].name = movingLogName
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
            items(allLogs!!.size, key = { index -> allLogs!![index].logId!! }) { index ->

                var alpha by remember { mutableStateOf(1f) }

                val log = allLogs!![index]
                Card(
                    modifier = Modifier
                        .size(175.dp)
                        .clickable { navController.navigate("home_log_details_${log.logId}") }
                        .alpha(alpha)
                        .onGloballyPositioned { coordinates ->
                            height = coordinates.size.height.toFloat()
                            width = coordinates.size.width.toFloat()

                            val currTop = coordinates.positionInParent().y.toFloat()
                            val currBottom = currTop + height
                            val currLeft = coordinates.positionInParent().x.toFloat()
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
                                    currRight
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

                                    Log.d(TAG, "Box Top: $boxOverlayY")


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
                    shape = RoundedCornerShape(20.dp),
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
                            painter = painterResource(id = R.drawable.creator),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
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
            }
        }
    }
}

@Composable
fun NewLogCollaborator(displayName: String) {
    Row(
        modifier = Modifier.padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // User Icon
        Column(modifier = Modifier.weight(1F)) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp),
                colorFilter = tint(color = colorResource(id = R.color.white))
            )
        }

        // Username
        Column(
            modifier = Modifier
                .weight(3F)
                .height(60.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(displayName, style = MaterialTheme.typography.bodyLarge)
        }

        // Add Button
        Column(
            modifier = Modifier
                .width(40.dp)
                .height(60.dp),
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

@Composable
fun NewLogBottomSection(logName: String, onCreateClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Divider(thickness = 1.dp, color = Color(0xFF303437))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // Create Button
        Button(
            onClick = {
                if (!logName.isNullOrEmpty()) {
                    onCreateClick(logName)
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
            Text(
                "Create",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}




/*
@Composable
fun ListLogs(navController: NavController, allLogs: List<LogData>) {
                    for (log in logPositions) {
                        if (log.logId != selectedLog!!.logId) {
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

                        if (firstIndexToSwap < secondIndexToSwap) {
                            // Move each logId one position up in the range from firstIndexToSwap to secondIndexToSwap
                            for (i in firstIndexToSwap until secondIndexToSwap) {
                                logPositions[i].logId = logPositions[i + 1].logId
                                logPositions[i].name = logPositions[i + 1].name
                            }
                        } else {
                            // Move each logId one position down in the range from secondIndexToSwap to firstIndexToSwap
                            for (i in firstIndexToSwap downTo secondIndexToSwap + 1) {
                                logPositions[i].logId = logPositions[i - 1].logId
                                logPositions[i].name = logPositions[i - 1].name
                            }
                        }

                        logPositions[secondIndexToSwap].logId = movingLogId
                        logPositions[secondIndexToSwap].name = movingLogName
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
            items(allLogs!!.size, key = { index -> allLogs[index].logId!! }) { index ->

                var alpha by remember { mutableStateOf(1f) }

                val log = allLogs[index]
                Card(
                    modifier = Modifier
                        .size(175.dp)
                        .clickable { navController.navigate("home_log_details_${log.logId}") }
                        .alpha(alpha)
                        .onGloballyPositioned { coordinates ->
                            height = coordinates.size.height.toFloat()
                            width = coordinates.size.width.toFloat()

                            val currTop = coordinates.positionInParent().y.toFloat()
                            val currBottom = currTop + height
                            val currLeft = coordinates.positionInParent().x.toFloat()
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
                                    currRight
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

                                    Log.d(TAG, "Box Top: $boxOverlayY")


                                    // Calculate the midpoints
                                    middleVertical = (left + right) / 2
                                    middleHorizontal = (top + bottom) / 2

                                    /*                                    if (boxBottomInViewPort > screenHeight) {
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
                    shape = RoundedCornerShape(20.dp),
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
                            painter = painterResource(id = R.drawable.creator),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
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
            }
        }
    }
}
*/