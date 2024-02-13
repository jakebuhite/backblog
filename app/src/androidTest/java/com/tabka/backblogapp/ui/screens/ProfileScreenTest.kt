package com.tabka.backblogapp.ui.screens

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import com.tabka.backblogapp.ui.viewmodels.ProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeProfileViewModel : ProfileViewModel() {
    // User Info
    override val userData: MutableLiveData<UserData> = MutableLiveData()
    override val publicLogData: MutableLiveData<List<LogData>> = MutableLiveData()

    private val _friendsData = MutableStateFlow<List<UserData>>(emptyList())
    override val friendsData = _friendsData.asStateFlow()

    override suspend fun getUserData(friendId: String) {
        val result = UserData(
            userId = null,
            username = null,
            joinDate = null,
            avatarPreset = 1,
            friends = emptyMap(),
            blocked = emptyMap()
        )
        userData.value = result
    }

    override suspend fun getPublicLogs(friendId: String) {
        publicLogData.value = emptyList()
    }

    override suspend fun getFriends(friendId: String) {
        _friendsData.value = emptyList()
    }
}

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private lateinit var fakeProfileViewModel: FakeProfileViewModel

    @Before
    fun setup() {
        // Launch the ProfileScreen composable
        fakeProfileViewModel = FakeProfileViewModel()
        composeTestRule.setContent {
            mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
            mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
            BottomNavGraph(navController = mockNavController)
            MaterialTheme {
                Surface {
                    ProfileScreen(navController = mockNavController, friendId = "friend123", fakeProfileViewModel)
                }
            }
        }
    }

    @Test
    fun testProfileScreenInView() {
        composeTestRule.onNodeWithTag("USER_FRIEND_ICON").assertExists()
    }
}