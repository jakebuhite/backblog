//
//  NavGraph.kt
//  backblog
//
//  Created by Christian Totaro on 2/3/24.
//
package com.tabka.backblogapp.ui.bottomnav

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.tabka.backblogapp.ui.screens.BlockedUsersScreen
import com.tabka.backblogapp.ui.screens.CategoryResultsScreen
import com.tabka.backblogapp.ui.screens.FriendsScreen
import com.tabka.backblogapp.ui.screens.HomeScreen
import com.tabka.backblogapp.ui.screens.LogDetailsScreen
import com.tabka.backblogapp.ui.screens.LoginScreen
import com.tabka.backblogapp.ui.screens.MovieDetailsScreen
import com.tabka.backblogapp.ui.screens.ProfileScreen
import com.tabka.backblogapp.ui.screens.SearchResultsScreen
import com.tabka.backblogapp.ui.screens.SearchScreen
import com.tabka.backblogapp.ui.screens.SettingsScreen
import com.tabka.backblogapp.ui.screens.SignupScreen
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.ui.viewmodels.MovieDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.ProfileViewModel
import com.tabka.backblogapp.ui.viewmodels.SettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(navController: NavHostController) {
    val friendsViewModel = FriendsViewModel()
    val movieDetailsViewModel = MovieDetailsViewModel()
    val logDetailsViewModel = LogDetailsViewModel()

    LaunchedEffect(true) {
        friendsViewModel.getFriends()
    }
    //val logDetailsViewModel = LogDetailsViewModel()
    val logViewModel = LogViewModel()
    val settingsViewModel = SettingsViewModel()
    val profileViewModel = ProfileViewModel()

    Log.d("Something", "PLEASE LOG SOMETHING")
    val auth = Firebase.auth
    //auth.signOut()

    NavHost(
        navController = navController,
        startDestination = BottomNavigationBar.Home.route
    ) {
        navigation(startDestination = "home", route = BottomNavigationBar.Home.route) {

            composable(route = "home") {
                HomeScreen(navController, logViewModel, friendsViewModel)
            }

            composable(
                route = "home_log_details_{logId}",
                arguments = listOf(navArgument("logId") { type = NavType.StringType })
            ) { backStackEntry ->
                LogDetailsScreen(navController, backStackEntry.arguments?.getString("logId"), friendsViewModel, logViewModel, logDetailsViewModel, movieDetailsViewModel)
            }

            composable(
                route = "home_movie_details_{movieId}_{logId}_{movieIsWatched}",
                arguments = listOf(navArgument("movieId") { type = NavType.StringType }, navArgument("logId") { type = NavType.StringType }, navArgument("movieIsWatched") { type = NavType.IntType})
            ) { backStackEntry ->
                backStackEntry.arguments?.let { MovieDetailsScreen(navController, backStackEntry.arguments?.getString("movieId"), backStackEntry.arguments?.getString("logId"), logViewModel, it.getInt("movieIsWatched"), movieDetailsViewModel, friendsViewModel) }
            }
        }

        navigation(startDestination = "search", route = BottomNavigationBar.Search.route) {

            composable(route = "search") {
                SearchScreen(navController)
            }

            composable(route = "search_results") {
                SearchResultsScreen(navController, logViewModel, friendsViewModel, movieDetailsViewModel)
            }

            composable(
                route = "category_results_{genreId}_{name}",
                arguments = listOf(
                    navArgument("genreId") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType })
            ) { backStackEntry ->
                CategoryResultsScreen(navController, logViewModel, backStackEntry.arguments?.getString("genreId"), backStackEntry.arguments?.getString("name"), friendsViewModel, movieDetailsViewModel)
            }

            composable(
                route = "search_movie_details_{movieId}_{movieIsWatched}",
                arguments = listOf(navArgument("movieId") { type = NavType.StringType }, navArgument("movieIsWatched") { type = NavType.IntType })
            ) { backStackEntry ->
                backStackEntry.arguments?.let {
                    MovieDetailsScreen(navController, backStackEntry.arguments?.getString("movieId"), null, logViewModel, it.getInt("movieIsWatched"), movieDetailsViewModel, friendsViewModel
                    )
                }
            }
        }

        navigation(startDestination = if (auth.currentUser == null) "login" else "friends", route = BottomNavigationBar.Friends.route) {
            composable(route = "friends") {
                FriendsScreen(navController, friendsViewModel, logViewModel)
            }

            composable(route = "login") {
                LoginScreen(navController)
            }

            composable(route = "signup") {
                SignupScreen(navController)
            }

            composable(route = "settings") {
                SettingsScreen(navController, settingsViewModel)
            }

            composable(route = "blocked_users") {
                BlockedUsersScreen(navController)
            }

            composable(
                route = "public_log_details_{logId}",
                arguments = listOf(navArgument("logId") { type = NavType.StringType })
            ) { backStackEntry ->
                LogDetailsScreen(navController, backStackEntry.arguments?.getString("logId"), friendsViewModel, logViewModel, logDetailsViewModel, movieDetailsViewModel)
            }

            composable(
                route = "friends_page_{friendId}",
                arguments = listOf(navArgument("friendId") { type = NavType.StringType })
            ) { backStackEntry ->
                ProfileScreen(navController, backStackEntry.arguments?.getString("friendId"), profileViewModel, logViewModel)
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

