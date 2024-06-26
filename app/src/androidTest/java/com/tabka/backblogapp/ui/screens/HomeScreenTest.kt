package com.tabka.backblogapp.ui.screens

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasRequestFocusAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.ui.screens.models.createFakeMovieData
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeLogViewModel(initialLogs: List<LogData>? = null) : LogViewModel() {
    private var _allLogs = MutableStateFlow(initialLogs ?: emptyList())
    override var allLogs: StateFlow<List<LogData>?> = _allLogs.asStateFlow()

    private var _movie = MutableStateFlow<Pair<MovieData?, String>>(null to "")
    override var movie: StateFlow<Pair<MovieData?, String>> = _movie.asStateFlow()

    override fun getMovieById(movieId: String) {
        val fakeMovieData = createFakeMovieData(id=531330, title="Fake Top Gun")
        _movie.value = (fakeMovieData to "")
    }

    fun addLogs(logs: List<LogData>) {
        _allLogs.value = logs
    }

    // Reset logs
    fun clearAll() {
        _allLogs.value = listOf()
        _movie.value = (null to "")
    }
}

class FakeFriendsViewModel(initialFriends: List<UserData>? = null) : FriendsViewModel() {
    private val _friendsData = MutableStateFlow(initialFriends ?: listOf(
        UserData(
            userId = "firstUser123",
            username = "John"
        ),
        UserData(
            userId = "secondUser456",
            username = "Jane"
        )
    ))
    override val friendsData = _friendsData.asStateFlow()
}

/*
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private lateinit var fakeLogViewModel: FakeLogViewModel
    private lateinit var fakeFriendsViewModel: FriendsViewModel

    @Before
    fun setUp() {
        // Set up NavController
        mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
        mockNavController.navigatorProvider.addNavigator(ComposeNavigator())

        // Set up view model
        fakeLogViewModel = FakeLogViewModel()
        fakeFriendsViewModel = FakeFriendsViewModel()
    }

    @Test
    fun testNoLogData() {
        // Launch compose
        composeTestRule.setContent {
            HomeScreen(mockNavController, fakeLogViewModel, FriendsViewModel())
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
        val initialLogs = listOf(LogData(
            logId = "log001",
            name = "Minimal Log",
            creationDate = "2024-02-01",
            lastModifiedDate = "2024-02-01",
            isVisible = true,
            owner = Owner(userId = "user001", priority = 1),
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf("531330"),
            watchedIds = mutableListOf()
        ))

        // Launch compose
        composeTestRule.setContent {
            fakeLogViewModel.addLogs(initialLogs)
            HomeScreen(mockNavController, fakeLogViewModel, FriendsViewModel())
        }

        composeTestRule.onNodeWithTag("PRIORITY_LOG_TITLE").assertExists()
        composeTestRule.onNodeWithTag("MOVIE_IMAGE", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("MOVIE_TITLE", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("MOVIE_YEAR", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("CHECK_ICON", useUnmergedTree = true).assertExists()
    }

    @Test
    fun testLogDataWithNullMovieId() {
        val initialLogs = listOf(
            LogData(
                logId = "log001",
                name = "Minimal Log",
                creationDate = "2024-02-01",
                lastModifiedDate = "2024-02-01",
                isVisible = true,
                owner = Owner(userId = "user001", priority = 1),
                collaborators = mutableListOf(),
                order = emptyMap(),
                movieIds = mutableListOf(),
                watchedIds = mutableListOf()
            )
        )

        composeTestRule.setContent {
            fakeLogViewModel.addLogs(initialLogs)
            HomeScreen(mockNavController, fakeLogViewModel, FriendsViewModel())
        }

        composeTestRule.onNodeWithTag("MOVIE_IMAGE", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testAddLogPopupExists() {
        composeTestRule.setContent {
            HomeScreen(mockNavController, fakeLogViewModel, FriendsViewModel())
        }

        // Verify My Logs Header
        composeTestRule.onNodeWithTag("MY_LOGS_HEADER").assertIsDisplayed()

        // Open Add Log Popup
        composeTestRule.onNodeWithTag("ADD_LOG_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP").assertIsDisplayed()

        // Ensure text field has focus
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assert(hasRequestFocusAction()).assert(hasText("Log Name"))
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP_LOG_NAME_LABEL", useUnmergedTree = true).assert(hasText("Log Name"))

        // Simulate user input log name

      /*  val inputText = "My First Log"
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performTextInput(inputText)
*/
        // Click enter on keyboard to clear focus
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performImeAction()
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assertIsNotFocused()

        // Verify that popup menu disappears on create log
/*        composeTestRule.onNodeWithTag("CREATE_LOG_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP").assertDoesNotExist()*/

    }

    @Test
    fun testAddLogWithName() {
        composeTestRule.setContent {
            HomeScreen(mockNavController, fakeLogViewModel, FriendsViewModel())
        }

        composeTestRule.onNodeWithTag("ADD_LOG_BUTTON").performClick()
        val inputText = "My First Log"
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performTextInput(inputText)
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assertTextContains(inputText)
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performImeAction()
        composeTestRule.waitForIdle()
        runBlocking {
            delay(2000)
        }
        composeTestRule.onNodeWithTag("CREATE_LOG_BUTTON").performClick()
        composeTestRule.waitForIdle()
        runBlocking {
            delay(5000)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP").assertDoesNotExist()
    }

    @Test
    fun testAddLogWithNoName() {
        composeTestRule.setContent {
            HomeScreen(mockNavController, fakeLogViewModel, FriendsViewModel())
        }
        composeTestRule.onNodeWithTag("ADD_LOG_BUTTON").performClick()
        val inputText = ""
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performTextInput(inputText)
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assertTextContains(inputText)
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").performImeAction()
        composeTestRule.onNodeWithTag("CREATE_LOG_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ADD_LOG_POPUP").assertIsDisplayed()
    }

    @Test
    fun testAddLogWithCollaborators() {
        composeTestRule.setContent {
            HomeScreen(mockNavController, fakeLogViewModel, fakeFriendsViewModel)
        }

        composeTestRule.onNodeWithTag("ADD_LOG_BUTTON").performClick()
        composeTestRule.onAllNodesWithTag("NEW_LOG_COLLABORATOR_AVATAR").fetchSemanticsNodes().isNotEmpty()
        composeTestRule.onAllNodesWithTag("NEW_LOG_COLLABORATOR_USERNAME").fetchSemanticsNodes().isNotEmpty()

        composeTestRule.onAllNodesWithTag("REMOVE_COLLABORATOR_ICON").fetchSemanticsNodes().isEmpty()
        composeTestRule.onAllNodesWithTag("REMOVE_COLLABORATOR_BUTTON").fetchSemanticsNodes().isEmpty()
        composeTestRule.onAllNodesWithTag("ADD_COLLABORATOR_BUTTON").fetchSemanticsNodes().isNotEmpty()
        composeTestRule.onAllNodesWithTag("ADD_COLLABORATOR_ICON").fetchSemanticsNodes().isNotEmpty()

        // Add friend as collaborator
        composeTestRule.onAllNodesWithTag("ADD_COLLABORATOR_BUTTON").onFirst().assertHasClickAction().performClick()
        composeTestRule.onAllNodesWithTag("REMOVE_COLLABORATOR_BUTTON").fetchSemanticsNodes().isNotEmpty()

        // Remove friend
        composeTestRule.onAllNodesWithTag("REMOVE_COLLABORATOR_BUTTON").onFirst().performClick()
        composeTestRule.onAllNodesWithTag("REMOVE_COLLABORATOR_BUTTON").fetchSemanticsNodes().isEmpty()
    }

    @After
    fun tearDown() {
        fakeLogViewModel.clearAll()
    }
}*/