package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.content.MediaType.Companion.Text
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BlockedUsersScreen(navController: NavController) {
    val hasBackButton = true
    val isMovieDetails = false
    val pageTitle = "Blocked Users"
    val blockedUsers = 0

    BaseScreen(navController = navController, isBackButtonVisible = hasBackButton, isMovieDetails = isMovieDetails, title = pageTitle) {
        if (blockedUsers == 0) {
            Spacer(modifier = Modifier.height(250.dp))

            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.Text(
                    "You have no blocked users",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
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
//                        items(blockedUsers.size) { index ->
//                           blockedUserEntry()
//                        }
                    }
            }
        }
    }

    Box(modifier = Modifier.offset(x = 16.dp, y = 20.dp)) {
        BackButton(navController, true)
    }
}

@Composable
fun blockedUserEntry() {

}