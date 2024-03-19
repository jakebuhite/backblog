//
//  BaseScreen.kt
//  backblog
//
//  Created by Christian Totaro on 2/1/24.
//
package com.tabka.backblogapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R

@Composable
fun BaseScreen(
    navController: NavController,
    isBackButtonVisible: Boolean,
    isMovieDetails: Boolean,
    title: String,
    content: @Composable (scrollState: ScrollState) -> Unit
) {

    if (!isMovieDetails) {
        val scrollState = rememberScrollState()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BackgroundGradient()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(76.dp))
                /*Spacer(modifier = Modifier.height(36.dp))
                BackButton(navController, isBackButtonVisible)
                Spacer(modifier = Modifier.height(20.dp))*/
                PageTitle(title)
                content(scrollState)
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

@Composable
fun BackButton(navController: NavController, visible: Boolean) {
    val icon = painterResource(R.drawable.backbutton)

    val alpha = if (visible) 1f else 0f

    // State to track if the button is enabled or not
    var isEnabled by remember { mutableStateOf(true) }

    var modifier = Modifier.size(48.dp)

    if (visible && isEnabled) {
        modifier = modifier.clickable {
            navController.popBackStack()
            isEnabled = false
        }
    }

    val isVisible = visible
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .alpha(alpha)
                    .testTag("BACK_BUTTON"),
                painter = icon,
                contentDescription = "Back Button",
            )
        }
    }
}

@Composable
fun PageTitle(title: String) {
    val isVisible = title.isNotEmpty()
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000))
    ) {
        Text(
            title, style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.testTag("PAGE_TITLE")
        )
    }
}

@Composable
fun BackgroundGradient() {
    // Define the gradient colors
    val lightGrey = Color(0xFF37414A)
    val darkGrey = Color(0xFF191919)
    val midColor = Color(0xFF292E33)

    val bottomMidColor = Color(0xFF232629)
    val topMidColor = Color(0xFF2F373E)

  /*  val gradientColors = listOf(lightGrey, topMidColor, otherColor, bottomMidColor, darkGrey)*/

    val gradientColors = listOf(lightGrey, darkGrey)

    // Create a vertical gradient brush
    Box(
        modifier = Modifier
            .background(Brush.verticalGradient(gradientColors))
            .testTag("GRADIENT")
    )
}
