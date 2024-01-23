package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    val hasBackButton = false
    val pageTitle = "Log In"

    BaseScreen(navController, hasBackButton, pageTitle) {
        Text("Click here to go to sign up  page",
            modifier = Modifier.clickable { navController.navigate("signup") }
        )
        Text("Click here to login and go to friends page",
            modifier = Modifier.clickable { navController.navigate("friends") }
        )
    }
}