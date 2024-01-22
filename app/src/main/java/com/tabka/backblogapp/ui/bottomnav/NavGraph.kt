package com.tabka.backblogapp.ui.bottomnav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tabka.backblogapp.ui.screens.HomeScreen

@RequiresApi(Build.VERSION_CODES.O)
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
