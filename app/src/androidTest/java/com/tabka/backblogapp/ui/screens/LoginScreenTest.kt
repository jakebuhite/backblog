package com.tabka.backblogapp.ui.screens

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavController
import androidx.test.platform.app.InstrumentationRegistry
import com.tabka.backblogapp.R
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreenInView() {
        // Mock NavController
        val mockNavController = object : NavController(InstrumentationRegistry.getInstrumentation().context) {}

        // Launch the LoginScreen composable
        composeTestRule.setContent {
            Surface {
                LoginScreen(navController = mockNavController)
            }
        }

        val imgId = R.drawable.img_logo_80_80.toString()
        composeTestRule.onNodeWithTag(imgId).assertIsDisplayed()
        composeTestRule.onNodeWithText("BackBlog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login to collaborate").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EMAIL_FIELD").assertIsDisplayed()
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").assertIsDisplayed()
        composeTestRule.onNodeWithText("STATUS_MESSAGE").assertDoesNotExist()
        composeTestRule.onNodeWithText("LOG IN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign up").assertIsDisplayed()
    }
}