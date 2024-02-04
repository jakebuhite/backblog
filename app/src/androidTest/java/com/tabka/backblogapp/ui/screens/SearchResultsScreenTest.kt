package com.tabka.backblogapp.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SearchResultsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private lateinit var fakeLogViewModel: FakeLogViewModel

    @Before
    fun setUp() {
        mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
        mockNavController.navigatorProvider.addNavigator(ComposeNavigator())


    }


    @Test
    fun testInitialStateShowsSearchBar() {
        composeTestRule.setContent {
            fakeLogViewModel = FakeLogViewModel()
            BottomNavGraph(mockNavController)
            SearchResultsScreen(mockNavController, fakeLogViewModel)
        }
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT", useUnmergedTree = true)
            .assertIsDisplayed()
    }


    @Test
    fun testInputTextUpdatesSearchQuery() {
        composeTestRule.setContent {
            fakeLogViewModel = FakeLogViewModel()
            BottomNavGraph(mockNavController)
            SearchResultsScreen(mockNavController, fakeLogViewModel)
        }
        val query = "Inception"
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT", useUnmergedTree = true)
            .performTextInput(query)

        composeTestRule.onAllNodesWithText(query)[1].assertIsDisplayed()
    }

    @Test
    fun testSearchResultsDisplayedWhenQueryIsValid() {
        composeTestRule.setContent {
            fakeLogViewModel = FakeLogViewModel()
            BottomNavGraph(mockNavController)
            SearchResultsScreen(mockNavController, fakeLogViewModel)
        }
        val query = "M"
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT", useUnmergedTree = true)
            .performTextInput(query)
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithTag("MOVIE_RESULTS_LIST").fetchSemanticsNodes().size == 1
        }

        composeTestRule.onNodeWithTag("MOVIE_RESULTS_LIST").assertIsDisplayed()
    }

    @Test
    fun testNoResultsDisplayedWhenQueryReturnsEmpty() {
        composeTestRule.setContent {
            fakeLogViewModel = FakeLogViewModel()
            BottomNavGraph(mockNavController)
            SearchResultsScreen(mockNavController, fakeLogViewModel)
        }
        val query = "NonExistingMovie"
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").performTextInput(query)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("NO_RESULTS_ROW").assertIsDisplayed()
    }

    @Test
    fun testClickOnMovieResultTriggersNavigation() {
        composeTestRule.setContent {
            fakeLogViewModel = FakeLogViewModel()
            BottomNavGraph(mockNavController)
            SearchResultsScreen(mockNavController, fakeLogViewModel)
        }
        val query = "Top Gun: Maverick"
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").performTextInput(query)
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").performImeAction()
        //composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").assert(isNotFocused())

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithTag("MOVIE_RESULT").fetchSemanticsNodes().size > 1
        }
        /*composeTestRule.onNodeWithText(query).performClick()*/
        composeTestRule.onAllNodesWithTag("MOVIE_RESULT")[0].performClick()

        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("MOVIE_DETAILS_MOVIE").fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithTag("MOVIE_DETAILS_MOVIE").assertIsDisplayed()

        assertThat(mockNavController.currentDestination?.route).isEqualTo("search_movie_details_{movieId}")
        // Verify navigation occurred with expected argument
        // This step requires you to capture the navigation action and arguments in some manner
        //assertTrue(mockNavController.currentDestination?.route?.startsWith("search_movie_details") == true)
    }

    @Test
    fun testAddMovieToLogButtonAndAddToLogButton() {
        composeTestRule.setContent {
            fakeLogViewModel = FakeLogViewModel()
            BottomNavGraph(mockNavController)
            SearchResultsScreen(mockNavController, fakeLogViewModel)
        }
        val query = "Top Gun: Maverick"
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").performTextInput(query)
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").performImeAction()
        //composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").assert(isNotFocused())

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithTag("MOVIE_RESULT").fetchSemanticsNodes().size > 1
        }
        /*composeTestRule.onNodeWithText(query).performClick()*/
        composeTestRule.onAllNodesWithTag("ADD_MOVIE_TO_LOG_BUTTON")[0].performClick()

        // Verify the popup opens
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("ADD_MOVIE_TO_LOG_POPUP").assertIsDisplayed()

        composeTestRule.onNodeWithTag("ADD_TO_LOG_BUTTON").assertIsDisplayed()
    }

    @Test
    fun testAddMovieToLogButtonWithLogs() {

        val initialLogs = listOf(
            LogData(
                logId = "log1",
                name = "Log One",
                creationDate = null,
                lastModifiedDate = null,
                isVisible = null,
                owner = null, // You might want to at least provide an Owner with an id for consistency
                collaborators = emptyMap(),
                movieIds = emptyMap(),
                watchedIds = emptyMap()
            ),
            LogData(
                logId = "log2",
                name = "Log Two",
                creationDate = null,
                lastModifiedDate = null,
                isVisible = null,
                owner = null, // Same note as above regarding providing minimal Owner data
                collaborators = emptyMap(),
                movieIds = emptyMap(),
                watchedIds = emptyMap()
            )
        )

        composeTestRule.setContent {
            fakeLogViewModel = FakeLogViewModel(initialLogs)
            BottomNavGraph(mockNavController)
            SearchResultsScreen(mockNavController, fakeLogViewModel)
        }
        val query = "Top Gun: Maverick"
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").performTextInput(query)
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").performImeAction()

        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithTag("MOVIE_RESULT").fetchSemanticsNodes().size > 1
        }
        /*composeTestRule.onNodeWithText(query).performClick()*/
        composeTestRule.onAllNodesWithTag("ADD_MOVIE_TO_LOG_BUTTON")[0].performClick()

        // Verify the popup opens
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("ADD_MOVIE_TO_LOG_POPUP").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("LOG_CHECKBOX").fetchSemanticsNodes().size > 1
        composeTestRule.onAllNodesWithTag("LOG_CHECKBOX")[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("LOG_CHECKBOX")[1].assertIsToggleable()
        composeTestRule.onAllNodesWithTag("LOG_CHECKBOX")[1].assertIsOff()
        composeTestRule.onAllNodesWithTag("LOG_CHECKBOX")[1].performClick()
        composeTestRule.onAllNodesWithTag("LOG_CHECKBOX")[1].assertIsOn()
        composeTestRule.onNodeWithTag("ADD_TO_LOG_BUTTON").assertIsDisplayed()

    }

}

