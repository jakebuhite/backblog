package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BaseScreen(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BackgroundGradient()
        content()
    }
}

@Composable
fun BackgroundGradient() {
    // Define the gradient colors
    val lightGrey = Color(0xFF37414A)
    val darkGrey = Color(0xFF191919)

    val gradientColors = listOf(lightGrey, darkGrey)

    // Create a vertical gradient brush
    Box(modifier = Modifier.background(Brush.verticalGradient(gradientColors)))
}
