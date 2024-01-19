package com.tabka.backblogapp.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationBar(
    val route: String,
    val icon: ImageVector
) {
    object Home: BottomNavigationBar(
        route = "home",
        icon = Icons.Default.Home
    )
    object Search: BottomNavigationBar(
        route = "search",
        icon = Icons.Default.Search
    )
    object Friends: BottomNavigationBar(
        route = "friends",
        icon = Icons.Default.Person
    )
}
