package com.tabka.backblogapp.ui.screens

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendRequestsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        // Launch the ProfileScreen composable
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    FriendRequestsScreen()
                }
            }
        }
    }

    @Test
    fun testFriendRequestsScreenInView() {
        composeTestRule.onNodeWithText("This is the requests screen").assertIsDisplayed()
    }
}