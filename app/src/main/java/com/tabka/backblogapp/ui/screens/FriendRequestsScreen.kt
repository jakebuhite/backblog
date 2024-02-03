package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun FriendRequestsScreen() {
    Row() {
        Text("Public Logs", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp).testTag("PAGE_SUB_TITLE"))
    }
}