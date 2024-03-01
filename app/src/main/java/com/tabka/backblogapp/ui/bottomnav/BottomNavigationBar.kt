//
//  BottomNavigationBar.kt
//  backblog
//
//  Created by Christian Totaro on 2/3/24.
//
package com.tabka.backblogapp.ui.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationBar(
    val route: String,
    val icon: ImageVector
) {
    object Home: BottomNavigationBar(
        route = "home_nav",
        icon = Icons.Default.Layers
    )
    object Search: BottomNavigationBar(
        route = "search_nav",
        icon = Icons.Default.Search
    )
    object Friends: BottomNavigationBar(
        route = "friends_nav",
        icon = Icons.Default.PeopleAlt
    )
}
