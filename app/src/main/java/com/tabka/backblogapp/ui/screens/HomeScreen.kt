package com.tabka.backblogapp.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tabka.backblogapp.R
import com.tabka.backblogapp.models.LogData
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController


private val TAG = "HomeScreen"
private val logViewModel: LogViewModel = LogViewModel()

@Composable
fun HomeScreen() {
    val allLogs = logViewModel.allLogs.collectAsState().value
    val pageTitle = "What's Next?"

    BaseScreen(pageTitle) {
        WatchNextCard()
        Spacer(Modifier.height(40.dp))
        MyLogsSection(allLogs)
    }
}

@Preview
@Composable
fun WatchNextCard() {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Priority Log title
        Row() {
            Text("From Personal Log", style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Next Movie image
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(
                    modifier = Modifier.height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tenetdefault),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Movie Title, Rating, Year, Complete button
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
}

// This function creates the "My Logs" header, as well as the button to create a new log
// This function then calls ListLogs, which will list each log the user has
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MyLogsSection(allLogs: List<LogData>?) {

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
                        createLog(logName)
                        isSheetOpen = false
                        logName = ""
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

    listLogs(allLogs)
}

@Composable
fun listLogs(allLogs: List<LogData>?) {
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
                        modifier = Modifier.size(175.dp),
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
}

@RequiresApi(Build.VERSION_CODES.O)
fun createLog(logName: String) {
    Log.d(TAG, logName)
    logViewModel.createLog(logName)
}

