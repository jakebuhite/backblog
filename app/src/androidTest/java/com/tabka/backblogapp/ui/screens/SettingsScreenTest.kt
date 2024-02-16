package com.tabka.backblogapp.ui.screens

import androidx.compose.material.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import com.tabka.backblogapp.ui.viewmodels.SettingsViewModel
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.NetworkError
import com.tabka.backblogapp.util.NetworkExceptionType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


class FakeSettingsViewModel(
    private val userData: UserData? = null,
    private val success: Boolean = true,
    private val successSync: Boolean = true,
    private val successUpdate: Boolean = true,
    private val logsCount: Int = 1,
) : SettingsViewModel() {

    override suspend fun getUserData(): DataResult<UserData?> {
        return if (success) {
            DataResult.Success(userData)
        } else {
            DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
        }
    }

    override suspend fun syncLocalLogsToDB(): DataResult<Boolean> {
        return if (successSync) {
            DataResult.Success(true)
        } else {
            DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
        }
    }

    override suspend fun updateUserData(updates: Map<String, Any?>, password: String): DataResult<Boolean> {
        return if (successUpdate) {
            DataResult.Success(true)
        } else {
            DataResult.Failure(NetworkError(NetworkExceptionType.REQUEST_FAILED))
        }
    }

    override fun getLogCount(): Int {
        return logsCount
    }
}

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private lateinit var fakeSettingsViewModel: FakeSettingsViewModel

    @Before
    fun setUp() {
        mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
        mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
    }

    @Test
    fun testSettingsScreenInViewWithNullUser() {
        fakeSettingsViewModel = FakeSettingsViewModel()
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }
        composeTestRule.onNodeWithTag("LOADING_SPINNER").assertIsDisplayed()
    }

    @Test
    fun testSettingsScreenFailure() {
        fakeSettingsViewModel = FakeSettingsViewModel(null, false)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }

        composeTestRule.onNodeWithTag("LOADING_SPINNER").assertIsDisplayed()
    }

    @Test
    fun testSettingsScreenInViewWithUser() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }

        composeTestRule.onNodeWithTag("LOADING_SPINNER_PREVIEW").assertIsDisplayed()

        composeTestRule.onNodeWithTag("CHANGE_AVATAR_BUTTON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("USERNAME_TEXT_INPUT").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("PASSWORD_FIELD")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("PASSWORD_FIELD")[1].assertIsDisplayed()
        composeTestRule.onNodeWithTag("UPDATE_SETTINGS_BUTTON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SYNC_LOGS_BUTTON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SYNC_LOGS_TEXT", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testSettingsScreenChangeAvatar() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }

        composeTestRule.onNodeWithTag("CHANGE_AVATAR_BUTTON").performClick()
        composeTestRule.onNodeWithTag("AVATAR_SELECTION_DIALOG").assertIsDisplayed()
    }

    @Test
    fun testSettingsScreenNoLocalLogs() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData,
            success = true,
            successSync = false,
            logsCount = 0
        )
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }
        composeTestRule.onNodeWithTag("SYNC_LOGS_BUTTON").assertDoesNotExist()
        composeTestRule.onNodeWithTag("SYNC_LOGS_TEXT").assertDoesNotExist()
    }

    @Test
    fun testSettingsScreenSyncLocalLogsSuccess() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }

        composeTestRule.onNodeWithTag("SYNC_LOGS_BUTTON").performClick()
        composeTestRule.onNodeWithTag("SYNC_STATUS_MESSAGE").assertIsDisplayed()

    }

    @Test
    fun testSettingsScreenSyncLocalLogsError() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData, success = true, successSync = false)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }

        composeTestRule.onNodeWithTag("SYNC_LOGS_BUTTON").performClick()
        composeTestRule.onNodeWithTag("SYNC_STATUS_MESSAGE").assertIsDisplayed()

    }

    @Test
    fun testSettingsScreenUpdateSettingsNoPassword() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }

        composeTestRule.onNodeWithTag("UPDATE_SETTINGS_BUTTON").performClick()
    }

    @Test
    fun testSettingsScreenPasswordNoChanges() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }

        val password = "pass123"
        composeTestRule.onAllNodesWithTag("PASSWORD_FIELD")[0].performTextInput(password)
        composeTestRule.onNodeWithTag("UPDATE_SETTINGS_BUTTON").performClick()
    }

    @Test
    fun testSettingsScreenUpdateSettingsUsernameChange() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }
        val newUsername = "changing"
        val password = "pass123"

        composeTestRule.onNodeWithTag("USERNAME_TEXT_INPUT").performTextInput(newUsername)
        composeTestRule.onAllNodesWithTag("PASSWORD_FIELD")[0].performTextInput(password)
        composeTestRule.onNodeWithTag("UPDATE_SETTINGS_BUTTON").performClick()
    }

    @Test
    fun testSettingsScreenUpdateSettingsFailed() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData, successUpdate = false)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }
        val password = "pass123"

        composeTestRule.onAllNodesWithTag("PASSWORD_FIELD")[0].performTextInput(password)
        composeTestRule.onAllNodesWithTag("PASSWORD_FIELD")[1].performTextInput(password)
        composeTestRule.onNodeWithTag("UPDATE_SETTINGS_BUTTON").performClick()

        composeTestRule.onNodeWithTag("SYNC_STATUS_MESSAGE").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SYNC_STATUS_MESSAGE").assertTextEquals("Error updating user settings")

    }

    @Test
    fun testSettingsScreenUpdateSettingsSuccessful() {
        val userData = UserData(
            userId = "12345",
            username = "john_doe",
            joinDate = "2024-02-04",
            avatarPreset = 5,
            friends = mapOf("67890" to true, "11223" to false),
            blocked = mapOf("44556" to true)
        )
        fakeSettingsViewModel = FakeSettingsViewModel(userData, success = true)
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                SettingsScreen(mockNavController, fakeSettingsViewModel)
            }
        }
        val newUsername = "changing"
        val password = "pass123"

        composeTestRule.onNodeWithTag("USERNAME_TEXT_INPUT").performTextInput(newUsername)
        composeTestRule.onAllNodesWithTag("PASSWORD_FIELD")[0].performTextInput(password)
        composeTestRule.onNodeWithTag("UPDATE_SETTINGS_BUTTON").performClick()

        composeTestRule.onNodeWithTag("SYNC_STATUS_MESSAGE").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SYNC_STATUS_MESSAGE")
            .assertTextEquals("Settings successfully updated!")

    }
}
