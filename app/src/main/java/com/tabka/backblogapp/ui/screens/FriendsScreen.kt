package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun FriendsScreen(navController: NavController) {
    val hasBackButton = false
    val pageTitle = "Friends"

    BaseScreen(navController, hasBackButton, pageTitle) {
        Text("Click here to go nowhere",
            /*modifier = Modifier.clickable { navController.navigate("search_movie_details_128") }*/
        )
    }
}