package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R

@Composable
fun FriendsScreen(navController: NavController) {
    val hasBackButton = false
    val pageTitle = "Friends"

    BaseScreen(navController, hasBackButton, pageTitle) {
        Text("Click here to go nowhere",
            /*modifier = Modifier.clickable { navController.navigate("search_movie_details_128") }*/
        )
        Image(
            painter = painterResource(id = R.drawable.settings_icon),
            contentDescription = "Settings icon",
            modifier = Modifier
                .height(35.dp)
                .width(35.dp)
                .clickable { navController.navigate("settings") }
        )
    }
}