package com.tabka.backblogapp.ui.bottomnav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tabka.backblogapp.BuildConfig
import com.tabka.backblogapp.ui.screens.FriendsScreen
import com.tabka.backblogapp.ui.screens.HomeScreen
import com.tabka.backblogapp.ui.screens.LogDetailsScreen
import com.tabka.backblogapp.ui.screens.LoginScreen
import com.tabka.backblogapp.ui.screens.MovieDetailsScreen
import com.tabka.backblogapp.ui.screens.SearchResultsScreen
import com.tabka.backblogapp.ui.screens.SearchScreen
import com.tabka.backblogapp.ui.screens.SettingsScreen
import com.tabka.backblogapp.ui.screens.SignupScreen
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(navController: NavHostController) {
    val friendsViewModel = FriendsViewModel()
    val logDetailsViewModel = LogDetailsViewModel()

    var startDest by remember { mutableStateOf("login") }
    val auth = Firebase.auth
    auth.addAuthStateListener {
        startDest = if (auth.currentUser == null) {
            "login"
        } else {
            "friends"
        }
    }

    NavHost(
        navController = navController,
        startDestination = BottomNavigationBar.Home.route
    ) {

        navigation(startDestination = "home", route = BottomNavigationBar.Home.route) {

            composable(route = "home") { backStackEntry ->
                /*val logViewModel = backStackEntry.logViewModel<LogViewModel>(navController)
                val allLogs by logViewModel.allLogs.collectAsState()*/
                // If not testing
                if (!BuildConfig.IS_TESTING) {
                    HomeScreen(navController, backStackEntry)
                }
            }

            composable(
                route = "home_log_details_{logId}",
                arguments = listOf(navArgument("logId") { type = NavType.StringType })
            ) { backStackEntry ->
                LogDetailsScreen(navController, logDetailsViewModel, backStackEntry.arguments?.getString("logId"))
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
                if (!BuildConfig.IS_TESTING) {
                    SearchScreen(navController)
                }
            }

            composable(route = "search_results") { backStackEntry ->
                /*val logViewModel = entry.logViewModel<LogViewModel>(navController)
                val allLogs by logViewModel.allLogs.collectAsState()*/
                if (!BuildConfig.IS_TESTING) {
                    SearchResultsScreen(navController, backStackEntry)
                }
            }

            composable(
                route = "search_movie_details_{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) { backStackEntry ->
                MovieDetailsScreen(navController, backStackEntry.arguments?.getString("movieId"))
            }
        }

        navigation(startDestination = startDest, route = BottomNavigationBar.Friends.route) {

            composable(route = "friends") {
                FriendsScreen(navController, friendsViewModel)
            }

            composable(route = "login") {
                LoginScreen(navController)
            }

            composable(route = "signup") {
                SignupScreen(navController)
            }

            composable(route = "settings") {
                SettingsScreen(navController)
            }

            composable(
                route = "public_log_details_{logId}",
                arguments = listOf(navArgument("logId") { type = NavType.StringType })
            ) { backStackEntry ->
                LogDetailsScreen(navController, logDetailsViewModel, backStackEntry.arguments?.getString("logId"))
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.logViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}

