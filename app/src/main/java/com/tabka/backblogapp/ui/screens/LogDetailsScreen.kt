package com.tabka.backblogapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel

private val TAG = "LogDetailsScreen"


@Composable
fun LogDetailsScreen(navController: NavController, logId: String?) {
    val logDetailsViewModel: LogDetailsViewModel = viewModel()
    val log = logDetailsViewModel.log!!

    val hasBackButton = true
    val pageTitle = log.name!!

    BaseScreen(navController, hasBackButton, pageTitle) {
    }
}