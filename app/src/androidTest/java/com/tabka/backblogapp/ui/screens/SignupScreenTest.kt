package com.tabka.backblogapp.ui.screens

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.R
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignupScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController

    @Before
    fun setUp() {
        // Launch the LoginScreen composable
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
            BottomNavGraph(navController = mockNavController)
            Surface {
                SignupScreen(navController = mockNavController)
            }
        }
    }

    @Test
    fun signupScreenInView() {
        val imgId = R.drawable.img_logo_80_80.toString()
        composeTestRule.onNodeWithTag(imgId).assertIsDisplayed()
        composeTestRule.onNodeWithText("BackBlog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign up to collaborate").assertIsDisplayed()

        // Email Field
        composeTestRule.onNodeWithTag("EMAIL_FIELD").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()

        // Username Field
        composeTestRule.onNodeWithTag("USERNAME_FIELD").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()

        // Password Field
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertDoesNotExist()

        // Login Button
        composeTestRule.onNodeWithTag("SIGNUP_BUTTON").assertIsDisplayed()
        composeTestRule.onNodeWithText("SIGN UP").assertIsDisplayed()

        // Go to Signup label/button
        composeTestRule.onNodeWithText("Already have an account?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log in").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GO_TO_LOGIN_BUTTON").assertIsDisplayed()
    }
}