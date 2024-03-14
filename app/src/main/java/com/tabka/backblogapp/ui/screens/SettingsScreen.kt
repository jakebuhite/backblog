package com.tabka.backblogapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.ui.shared.LoadingSpinner
import com.tabka.backblogapp.ui.shared.PasswordTextField
import com.tabka.backblogapp.ui.viewmodels.SettingsViewModel
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.getAvatarResourceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val hasBackButton = true
    val isMovieDetails = false
    val pageTitle = "Settings"

    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) {
        var userData by remember { mutableStateOf<UserData?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            when (val result = settingsViewModel.getUserData()) {
                is DataResult.Success -> {
                    userData = result.item
                    isLoading = false
                }
                is DataResult.Failure -> {
                    val e = result.throwable.message
                    Log.d("SettingsScreen", "Error getting the user's data $e")
                    isLoading = false
                }
            }
        }

        if (isLoading) {
            LoadingSpinner()
        } else {
            SettingsForm(userData, settingsViewModel::updateUserData,
                settingsViewModel::syncLocalLogsToDB,
                settingsViewModel::getLogCount)
        }
    }
    Box(modifier = Modifier.offset(x = 20.dp, y = 20.dp)) {
        BackButton(navController, true)
    }
}

@Composable
fun SettingsForm(userData: UserData?,
                 updateUserData: suspend (Map<String, Any?>, String) -> DataResult<Boolean>,
                 syncLocalLogsToDB: suspend () -> DataResult<Boolean>,
                 getLogCount: () -> Int) {
    // Avatar selection button
    var showDialog by remember { mutableStateOf(false) }
    var selectedAvatar by remember { mutableIntStateOf(userData?.avatarPreset ?: 1) }

    Column {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ){
            Image(
                painter = painterResource(id = getAvatarResourceId(selectedAvatar).second),
                contentDescription = "loading spinner",
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp)
                    .testTag("LOADING_SPINNER_PREVIEW")
            )
            Button(onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3891E1)
                ),
                shape = RoundedCornerShape(35),
                modifier = Modifier
                    .height(70.dp)
                    .width(200.dp)
                    .padding(12.dp)
                    .testTag("CHANGE_AVATAR_BUTTON")
            )
            {
                Text("Change Avatar", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }

        // AvatarSelectionDialog visibility
        if (showDialog) {
            AvatarSelectionDialog(
                onDismiss = { showDialog = false },
                onAvatarSelected = { avatarId -> selectedAvatar = avatarId }
            )
        }

        // Username field
        var username by remember { mutableStateOf("") }
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            placeholder = { Text(userData?.username ?: "") },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .testTag("USERNAME_TEXT_INPUT"),
            textStyle = TextStyle(color = Color(0xE6FFFFFF)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF373737),
                focusedLabelColor = Color(0xFF979C9E),
                unfocusedLabelColor = Color(0xFF979C9E),
                unfocusedBorderColor = Color(0xFF373737),
                backgroundColor = Color(0xFF373737)
            ),
            singleLine = true
        )

        // Old Password field
        var password by remember { mutableStateOf("") }
        PasswordTextField(
            "Old Password",
        ) { pwd -> password = pwd }

        // New Password field
        var newPassword by remember { mutableStateOf("") }
        PasswordTextField("New Password") { pwd -> newPassword = pwd }

        // Status Message
        var visible by remember { mutableStateOf(false) }
        var statusText by remember { mutableStateOf("") }
        var statusColor by remember { mutableStateOf(Color(0xFF4BB543)) }
        if (visible) {
            Text(text = statusText,
                style = MaterialTheme.typography.bodyMedium.copy(color = statusColor),
                modifier = Modifier
                    .padding(12.dp)
                    .testTag("SYNC_STATUS_MESSAGE")
            )
        }

        // Sync local data
        if (getLogCount() > 0) {
            Button(onClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    when (val result = syncLocalLogsToDB()) {
                        is DataResult.Success -> {
                            statusText = "Local logs successfully synced!"
                            statusColor = Color(0xFF4BB543)
                            visible = true
                        }
                        is DataResult.Failure -> {
                            val e = result.throwable.message
                            Log.d("SettingsScreen", "Error syncing logs $e")
                            statusText = "Error syncing logs."
                            statusColor = Color(0xFFCC0000)
                            visible = true
                        }
                    }
                }
            },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3891E1)
                ),
                shape = RoundedCornerShape(35),
                modifier = Modifier
                    .height(70.dp)
                    .width(200.dp)
                    .padding(12.dp)
                    .testTag("SYNC_LOGS_BUTTON")
            )
            {
                Text(
                    "Sync Logs to DB",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.testTag("SYNC_LOGS_TEXT")
                )
            }
        }

        // Update settings button
        Button(onClick = {
            CoroutineScope(Dispatchers.Default).launch {
                if (password.isNotEmpty()) {
                    // Check if any field was updated
                    if (username.isNotEmpty() || newPassword.isNotEmpty() || selectedAvatar != (userData?.avatarPreset ?: 1)) {
                        val updates = mutableMapOf<String, Any>()

                        if (username.isNotEmpty()) { updates["username"] = username }
                        if (newPassword.isNotEmpty()) { updates["password"] = newPassword }
                        if (selectedAvatar != (userData?.avatarPreset ?: 1)) { updates["avatar_preset"] = selectedAvatar }

                        when (val result = updateUserData(updates, password)) {
                            is DataResult.Success -> {
                                statusText = "Settings successfully updated!"
                                statusColor = Color(0xFF4BB543)
                                visible = true
                            }
                            is DataResult.Failure -> {
                                val e = result.throwable.message
                                Log.d("SettingsScreen", "Error updating settings $e")
                                statusText = "Error updating user settings"
                                statusColor = Color(0xFFCC0000)
                                visible = true
                            }
                        }
                    } else {
                        // No field was updated
                        statusText = "Error: Please enter a field to update"
                        statusColor = Color(0xFFCC0000)
                        visible = true
                    }
                } else {
                    // Password field is empty
                    statusText = "Error: Please enter your current password"
                    statusColor = Color(0xFFCC0000)
                    visible = true
                }
            }
        },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF3891E1)
            ),
            shape = RoundedCornerShape(35),
            modifier = Modifier
                .height(70.dp)
                .width(200.dp)
                .padding(12.dp)
                .testTag("UPDATE_SETTINGS_BUTTON")
        )
        {
            Text("Update Settings", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}

@Composable
fun AvatarSelectionDialog(
    onDismiss: () -> Unit,
    onAvatarSelected: (Int) -> Unit
) {
    // Use Dialog to display avatar options
    Dialog(onDismissRequest = onDismiss) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(start = 16.dp)
                .testTag("AVATAR_SELECTION_DIALOG")
        ) {
            // Display six avatar options in a grid
            items(6) { avatarId ->
                AvatarOption(avatarId + 1, onDismiss, onAvatarSelected)
            }
        }
    }
}

@Composable
fun AvatarOption(
    avatarId: Int,
    onDismiss: () -> Unit,
    onAvatarSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                onAvatarSelected(avatarId)
                onDismiss()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (imageName, imageId) = getAvatarResourceId(avatarId)
        Image(
            painter = painterResource(id = imageId),
            contentDescription = null,
            modifier = Modifier
                .height(120.dp)
                .width(120.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Text(
            text = imageName,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
            )
        )
    }
}