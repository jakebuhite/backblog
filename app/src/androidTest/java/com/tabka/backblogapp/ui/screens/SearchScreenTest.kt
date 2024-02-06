package com.tabka.backblogapp.ui.screens

import androidx.compose.material3.Surface
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
class SearchScreenTest {
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
                SearchScreen(navController = mockNavController)
            }
        }
    }

    @Test
    fun testSearchScreenInView() {
        composeTestRule.onNodeWithTag("SEARCH_BAR_LAYOUT").assertIsDisplayed()
        composeTestRule.onNodeWithTag("BROWSE_CATEGORIES_TITLE").assertIsDisplayed()
        composeTestRule.onNodeWithTag("FRIENDS_RECENTLY_ADDED_TITLE").assertIsDisplayed()
    }

/*    @Test
    fun testSearchScreenNavigateToSearchResultsScreenOnSearchBarPress() {
        composeTestRule.onNodeWithTag("SEARCH_BAR_LAYOUT").performClick()
        composeTestRule.onAllNodesWithTag("PAGE_TITLE")[0].assertTextContains("Results")
    }*/
}
