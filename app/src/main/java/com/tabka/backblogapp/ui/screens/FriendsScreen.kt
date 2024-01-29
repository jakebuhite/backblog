package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel

private val friendsViewModel: FriendsViewModel = FriendsViewModel()

@Composable
fun FriendsScreen(navController: NavController) {
    val hasBackButton = false
    val username = friendsViewModel.username.collectAsState().value
    val pageTitle = if(username.isNullOrEmpty()) "null" else username

    BaseScreen(navController, hasBackButton, pageTitle) {
        Text("Click here to go nowhere",
            /*modifier = Modifier.clickable { navController.navigate("search_movie_details_128") }*/
        )
    }
}