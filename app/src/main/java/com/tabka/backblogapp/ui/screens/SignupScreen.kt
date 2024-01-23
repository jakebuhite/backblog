package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SignupScreen(navController: NavController) {
    val hasBackButton = false
    val pageTitle = "Sign Up"

    BaseScreen(navController, hasBackButton, pageTitle) {
        Text("Click here to go to login page",
            modifier = Modifier.clickable { navController.navigate("login") }
        )
    }
}