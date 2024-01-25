package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SearchScreen(navController: NavController) {
    val hasBackButton = false
    val pageTitle = "Search"

    BaseScreen(navController, hasBackButton, pageTitle) {
        /*Text("Click here to go to results page",
            modifier = Modifier.clickable { navController.navigate("search_results") }
        )*/
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(16.dp)
            .clickable {
                // Navigate to another page with the search query
                navController.navigate("search_results") }
        ) {
        }
    }
}