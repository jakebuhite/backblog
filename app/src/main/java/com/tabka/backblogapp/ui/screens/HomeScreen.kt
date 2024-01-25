package com.tabka.backblogapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tabka.backblogapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import org.burnoutcrew.reorderable.NoDragCancelledAnimation
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import kotlin.math.roundToInt


private val TAG = "HomeScreen"
private val logViewModel: LogViewModel = LogViewModel()


@RequiresApi(Build.VERSION_CODES.O)
fun createLog(logName: String) {
    logViewModel.createLog(logName)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    val allLogs = logViewModel.allLogs.collectAsState().value

    val hasBackButton = false
    val pageTitle = "What's Next?"


    BaseScreen(navController, hasBackButton, pageTitle) {
        // If logs exist
        if (!allLogs.isNullOrEmpty()) {
            WatchNextCard(navController, allLogs[0])

        }
        Spacer(Modifier.height(40.dp))
        MyLogsSection(navController, allLogs)
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

    var image = R.drawable.icon_empty_log

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
            modifier = Modifier.clickable{ navController.navigate("home_movie_details_0")
            }
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
fun MyLogsSection(navController: NavController, allLogs: List<LogData>?) {

    val sheetState = rememberModalBottomSheetState()
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

                // Log Name
                TextField(
                    value = logName,
                    onValueChange = { logName = it },
                    label = { Text("Log Name") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                // Create Button
                Button(
                    onClick = {
                        if (!logName.isNullOrEmpty()) {
                            createLog(logName)
                            isSheetOpen = false
                            logName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.sky_blue),
                        disabledContainerColor = colorResource(id = R.color.sky_blue)
                    )
                ) {
                    Text("Create Log")
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(15.dp))

    if (!allLogs.isNullOrEmpty()) {
        ListLogs(navController, allLogs)
    }
}








@Composable
fun ListLogs(navController: NavController, allLogs: List<LogData>) {
    val items = remember { mutableStateListOf<LogData>().apply { addAll(allLogs) } }
    val draggedItemIndex = remember { mutableStateOf(-1) }

    Column(modifier = Modifier.fillMaxWidth()) {
        val itemsPerRow = 2
        items.chunked(itemsPerRow).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { logData ->
                    val index = items.indexOf(logData)
                    DraggableCard(
                        logData = logData,
                        index = index,
                        items = items.size,
                        draggedItemIndex = draggedItemIndex,
                        onMove = { fromIndex, toIndex ->
                            if (toIndex in items.indices) {
                                val movedItem = items.removeAt(fromIndex)
                                items.add(toIndex, movedItem)
                            }
                        },
                        navController = navController
                    )
                }
            }
        }
    }
}


@Composable
fun DraggableCard(
    logData: LogData,
    index: Int,
    items: Int,
    draggedItemIndex: MutableState<Int>,
    onMove: (Int, Int) -> Unit,
    navController: NavController
) {
    var offset by remember { mutableStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .offset { IntOffset(0, offset.roundToInt()) }
            .size(175.dp)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    offset += delta
                    if (!dragging) {
                        draggedItemIndex.value = index
                    }
                },
                onDragStarted = { dragging = true },
                onDragStopped = {
                    dragging = false
                    offset = 0f
                    draggedItemIndex.value = -1
                    val newIndex = calculateNewIndex(offset, index, 2, items)
                    if (newIndex != index) {
                        onMove(index, newIndex)
                    }
                }
            )
            .clickable { navController.navigate("home_log_details_${logData.logId}") },
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (draggedItemIndex.value == index) 20.dp else 15.dp
        )
    ) {
        Box(
            modifier = Modifier
                .size(175.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.creator),
                contentDescription = null,
                contentScale = ContentScale.Crop
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
                text = logData.name!!,
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


fun calculateNewIndex(delta: Float, currentIndex: Int, itemsPerRow: Int, totalItems: Int): Int {
    val newIndex = currentIndex + (if (delta > 0) 1 else -1) * itemsPerRow
    return newIndex.coerceIn(0, totalItems - 1)
}
























/*@Composable
fun ListLogs(navController: NavController, allLogs: List<LogData>) {
    // Mutable state list to manage the order of LogData items
    val items = remember { mutableStateListOf<LogData>().apply { addAll(allLogs) } }
    val draggedItemIndex = remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val itemsPerRow = 2
        items.chunked(itemsPerRow).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { logData ->
                    val index = items.indexOf(logData)
                    DraggableCard(
                        logData = logData,
                        index = index,
                        items = items.size,
                        draggedItemIndex = draggedItemIndex,
                        onMove = { fromIndex, toIndex ->
                            if (toIndex in items.indices) {
                                val movedItem = items.removeAt(fromIndex)
                                items.add(toIndex, movedItem)
                            }
                        },
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun DraggableCard(
    logData: LogData,
    index: Int,
    items: Int,
    draggedItemIndex: MutableState<Int>,
    onMove: (Int, Int) -> Unit,
    navController: NavController
) {
    var offset by remember { mutableStateOf(0f) }

    Card(
        modifier = Modifier
            .offset { IntOffset(0, offset.roundToInt()) }
            .size(175.dp)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    offset += delta
                    val newIndex = calculateNewIndex(delta, index, 2, items)
                    if (newIndex != index) {
                        onMove(index, newIndex)
                    }
                },
                onDragStopped = {
                    offset = 0f
                }
            )
            .clickable { navController.navigate("home_log_details_${logData.logId}") },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (draggedItemIndex.value == index) 15.dp else 10.dp
        )
    ) {
        Box(
            modifier = Modifier
                .size(175.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.creator),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            // Text overlay
            Text(
                text = "${logData.name}",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(175.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .drawBehind {
                        drawRoundRect(
                            color = Color.Black,
                            cornerRadius = CornerRadius(20.dp.toPx()),
                            alpha = 0.75f
                        )
                    }
            )
        }
    }
}

// Utility function to calculate the new index based on the drag delta
fun calculateNewIndex(delta: Float, currentIndex: Int, itemsPerRow: Int, totalItems: Int): Int {
    val newIndex = currentIndex + (if (delta > 0) 1 else -1) * itemsPerRow
    return newIndex.coerceIn(0, totalItems - 1)
}*/



























/*
@Composable
fun ListLogs(navController: NavController, allLogs: List<LogData>) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val itemsPerRow = 2
        val rows = allLogs?.chunked(itemsPerRow)

        rows?.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { index ->
                    Card(
                        modifier = Modifier
                            .size(175.dp)
                            .clickable { navController.navigate("home_log_details_${index.logId}") },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(175.dp)
                                .fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.creator),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )

                            // Text overlay
                            Text(
                                text = "${index.name}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .size(175.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .drawBehind {
                                        drawRoundRect(
                                            color = Color.Black,
                                            cornerRadius = CornerRadius(20.dp.toPx()),
                                            alpha = 0.75f
                                        )
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}*/



