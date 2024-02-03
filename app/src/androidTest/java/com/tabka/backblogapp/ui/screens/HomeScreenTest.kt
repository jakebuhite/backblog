package com.tabka.backblogapp.ui.screens

import androidx.compose.material3.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeLogViewModel(initialLogs: List<LogData>? = null) : LogViewModel() {
    private var _allLogs = MutableStateFlow(initialLogs)
    override var allLogs: StateFlow<List<LogData>?> = _allLogs
    /*override var allLogs: StateFlow<List<LogData>?> = MutableStateFlow(listOf(
        LogData(
            logId = "log001",
            name = "Minimal Log",
            creationDate = "2024-02-01",
            lastModifiedDate = "2024-02-01",
            isVisible = true,
            owner = Owner(userId = "user001", priority = 1),
            collaborators = emptyMap(),
            movieIds = mapOf("1" to true),
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
        ),
    ))*/
}

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private var allLogs: List<LogData>? = null

    @Before
    fun setUp() {
        mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
        mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
    }

    @Test
    fun testNoLogData() {

        // Launch compose
        composeTestRule.setContent {
            val fakeLogViewModel = FakeLogViewModel()
            BottomNavGraph(navController = mockNavController)
            HomeScreen(navController = mockNavController, backStackEntry = mockNavController.getBackStackEntry("home"), fakeLogViewModel)
        }

        composeTestRule.onNodeWithTag("PRIORITY_LOG_TITLE").assertDoesNotExist()
        composeTestRule.onNodeWithTag("MOVIE_IMAGE").assertDoesNotExist()
        composeTestRule.onNodeWithTag("MOVIE_TITLE").assertDoesNotExist()
        composeTestRule.onNodeWithTag("MOVIE_RATING").assertDoesNotExist()
        composeTestRule.onNodeWithTag("MOVIE_YEAR").assertDoesNotExist()
        composeTestRule.onNodeWithTag("CHECK_ICON").assertDoesNotExist()

    }

    @Test
    fun testLogDataAndFirstLogMovieId() {
        // Launch compose
        composeTestRule.setContent {
            // Create a fake ViewModel with the desired state
            val logs = listOf(LogData(
                logId = "log001",
                name = "Minimal Log",
                creationDate = "2024-02-01",
                lastModifiedDate = "2024-02-01",
                isVisible = true,
                owner = Owner(userId = "user001", priority = 1),
                collaborators = emptyMap(),
                movieIds = mapOf("1" to true),
                watchedIds = emptyMap()
            ))
            val fakeLogViewModel = FakeLogViewModel(logs)

            BottomNavGraph(navController = mockNavController)
            Surface {
                HomeScreen(mockNavController, backStackEntry = mockNavController.getBackStackEntry("home"), logViewModel = fakeLogViewModel)
            }
        }
        composeTestRule.onNodeWithTag("PRIORITY_LOG_TITLE").assertExists()
        composeTestRule.onNodeWithTag("MOVIE_IMAGE", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("MOVIE_TITLE", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("MOVIE_RATING", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("MOVIE_YEAR", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("CHECK_ICON", useUnmergedTree = true).assertExists()
    }

    @Test
    fun testLogDataWithNullMovieId() {
        composeTestRule.setContent {
            val logs = listOf(
                LogData(
                    logId = "log001",
                    name = "Minimal Log",
                    creationDate = "2024-02-01",
                    lastModifiedDate = "2024-02-01",
                    isVisible = true,
                    owner = Owner(userId = "user001", priority = 1),
                    collaborators = emptyMap(),
                    movieIds = emptyMap(),
                    watchedIds = emptyMap()
                )
            )
            val fakeLogViewModel = FakeLogViewModel(logs)

            BottomNavGraph(navController = mockNavController)
            Surface {
                HomeScreen(
                    mockNavController,
                    backStackEntry = mockNavController.getBackStackEntry("home"),
                    logViewModel = fakeLogViewModel
                )
            }
        }
        composeTestRule.onNodeWithTag("MOVIE_IMAGE", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testAddLogPopupExists() {
        composeTestRule.setContent {
            // Create a fake ViewModel with the desired state
            val fakeLogViewModel = FakeLogViewModel()

            BottomNavGraph(navController = mockNavController)
            /*mockNavController.getBackStackEntry("home")*/
            HomeScreen(mockNavController, backStackEntry = mockNavController.currentBackStackEntry!!, fakeLogViewModel)

        }

        // Verify My Logs Header
        composeTestRule.onNodeWithTag("MY_LOGS_HEADER").assertIsDisplayed()

        // Open Add Log Popup
        composeTestRule.onNodeWithTag("ADD_LOG_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP").assertIsDisplayed()

        // Ensure text field has focus
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assert(hasRequestFocusAction()).assert(
            hasText("Log Name"))
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP_LOG_NAME_LABEL", useUnmergedTree = true).assert(hasText("Log Name"))

        // Simulate user input log name
        /*val inputText = "My First Log"
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performTextInput(inputText)*/

        //composeTestRule.onNodeWithText("Log Name").performImeAction()
        //composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assertTextContains(inputText)

        // Click enter on keyboard to clear focus
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performImeAction()
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assertIsNotFocused()

/*        composeTestRule.onNodeWithTag("CREATE_LOG_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP").assertDoesNotExist()*/
    }

    @Test
    fun testAddLogWithName() {
        composeTestRule.setContent {
            // Create a fake ViewModel with the desired state
            val fakeLogViewModel = FakeLogViewModel()

            BottomNavGraph(navController = mockNavController)
            /*mockNavController.getBackStackEntry("home")*/
            HomeScreen(
                mockNavController,
                backStackEntry = mockNavController.currentBackStackEntry!!,
                fakeLogViewModel
            )

        }
        composeTestRule.onNodeWithTag("ADD_LOG_BUTTON").performClick()
        val inputText = "My First Log"
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performTextInput(inputText)
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assertTextContains(inputText)
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performImeAction()
        composeTestRule.onNodeWithTag("CREATE_LOG_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP").assertDoesNotExist()
    }

    @Test
    fun testAddLogWithNoName() {
        composeTestRule.setContent {
            // Create a fake ViewModel with the desired state
            val fakeLogViewModel = FakeLogViewModel()

            BottomNavGraph(navController = mockNavController)
            /*mockNavController.getBackStackEntry("home")*/
            HomeScreen(
                mockNavController,
                backStackEntry = mockNavController.currentBackStackEntry!!,
                fakeLogViewModel
            )

        }
        composeTestRule.onNodeWithTag("ADD_LOG_BUTTON").performClick()
        val inputText = ""
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performTextInput(inputText)
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assertTextContains(inputText)
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performImeAction()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("CREATE_LOG_BUTTON").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP").assertIsDisplayed()
    }
}