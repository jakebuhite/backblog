package com.tabka.backblogapp.ui.bottomnav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.tabka.backblogapp.ui.screens.FriendsScreen
import com.tabka.backblogapp.ui.screens.HomeScreen
import com.tabka.backblogapp.ui.screens.LogDetailsScreen
import com.tabka.backblogapp.ui.screens.LoginScreen
import com.tabka.backblogapp.ui.screens.MovieDetailsScreen
import com.tabka.backblogapp.ui.screens.SearchResultsScreen
import com.tabka.backblogapp.ui.screens.SearchScreen
import com.tabka.backblogapp.ui.screens.SignupScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationBar.Home.route
    ) {
        navigation(startDestination = "home", route = BottomNavigationBar.Home.route) {

            composable(route = "home") {
                HomeScreen(navController)
            }

            composable(
                route = "home_log_details_{logId}",
                arguments = listOf(navArgument("logId") { type = NavType.StringType })
            ) { backStackEntry ->
                LogDetailsScreen(navController, backStackEntry.arguments?.getString("logId"))
            }

            composable(
                route = "home_movie_details_{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) { backStackEntry ->
                MovieDetailsScreen(navController, backStackEntry.arguments?.getString("movieId"))
            }
        }

        navigation(startDestination = "search", route = BottomNavigationBar.Search.route) {

            composable(route = "search") {
                SearchScreen(navController)
            }

            composable(route = "search_results") {
                SearchResultsScreen(navController)
            }

            composable(
                route = "search_movie_details_{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) { backStackEntry ->
                MovieDetailsScreen(navController, backStackEntry.arguments?.getString("movieId"))
            }
        }

        navigation(startDestination = "login", route = BottomNavigationBar.Friends.route) {

            composable(route = "friends") {
                FriendsScreen(navController)
            }

            composable(route = "login") {
                LoginScreen(navController)
            }

            composable(route = "signup") {
                SignupScreen(navController)
            }
        }
    }
}
