package com.tabka.backblogapp.ui.shared

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R
import com.tabka.backblogapp.ui.screens.NewLogCollaborator
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.util.getAvatarResourceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NewLogMenu(friendsViewModel: FriendsViewModel, logViewModel: LogViewModel, isLoggedIn: Boolean, onCreateClick: () -> Unit,
               onCloseClick: () -> Unit, navController: NavController) {
    var logName by remember { mutableStateOf("") }
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
        //modifier = Modifier.padding(start = 50.dp, end = 15.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Log Name
        TextField(
            value = logName,
            onValueChange = { logName = it },
            label = {
                Text(
                    "Log Name",
                    modifier = Modifier.testTag("ADD_LOG_POPUP_LOG_NAME_LABEL")
                )
            },
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
        if (isLoggedIn) {
            val icon =
                if (logIsVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(
                onClick = {
                    logIsVisible = !logIsVisible
                },
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

    if (isLoggedIn) {
        // Collaborators Heading
        Row(modifier = Modifier.padding(start = 14.dp)) {
            Text("Collaborators", style = MaterialTheme.typography.headlineMedium)
        }

        if (currentCollab.isEmpty()) {
            Spacer(modifier = Modifier.height(85.dp))
        } else {
            Spacer(modifier = Modifier.height(15.dp))
            // Current collaborators sections
            LazyRow(modifier = Modifier.padding(start = 24.dp)) {
                items(collaboratorsList.size) { index ->
                    val userId = collaboratorsList[index]
                    val friend = userList.value.find { it.userId == userId }

                    /*Column() {*/
                    Image(
                        painter = painterResource(
                            id = getAvatarResourceId(
                                friend?.avatarPreset ?: 1
                            ).second
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .padding(end = 10.dp),
                    )
                    /*}*/
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
        Box(modifier = Modifier.height(290.dp)) {
            LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
                items(sortedUserList.size) { index ->
                    val friend = sortedUserList[index]
                    if (currentCollab.contains(friend.userId)) {
                        NewLogCollaborator(friend, collaboratorsList, true, navController)
                    } else {
                        NewLogCollaborator(friend, collaboratorsList, false, navController)
                    }
                }
            }
        }
    }
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
                    onCreateClick()
                    CoroutineScope(Dispatchers.Main).launch {
                        logViewModel.createLog(logName, currentCollab, logIsVisible)
                        logName = ""
                        logViewModel.loadLogs()
                    }
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
                "CREATE",
                style = MaterialTheme.typography.titleMedium,
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
                "CANCEL",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}