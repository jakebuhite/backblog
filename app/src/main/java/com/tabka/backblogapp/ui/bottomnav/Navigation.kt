package com.tabka.backblogapp.ui.bottomnav

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tabka.backblogapp.R

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.background(color = colorResource(R.color.bottomnav)),
        bottomBar = { BottomBar(navController = navController) }
    ) {
        BottomNavGraph(navController = navController)
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavigationBar.Home,
        BottomNavigationBar.Search,
        BottomNavigationBar.Friends,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation(
        /*modifier = Modifier.background(color = colorResource(R.color.bottomnav))*/
        backgroundColor = colorResource(id = R.color.bottomnav),
        contentColor = colorResource(id = R.color.bottomnav_icon_selected)
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavigationBar,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    BottomNavigationItem(
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon",
                modifier = Modifier.size(26.dp)
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        /*unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),*/
        unselectedContentColor = colorResource(id = R.color.bottomnav_icon),
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true

            }
        },
    )
}