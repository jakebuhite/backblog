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
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private var allLogs: List<LogData>? = null

    @Test
    fun testHomeScreenNoLogData() {
        allLogs = null

        // Launch compose
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
            BottomNavGraph(navController = mockNavController)
            Surface {
                // If logs exist
                if (!allLogs.isNullOrEmpty()) {
                    WatchNextCard(mockNavController, allLogs!![0])
                }

                Spacer(Modifier.height(40.dp))
            }
        }

        composeTestRule.onNodeWithTag("PRIORITY_LOG_TITLE").assertDoesNotExist()
        composeTestRule.onNodeWithTag("MOVIE_IMAGE").assertDoesNotExist()
        composeTestRule.onNodeWithTag("MOVIE_TITLE").assertDoesNotExist()
        composeTestRule.onNodeWithTag("MOVIE_RATING").assertDoesNotExist()
        composeTestRule.onNodeWithTag("MOVIE_YEAR").assertDoesNotExist()
        composeTestRule.onNodeWithTag("CHECK_ICON").assertDoesNotExist()

        // My Logs Section
    }

    //fun testHomeScreenWithLogs() {
    //
    //}
}