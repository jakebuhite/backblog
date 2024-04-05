package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BlockedUsersScreen(navController: NavController) {
    val hasBackButton = true
    val isMovieDetails = false
    val pageTitle = "Blocked Users"

    BaseScreen(navController = navController, isBackButtonVisible = hasBackButton, isMovieDetails = isMovieDetails, title = pageTitle) {

    }

    Box(modifier = Modifier.offset(x = 16.dp, y = 20.dp)) {
        BackButton(navController, true)
    }
}