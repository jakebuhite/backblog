package com.tabka.backblogapp.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.util.getAvatarResourceId

@Composable
fun UserInfo(navController: NavController, userData: UserData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.clickable {
            if (Firebase.auth.currentUser?.uid == userData.userId) {
                navController.navigate("friends")
            } else {
                navController.navigate("friends_page_${userData.userId ?: ""}")
            }
        }
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