package com.tabka.backblogapp.ui.screens

import androidx.compose.material.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
class SearchResultsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController

    @Before
    fun setUp() {
        // Launch the SearchResultsScreen composable
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
            BottomNavGraph(navController = mockNavController)
            Surface {
                SearchResultsScreen(navController = mockNavController, backStackEntry = mockNavController.currentBackStackEntry!!)
            }
        }
    }

    @Test
    fun testInitialStateShowsSearchBar() {
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT", useUnmergedTree = true).assertIsDisplayed()
    }
/*

    @Test
    fun testInputTextUpdatesSearchQuery() {
        val query = "Inception"
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT", useUnmergedTree = true).performTextInput(query)

        composeTestRule.onNodeWithText(query).assertIsDisplayed()
    }

    @Test
    fun testSearchResultsDisplayedWhenQueryIsValid() {
        val query = "Matrix"
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT", useUnmergedTree = true).performTextInput(query)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("MOVIE_RESULTS_LIST").assertIsDisplayed()
    }

    @Test
    fun testNoResultsDisplayedWhenQueryReturnsEmpty() {
        val query = "NonExistingMovie"
        composeTestRule.onNodeWithTag("SEARCH_BAR_INPUT").performTextInput(query)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("NO_RESULTS_ROW").assertIsDisplayed()
    }

    @Test
    fun testClickOnMovieResultTriggersNavigation() {
        val query = "Good Movie"
        composeTestRule
            .onNodeWithText("Search for a movie")
            .performTextInput(query)

        // Assuming "Good Movie" is a mock result that can be interacted with
        composeTestRule
            .onNodeWithText("Good Movie") // Adjust based on how movie titles are displayed
            .performClick()

        // Verify navigation occurred with expected argument
        // This step requires you to capture the navigation action and arguments in some manner
        assertTrue(mockNavController.currentDestination?.route?.startsWith("movieDetail") == true)
    }
*/


}
