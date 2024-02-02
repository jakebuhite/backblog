package com.tabka.backblogapp.ui.screens

import androidx.compose.material3.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
                HomeScreen(navController = mockNavController, backStackEntry = mockNavController.getBackStackEntry("home"))
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
/*
    @Test
    fun testHomeScreenWithLogData() {
        allLogs = listOf(LogData(
            logId = "log001",
            name = "Minimal Log",
            creationDate = "2024-02-01",
            lastModifiedDate = "2024-02-01",
            isVisible = true,
            owner = Owner(userId = "user001", priority = 1),
            collaborators = emptyMap(),
            movieIds = emptyMap(),
            watchedIds = emptyMap()
        ),
            LogData(
                logId = "log002",
                name = "Essential Fields Log",
                creationDate = "2024-02-02",
                lastModifiedDate = "2024-02-02",
                isVisible = false,
                owner = Owner(userId = "user002", priority = 2),
                collaborators = null,
                movieIds = null,
                watchedIds = null
            ),
            LogData(
                logId = "log003",
                name = "Visible Log",
                creationDate = "2024-02-03",
                lastModifiedDate = "2024-02-03",
                isVisible = true,
                owner = Owner(userId = "user003", priority = 3),
                collaborators = mapOf("user004" to mapOf("view" to 1)),
                movieIds = mapOf("movie001" to true),
                watchedIds = mapOf("movie001" to false)
            ),
            // Complex LogData Test Models
            LogData(
                logId = "log101",
                name = "Comprehensive Collaborators Log",
                creationDate = "2024-03-01",
                lastModifiedDate = "2024-03-02",
                isVisible = true,
                owner = Owner(userId = "user101", priority = 1),
                collaborators = mapOf(
                    "user102" to mapOf("edit" to 2, "view" to 1),
                    "user103" to mapOf("view" to 3)
                ),
                movieIds = mapOf("movie102" to true, "movie103" to false),
                watchedIds = mapOf("movie102" to true)
            ),
            LogData(
                logId = "log102",
                name = "Multiple Movies Log",
                creationDate = "2024-03-03",
                lastModifiedDate = "2024-03-04",
                isVisible = false,
                owner = Owner(userId = "user104", priority = 2),
                collaborators = mapOf("user105" to mapOf("edit" to 1)),
                movieIds = mapOf("movie104" to true, "movie105" to true, "movie106" to false),
                watchedIds = mapOf("movie104" to true, "movie106" to true)
            ),)

        // Launch compose
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
            BottomNavGraph(navController = mockNavController)
            Surface {
                HomeScreen(navController = mockNavController, backStackEntry = mockNavController.getBackStackEntry("home"))
            }
        }
    }*/
}