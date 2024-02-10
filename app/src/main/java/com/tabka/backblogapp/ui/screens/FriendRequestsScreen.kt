package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogRequestData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.util.getAvatarResourceId

@Composable
fun FriendRequestsScreen(
    friendRequests: List<Pair<FriendRequestData, UserData>>,
    logRequests: List<Pair<LogRequestData, UserData>>,
    friends: State<List<UserData>>
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()) {
        Text("Friends", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .testTag("PAGE_SUB_TITLE"))
        Button(onClick = { /*TODO*/ },
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