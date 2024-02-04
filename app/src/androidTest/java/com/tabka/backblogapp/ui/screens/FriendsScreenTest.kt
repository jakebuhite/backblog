/*
package com.tabka.backblogapp.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private lateinit var viewModel: FriendsViewModel

*/
/*    @Before
    fun setup() {
        // Launch the FriendsScreen composable
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
            BottomNavGraph(navController = mockNavController)
            Surface {
                FriendsScreen(navController = mockNavController, viewModel)
            }
        }
    }*//*

*/
/*
    @Test
    fun testFriendsScreenInView() {
        composeTestRule.onNodeWithText("null").assertExists()

        // TEMPORARY
        composeTestRule.onNodeWithText("Click here to go nowhere").assertIsDisplayed()

        composeTestRule.onNodeWithTag("SETTINGS_ICON").assertIsDisplayed()
    }

    @Test
    fun testClickSettingsIconNavigateSettings() {
        composeTestRule.onNodeWithTag("SETTINGS_ICON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SETTINGS_ICON").assertHasClickAction()
        composeTestRule.onNodeWithTag("SETTINGS_ICON").performClick()

        assert(mockNavController.currentDestination?.route == "settings")
    }*//*

}
*/
