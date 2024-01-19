package com.tabka.backblogapp.ui.bottomnav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tabka.backblogapp.ui.screens.HomeScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationBar.Home.route
    ) {
        composable(route = BottomNavigationBar.Home.route) {
            HomeScreen()
        }
        composable(route = BottomNavigationBar.Search.route) {
            //SearchScreen()
        }
        composable(route = BottomNavigationBar.Friends.route) {
            //FriendsScreen()
        }
    }
}
