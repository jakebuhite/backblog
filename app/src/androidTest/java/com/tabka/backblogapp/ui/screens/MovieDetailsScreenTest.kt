package com.tabka.backblogapp.ui.screens

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private var logId = "1cPRZ8LEUYndLj53pxSV"

    @Before
    fun setup() {
        mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
        mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
    }

    @Test
    fun testMovieDetailsScreenInView() {
        composeTestRule.setContent {

            BottomNavGraph(mockNavController)
            Surface {
                MovieDetailsScreen(navController = mockNavController, movieId = "11", logId = logId, logViewModel = LogViewModel(), isFromLog = true)
            }
        }
        Thread.sleep(10000)
        composeTestRule.onNodeWithTag("IMAGE_BACKGROUND").assertIsDisplayed()
        composeTestRule.waitUntil(timeoutMillis = 100000) {
            // Attempt to find all nodes with the tag and check if exactly one is present
            composeTestRule.onAllNodesWithTag("IMAGE_BACKGROUND").fetchSemanticsNodes().size == 1
        }
    }

    @Test
    fun testMovieDetailsScreenInViewNoMovie() {
        composeTestRule.setContent {

            BottomNavGraph(mockNavController)
            Surface {
                MovieDetailsScreen(navController = mockNavController, movieId = "-1", logId = logId, logViewModel = LogViewModel(), isFromLog = true)
            }
        }
        composeTestRule.onNodeWithTag("IMAGE_BACKGROUND").assertIsNotDisplayed()

    }
}