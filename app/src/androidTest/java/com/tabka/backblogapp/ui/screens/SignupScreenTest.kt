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
    private val email = "test@test.com"
    private val username = "test"
    private val password = "test123"

    @Before
    fun setup() {
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
    fun testSignupScreenInView() {
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

        // Assert click-ability
        composeTestRule.onNodeWithTag("GO_TO_LOGIN_BUTTON").assertHasClickAction()
    }

    @Test
    fun testSignupButtonClickFailedNoEmail() {
        // Enter NO email and password
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").performTextInput(password)
        composeTestRule.onNodeWithTag("USERNAME_FIELD").performTextInput(username)

        // Click on the login button
        composeTestRule.onNodeWithTag("SIGNUP_BUTTON").performClick()

        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("STATUS_MESSAGE").fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertTextEquals("Please complete all fields")
    }

    @Test
    fun testSignupButtonClickFailedNoPassword() {
        // Enter NO email and password
        composeTestRule.onNodeWithTag("EMAIL_FIELD").performTextInput(email)
        composeTestRule.onNodeWithTag("USERNAME_FIELD").performTextInput(username)

        // Click on the login button
        composeTestRule.onNodeWithTag("SIGNUP_BUTTON").performClick()

        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("STATUS_MESSAGE").fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertTextEquals("Please complete all fields")
    }

    @Test
    fun testSignupButtonClickFailedNoUsername() {
        // Enter NO email and password
        composeTestRule.onNodeWithTag("EMAIL_FIELD").performTextInput(email)
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").performTextInput(password)

        // Click on the login button
        composeTestRule.onNodeWithTag("SIGNUP_BUTTON").performClick()

        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("STATUS_MESSAGE").fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertTextEquals("Please complete all fields")
    }

    @Test
    fun testSignupButtonClickAccountAlreadyExistsAuth() {
        // Enter NO email and password
        composeTestRule.onNodeWithTag("EMAIL_FIELD").performTextInput("apple@apple.com")
        composeTestRule.onNodeWithTag("USERNAME_FIELD").performTextInput("apple")
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").performTextInput("apple123")

        // Click on the login button
        composeTestRule.onNodeWithTag("SIGNUP_BUTTON").performClick()

        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("STATUS_MESSAGE").fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertTextEquals("Email already in use.")
    }

    /*@Test
    fun testSignupUserClickSuccess() = runBlocking {
        // Enter email and password
        composeTestRule.onNodeWithTag("EMAIL_FIELD").performTextInput(email)
        composeTestRule.onNodeWithTag("USERNAME_FIELD").performTextInput(username)
        composeTestRule.onNodeWithTag("PASSWORD_FIELD").performTextInput(password)

        // Perform signup actions
        composeTestRule.onNodeWithTag("SIGNUP_BUTTON").performClick()

        // Wait for the signup process to complete
        composeTestRule.waitUntil(4000) {
            composeTestRule.onAllNodesWithTag("STATUS_MESSAGE").fetchSemanticsNodes().size == 1
        }

        // Check to see status message
        composeTestRule.onNodeWithTag("STATUS_MESSAGE").assertTextEquals("Signup successful. Redirecting...")

        // Check to see if view changed to login
        composeTestRule.waitUntil(1500) {
            mockNavController.currentDestination?.route == "login"
        }

        // Check if the user is signed up
       assert(Firebase.auth.currentUser != null)

        // Perform actions to delete the user
        if (Firebase.auth.currentUser != null) {
            Firebase.auth.currentUser!!.delete().await()
        }

        // Assert user is now deleted
        assert(Firebase.auth.currentUser == null)
    }*/
}