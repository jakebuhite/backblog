package com.tabka.backblogapp.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.network.models.UserData

@Composable
fun FriendsList(navController: NavController, friends: State<List<UserData>>) {
    RequestHeader(title = "Friends")

    // List of Friends
    friends.value.forEach { friend ->
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
            UserInfo(navController = navController, userData = friend)
        }
    }
}