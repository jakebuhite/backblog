package com.tabka.backblogapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tabka.backblogapp.ui.screens.HomeScreen
import com.tabka.backblogapp.ui.theme.BackBlogAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BackBlogAppTheme {
                HomeScreen()
            }
        }
    }
}