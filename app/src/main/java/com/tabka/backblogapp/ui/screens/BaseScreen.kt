package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tabka.backblogapp.R
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavController

@Composable
fun BaseScreen(navController: NavController, isBackButtonVisible: Boolean, title: String, content: @Composable (scrollState: ScrollState) -> Unit) {

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BackgroundGradient()

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            backButton(navController, isBackButtonVisible)
            Spacer(modifier = Modifier.height(40.dp))
            pageTitle(title)
            content(scrollState)
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
    
}

@Composable
fun backButton(navController: NavController, visible: Boolean) {
    val icon = painterResource(R.drawable.button_back_arrow)

    var alpha = 0f
    if (visible) {
        alpha = 1f
    }

    Image(
        painter = icon,
        contentDescription = "Back Button",
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .alpha(alpha)
            .clickable { navController.popBackStack() }
    )
}

@Composable
fun pageTitle(title: String) {
    Text(title, style = MaterialTheme.typography.headlineLarge)
}

@Composable
fun PageTitle(title: String) {
    Text(title, style = MaterialTheme.typography.headlineLarge)
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
