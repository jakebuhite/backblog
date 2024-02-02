package com.tabka.backblogapp.ui.screens

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    val hasBackButton = true
    val pageTitle = "Friends"

    BaseScreen(navController, hasBackButton, pageTitle) {
        Text("This is the profile screen")
    }
}
