package com.tabka.backblogapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel

private val TAG = "LogDetailsScreen"
private val logViewModel: LogViewModel = LogViewModel()


@Composable
fun LogScreen(logId: String?) {
    val logDetailsViewModel: LogDetailsViewModel = viewModel()
    val log = logDetailsViewModel.log
}