package com.tabka.backblogapp.ui.shared

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.ui.viewmodels.LogViewModel

@Composable
fun AddToLogMenu(logViewModel: LogViewModel, movie: MovieData, onCreateNewLog: () -> Unit, onCloseAddMenu: () -> Unit) {
    val allLogs by logViewModel.allLogs.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    if (allLogs != null) {
        val checkedStates =
            remember { mutableStateListOf<Boolean>().apply { addAll(List(allLogs!!.size) { false }) } }

        Spacer(modifier = Modifier.height(5.dp))

        Box(modifier = Modifier.height(150.dp)) {
            LazyColumn(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                items(allLogs!!.size) { index ->
                    val log = allLogs!![index]

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            checkedStates[index] = !checkedStates[index]
                        }
                    ) {
                        Column(modifier = Modifier.weight(3F)) {
                            androidx.compose.material.Text(
                                log.name!!,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1F),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
//                            Checkbox(
//                                checked = checkedStates[index],
//                                onCheckedChange = { isChecked ->
//                                    checkedStates[index] = isChecked
//                                },
//                                modifier = Modifier
//                                    .testTag("LOG_CHECKBOX")
//                            )
                            CircleCheckbox(selected = checkedStates[index], onChecked = {
                                    isChecked -> checkedStates[index] = isChecked as Boolean
                            })
                        }
                    }
                }
            }
        }

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
            var isAnyChecked by remember { mutableStateOf(false) }
            isAnyChecked = checkedStates.any { it }
            // Add Button
            if (isAnyChecked) {
                androidx.compose.material3.Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 24.dp)
                        .testTag("ADD_TO_LOG_BUTTON"),
                    onClick = {
                        // Use the checkedStates list to find out which checkboxes are checked
                        if (isAnyChecked) {
                            val checkedItems =
                                allLogs!!.indices.filter { checkedStates[it] }
                            //Log.d(com.tabka.backblogapp.ui.screens.TAG, "Checked Items: $checkedItems")
                            for (checkedItem in checkedItems) {
                                val log = allLogs!![checkedItem]

                                logViewModel.addMovieToLog(
                                    log.logId,
                                    movie.id.toString()
                                )
                                /*Log.d(TAG, allLogs)*/
                            }
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast
                                .makeText(
                                    context,
                                    "Movie added to log!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            onCloseAddMenu()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.sky_blue),
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    androidx.compose.material.Text(
                        /*"Add"*/
                        "ADD TO LOG",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                androidx.compose.material3.Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 24.dp)
                        .testTag("CREATE_NEW_LOG_BUTTON"),
                    onClick = {
                        onCreateNewLog()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.sky_blue),
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    androidx.compose.material.Text(
                        /*"Add"*/
                        "CREATE NEW LOG",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Cancel Button
            androidx.compose.material3.Button(
                onClick = {
                    onCloseAddMenu()
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
                    "CANCEL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

    }
}

@Composable
fun CircleCheckbox(selected: Boolean, enabled: Boolean = true, onChecked: (Any?) -> Unit) {

    val color = MaterialTheme.colorScheme
    val imageVector = if (selected) Icons.Filled.CheckCircle else Icons.Outlined.Circle
    val tint = if (selected) colorResource(id = R.color.sky_blue) else Color.White.copy(alpha = 0.8f)
    val background = Color.Transparent

    IconButton(onClick = { onChecked(!selected) },
        modifier = Modifier.offset(x = 4.dp, y = 4.dp),
        enabled = enabled) {

        Icon(imageVector = imageVector, tint = tint,
            modifier = Modifier.background(background, shape = CircleShape),
            contentDescription = "checkbox")
    }
}