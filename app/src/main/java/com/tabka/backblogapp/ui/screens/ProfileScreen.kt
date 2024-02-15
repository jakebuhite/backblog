package com.tabka.backblogapp.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    val hasBackButton = true
    val isMovieDetails = false
    val pageTitle = "Friends"

    BaseScreen(navController, hasBackButton, isMovieDetails, pageTitle) {
        Text("This is the profile screen")
    }
}
