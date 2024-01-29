package com.tabka.backblogapp.ui.screens

import android.graphics.Paint.Align
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tabka.backblogapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable
import kotlin.math.ceil
import androidx.compose.material.TextField


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


    BaseScreen(navController, hasBackButton, pageTitle) { scrollState ->

        // If logs exist
        if (!allLogs.isNullOrEmpty()) {
            WatchNextCard(navController, allLogs[0])

        }
        Spacer(Modifier.height(40.dp))
        MyLogsSection(navController, allLogs, scrollState)
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
fun MyLogsSection(navController: NavController, allLogs: List<LogData>?, scrollState: ScrollState) {

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

            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text("New Log",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.padding(horizontal = 50.dp),
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

            Spacer(modifier = Modifier.height(50.dp))

            // Collaborators Heading
            Row(modifier = Modifier.padding(start = 14.dp)) {
                Text("Collaborators", style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(modifier = Modifier.height(15.dp))

            LazyRow(modifier = Modifier.padding(start = 24.dp)) {
                items(4) { index ->
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
                LazyColumn(modifier = Modifier.padding(horizontal = 24.dp)) {
                    items(4) { index ->
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
                                Text("Username", style = MaterialTheme.typography.bodyLarge)
                            }

                            // Add Button
                            Column(
                                modifier = Modifier
                                    .weight(1F)
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
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                // Create Button
                Button(
                    onClick = {
                        if (!logName.isNullOrEmpty()) {
                            createLog(logName)
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
                }
            }
        }
    }


    Spacer(modifier = Modifier.height(15.dp))

    if (!allLogs.isNullOrEmpty()) {
        //ListLogs(navController, allLogs)
        VerticalReorderGrid(navController, allLogs)
    }
}


@Composable
fun VerticalReorderGrid(
    navController: NavController,
    allLogs: List<LogData>
) {

    val logs = remember { mutableStateOf(allLogs) }
    val state = rememberReorderableLazyGridState( onMove = { from, to ->
            logs.value = logs.value.toMutableList().apply {
                add(to.index, removeAt(from.index))
                Log.d(TAG, "After: ${logs.value}")
            }
        }
    )

    //val height = if (allLogs.size % 2 == 1)  ((allLogs.size + 1)/2) * 175 else allLogs.size/2 * 175
    val multiplier = ceil(allLogs.size / 2.0).toInt()
    val height: Dp = (175 * multiplier).dp

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state.gridState,
        modifier = Modifier
            .reorderable(state)
            .detectReorderAfterLongPress(state)
            .height(height),
        userScrollEnabled = false
    ) {
        items(logs.value.size, { it }) { item ->
            ReorderableItem(state, key = item) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "")
                val index = logs.value[item]
                Card(
                    modifier = Modifier
                        .size(175.dp)
                        .clickable { navController.navigate("home_log_details_${index.logId}") }
                        .detectReorderAfterLongPress(state),
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
                        text = "${index.name}",
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



