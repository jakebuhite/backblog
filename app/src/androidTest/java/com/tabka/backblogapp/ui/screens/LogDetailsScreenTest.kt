package com.tabka.backblogapp.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private val logId = "1cPRZ8LEUYndLj53pxSV"
    private val logName = "Log"

    @Before
    fun setup() {
        // Launch the LogDetailsScreen composable
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
            BottomNavGraph(navController = mockNavController)
            Surface {
                DetailBar()
                Spacer(modifier = Modifier.height(20.dp))
                LogButtons()
                Spacer(modifier = Modifier.height(20.dp))
                LogList()
            }
        }
    }

    @Test
    fun testLogDetailsScreenInView() {
        composeTestRule.onNodeWithTag("CREATOR_PICTURE").assertExists()
        composeTestRule.onNodeWithTag("COLLABS_PICTURE").assertExists()
        composeTestRule.onNodeWithText("7 Movies").assertExists()
        composeTestRule.onNodeWithTag("ADD_ICON").assertExists()
        composeTestRule.onNodeWithTag("EDIT_ICON").assertExists()
        composeTestRule.onNodeWithTag("SHUFFLE_ICON").assertExists()
        composeTestRule.onNodeWithTag("ADD_MOVIE_ICON").assertExists()
    }
}