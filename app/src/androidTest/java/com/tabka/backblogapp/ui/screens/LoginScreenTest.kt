package com.tabka.backblogapp.ui.screens

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.tabka.backblogapp.R
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController

    @Before
    fun setup() {
        // Launch the LoginScreen composable
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
            BottomNavGraph(navController = mockNavController)
            Surface {
                LoginScreen(navController = mockNavController)
            }
        }
    }

    @Test
    fun testLoginScreenInView() {
        val imgId = R.drawable.img_logo_80_80.toString()
        composeTestRule.onNodeWithTag(imgId).assertIsDisplayed()
        composeTestRule.onNodeWithText("BackBlog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login to collaborate").assertIsDisplayed()

        // Email Field
        composeTestRule.onNodeWithTag("EMAIL_FIELD").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed().printToLog("TestingLog")

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertDoesNotExist()

        // Password Field
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()

        // Login Button
        composeTestRule.onNodeWithTag("LOGIN_BUTTON").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("LOG IN").assertIsDisplayed()

        // Go to Signup label/button
        composeTestRule.onNodeWithText("Don't have an account?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign up").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GO_TO_SIGNUP_BUTTON").assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun testLoginButtonClickFailedNoEmail() {
        // Enter NO email and password
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").performTextInput("apple123")

        // Click on the login button
        composeTestRule.onNodeWithTag("LOGIN_BUTTON").performClick()

        composeTestRule.waitUntil(4000) {
            composeTestRule.onAllNodesWithTag("STATUS_MESSAGE").fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertTextEquals("Please complete all fields")
    }

    @Test
    fun testLoginButtonClickFailedNoPassword() {
        // Enter NO email and password
        composeTestRule.onNodeWithTag("EMAIL_FIELD").performTextInput("apple@apple.com")

        // Click on the login button
        composeTestRule.onNodeWithTag("LOGIN_BUTTON").performClick()

        composeTestRule.waitUntil(4000) {
            composeTestRule.onAllNodesWithTag("STATUS_MESSAGE").fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertTextEquals("Please complete all fields")
    }

    @Test
    fun testLoginButtonClickFailedAuth() {
        // Enter NO email and password
        composeTestRule.onNodeWithTag("EMAIL_FIELD").performTextInput("banana@apple.com")
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").performTextInput("apple123")

        // Click on the login button
        composeTestRule.onNodeWithTag("LOGIN_BUTTON").performClick()

        composeTestRule.waitUntil(4000) {
            composeTestRule.onAllNodesWithTag("STATUS_MESSAGE").fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertTextEquals("Incorrect email or password.")
    }

    @Test
    fun testLoginButtonClickSuccess() {
        // Enter email and password
        composeTestRule.onNodeWithTag("EMAIL_FIELD").performTextInput("apple@apple.com")
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").performTextInput("apple123")

        // Click on the login button
        composeTestRule.onNodeWithTag("LOGIN_BUTTON").performClick()

        composeTestRule.waitUntil(4000) {
            composeTestRule.onAllNodesWithTag("STATUS_MESSAGE").fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertTextEquals("Login successful. Redirecting...")

        composeTestRule.waitUntil(3000) {
            mockNavController.currentDestination?.route == "friends"
            //assertThat(mockNavController.currentBackStackEntry?.destination?.route).isEqualTo(BottomNavigationBar.Friends.route)
        }

        // Assert user is logged in
        assert(Firebase.auth.currentUser != null)
        // Sign user out
        if (Firebase.auth.currentUser != null) {
            Firebase.auth.signOut()
        }

        assert(Firebase.auth.currentUser == null)
    }

}