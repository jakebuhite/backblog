package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SearchResultsScreen(navController: NavController) {
    val hasBackButton = true
    val pageTitle = "Results"

    BaseScreen(navController, hasBackButton, pageTitle) {
        Text("Click here to go to movie details page",
            modifier = Modifier.clickable { navController.navigate("search_movie_details_128") }
        )
    }
}