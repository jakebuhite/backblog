package com.tabka.backblogapp.ui.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.tabka.backblogapp.R

sealed class BottomNavigationBar(
    val route: String,
    val icon: ImageVector
) {
    object Home: BottomNavigationBar(
        route = "home",
        icon = Icons.Default.Layers
    )
    object Search: BottomNavigationBar(
        route = "search",
        icon = Icons.Default.Search
    )
    object Friends: BottomNavigationBar(
        route = "friends",
        icon = Icons.Default.PeopleAlt
    )
}
