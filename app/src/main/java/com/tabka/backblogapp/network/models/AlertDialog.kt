package com.tabka.backblogapp.network.models

import androidx.compose.ui.graphics.Color

data class AlertDialog(
    var isVisible: Boolean = false,
    var header: String? = null,
    var message: String? = null,
    var dismiss: Dismiss? = null,
    var accept: Accept? = null
)

data class Dismiss(var text: String, var textColor: Color? = null)
data class Accept(var text: String, var textColor: Color? = null, var action: () -> Unit)
