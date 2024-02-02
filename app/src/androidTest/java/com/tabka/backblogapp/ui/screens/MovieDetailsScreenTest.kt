package com.tabka.backblogapp.ui.screens
/*
import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
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
class MovieDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private var logId = "1cPRZ8LEUYndLj53pxSV"

    @Before
    fun setup() {
        // Launch the MovieDetailsScreen composable
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
            BottomNavGraph(navController = mockNavController)
            Surface {
                MovieDetailsScreen(navController = mockNavController, logId = logId)
            }
        }
    }

    @Test
    fun testMovieDetailsScreenInView() {
        // BaseScreen
        composeTestRule.onNodeWithContentDescription("Back Button").assertExists()
        composeTestRule.onNodeWithText("").assertExists()
    }

}*/