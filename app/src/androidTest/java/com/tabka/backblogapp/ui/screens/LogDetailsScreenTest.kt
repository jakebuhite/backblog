/*
package com.tabka.backblogapp.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import com.tabka.backblogapp.ui.screens.models.createFakeMovieData
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeLogDetailsViewModel(initialLogs: List<LogData>? = null) : LogViewModel() {

}

@RunWith(AndroidJUnit4::class)
class LogDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private val logId = "1cPRZ8LEUYndLj53pxSV"
    private lateinit var fakeLogViewModel: FakeLogViewModel

    @Before
    fun setup() {
        // Launch the LogDetailsScreen composable
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())

            fakeLogViewModel = FakeLogViewModel()
            BottomNavGraph(navController = mockNavController)
            */
/*Surface {
                LogDetailsScreen(navController = mockNavController, logId = logId)
                LogDetailsScreen(navController = mockNavController, logId)
                DetailBar()
                Spacer(modifier = Modifier.height(20.dp))
                LogButtons()
                Spacer(modifier = Modifier.height(20.dp))
                LogList(navController = mockNavController)
            }*//*

        }
    }

    @Test
    fun testLogDetailsScreenInView() {
        composeTestRule.setContent {
            LogDetailsScreen(mockNavController, fakeLogViewModel, logId)
        }
    }

}
*/
