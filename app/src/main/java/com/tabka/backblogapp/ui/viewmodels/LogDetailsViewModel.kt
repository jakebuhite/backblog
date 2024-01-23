package com.tabka.backblogapp.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.repository.LogLocalRepository

class LogDetailsViewModel(savedStateHandle: SavedStateHandle): ViewModel() {
    private val logId: String = checkNotNull(savedStateHandle["logId"])
    val log: LogData? = LogLocalRepository().getLogById(logId)
}