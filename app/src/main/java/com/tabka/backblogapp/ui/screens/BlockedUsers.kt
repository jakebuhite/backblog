package com.tabka.backblogapp.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.util.getAvatarResourceId
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction0
import kotlin.reflect.KSuspendFunction1

@SuppressLint("MutableCollectionMutableState")
@Composable
fun BlockedUsersScreen(navController: NavController, getBlockedUsers: KSuspendFunction0<List<UserData>>, unBlockUser: KSuspendFunction1<String, Boolean>) {
    val hasBackButton = true
    val isMovieDetails = false
    val pageTitle = "Blocked Users"
    var blockedUsers by remember { mutableStateOf<List<UserData>>(listOf()) }

    LaunchedEffect(Unit) {
        blockedUsers = getBlockedUsers().toMutableList()
    }

    BaseScreen(navController = navController, isBackButtonVisible = hasBackButton, isMovieDetails = isMovieDetails, title = pageTitle) {
        if (blockedUsers.isEmpty()) {
            Spacer(modifier = Modifier.height(250.dp))

            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "You have no blocked users",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
        else {
                Box(modifier = Modifier.height(650.dp)) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 15.dp)
                    ) {
                        items(blockedUsers.size) { index ->
                           BlockedUserEntry(blockedUsers[index], unBlockUser) { unBlockedUser ->
                               val mutableBlockedUsers = blockedUsers.toMutableList()
                               mutableBlockedUsers.removeAll { user -> user.userId == unBlockedUser }
                               blockedUsers = mutableBlockedUsers.toList()
                               Log.d("BLOCKEDUSERS", "New blocked users: $blockedUsers")
                           }
                        }
                    }
            }
        }
    }

    Box(modifier = Modifier.offset(x = 16.dp, y = 20.dp)) {
        BackButton(navController, true)
    }
}

@Composable
fun BlockedUserEntry(
    userData: UserData,
    unBlockUser: KSuspendFunction1<String, Boolean>,
    removeUser: (userId: String) -> Unit
) {
    val composableScope = rememberCoroutineScope()
    val context = LocalContext.current

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
                        userData.avatarPreset ?: 1
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
                userData.username ?: "",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag("NEW_LOG_COLLABORATOR_USERNAME")
            )
        }

        Column(
            modifier = Modifier
                .width(40.dp)
                .height(60.dp)
                .clickable {
                    if (userData.userId == null) {
                        return@clickable
                    }
                    composableScope.launch {
                        if (unBlockUser(userData.userId)) {
                            // Successful unblock
                            removeUser(userData.userId)
                            Toast.makeText(
                                context,
                                "Successfully unblocked ${userData.username}!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Error unblocking user!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                .testTag("REMOVE_BLOCKED_USER_BUTTON"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = Icons.Default.RemoveCircle,
                contentDescription = "Add Icon",
                colorFilter = ColorFilter.tint(color = Color.Red),
                modifier = Modifier
                    .size(30.dp)
                    .testTag("REMOVE_BLOCKED_USER_ICON")
            )
        }
    }
}