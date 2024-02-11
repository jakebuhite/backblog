package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogRequestData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.util.getAvatarResourceId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequestsScreen(
    friendRequests: List<Pair<FriendRequestData, UserData>>,
    logRequests: List<Pair<LogRequestData, UserData>>,
    friends: State<List<UserData>>,
    addFriend: (String) -> Unit,
) {
    // Add Friend Sheet
    var isSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    AddLogModalBottomSheet(sheetState, isSheetOpen, {isSheetOpen = false}) {
        addFriend(it)
    }

    Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()) {
        Text("Friends", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .testTag("PAGE_SUB_TITLE"))
        Button(onClick = { isSheetOpen = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3891E1),
            )
            ) {
            Image(
                painter = painterResource(id = R.drawable.user_add),
                contentDescription = "Add Friend Request Icon",
                modifier = Modifier
                    .size(30.dp)
                    .testTag("ADD_ICON")
            )
        }
    }

    SocialRequests(friendRequests, logRequests)
    FriendsList(friends)
}

@Composable
fun SocialRequests(friendRequests: List<Pair<FriendRequestData, UserData>>, logRequests: List<Pair<LogRequestData, UserData>>) {
    RequestHeader(title = "Friend Request")

    // List of Friend Requests
    friendRequests.forEach { req ->
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            UserInfo(userData = req.second)
            RequestActions(req.first)
        }
    }

    RequestHeader(title = "Log Request")

    // List of Log Requests
    logRequests.forEach { req ->
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            UserInfo(userData = req.second)
            RequestActions(req.first)
        }
    }

}

@Composable
fun FriendsList(friends: State<List<UserData>>) {
    RequestHeader(title = "Friends")

    // List of Friends
    friends.value.forEach { friend ->
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
            UserInfo(userData = friend)
        }
    }
}

@Composable
fun RequestHeader(title: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(end = 10.dp)
            )
        Box(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(Color.Gray)
        )
    }
}

@Composable
fun UserInfo(userData: UserData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(id = getAvatarResourceId(userData.avatarPreset ?: 1).second),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(50.dp)
        )
        Text(
            text = userData.username ?: "null",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RequestActions(requestData: LogRequestData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.check_icon),
            contentDescription = "Accept request",
            modifier = Modifier
                .size(30.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.delete_icon),
            contentDescription = "Delete request",
            modifier = Modifier
                .size(30.dp)
        )
    }
}

@Composable
fun RequestActions(requestData: FriendRequestData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.check_icon),
            contentDescription = "Accept request",
            modifier = Modifier
                .size(30.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.delete_icon),
            contentDescription = "Delete request",
            modifier = Modifier
                .size(30.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogModalBottomSheet(
    sheetState: SheetState,
    isSheetOpen: Boolean,
    onDismiss: () -> Unit,
    addFriend: (String) -> Unit
) {
    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss,
            containerColor = colorResource(id = R.color.bottomnav),
            modifier = Modifier
                .fillMaxSize()
                .testTag("ADD_FRIEND_POPUP")
        ) {
            val focusManager = LocalFocusManager.current
            var friendsUsername by remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Add Friend",
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
                // Friend Name
                TextField(
                    value = friendsUsername,
                    onValueChange = { friendsUsername = it },
                    label = {
                        Text(
                            "Username",
                            modifier = Modifier.testTag("ADD_FRIEND_POPUP_FRIEND_NAME_LABEL")
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.testTag("FRIEND_NAME_INPUT"),
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
                        textColor = Color.White
                    ),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Create Button tab
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
                // Create Button
                Button(
                    onClick = {
                        if (friendsUsername.isNotEmpty()) {
                            addFriend(friendsUsername)
                            onDismiss()
                            friendsUsername = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 24.dp)
                        .testTag("ADD_FRIEND_BUTTON"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.sky_blue),
                        disabledContainerColor = Color.LightGray
                    ),
                    enabled = friendsUsername.isNotEmpty()
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
                        onDismiss()
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
            Spacer(modifier = Modifier.height(45.dp))
        }
    }
}