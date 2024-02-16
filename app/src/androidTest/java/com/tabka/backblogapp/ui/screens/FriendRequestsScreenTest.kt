
package com.tabka.backblogapp.ui.screens

import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogRequestData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendRequestsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController

    @Before
    fun setup() {
        mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
        mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
    }

    @Test
    fun testFriendRequestsScreenInView() {
        val friendRequests = listOf(
            Pair(FriendRequestData(
                requestId = "123",
                senderId = "joe123",
                targetId = "jane456",
                requestDate = "3943894893",
                isComplete = false
            ), UserData(
                userId = null,
                username = null,
                joinDate = null,
                avatarPreset = 1,
                friends = emptyMap(),
                blocked = emptyMap()
            ))
        )
        val logRequests = listOf(
            Pair(LogRequestData(
                requestId = "123",
                senderId = "joe123",
                logId = "log123",
                targetId = "jane456",
                requestDate = "3943894893",
                isComplete = false
            ), UserData(
                userId = null,
                username = null,
                joinDate = null,
                avatarPreset = 1,
                friends = emptyMap(),
                blocked = emptyMap()
            ))
        )

        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                val friends = remember { mutableStateOf(emptyList<UserData>()) }
                FriendRequestsScreen(mockNavController,
                    friendRequests = friendRequests,
                    logRequests = logRequests,
                    friends = friends,
                    addFriend = {},
                    updateRequest = { _, _, _ -> })
            }
        }
        composeTestRule.onNodeWithTag("PAGE_SUB_TITLE").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ADD_ICON", useUnmergedTree = true).assertExists()

        composeTestRule.onNodeWithTag("ACCEPT_LOG_REQUEST_ICON").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithTag("ACCEPT_FRIEND_REQUEST_ICON").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithTag("DELETE_LOG_REQUEST_ICON").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithTag("DELETE_FRIEND_REQUEST_ICON").assertIsDisplayed().assertHasClickAction()

        // Popup should not be visible
        composeTestRule.onNodeWithTag("ADD_FRIEND_POPUP").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("ADD_FRIEND_HEADER").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("FRIEND_NAME_INPUT").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("ADD_FRIEND_POPUP_FRIEND_NAME_LABEL").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("BUTTON_DIVIDER").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("ADD_FRIEND_BUTTON").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("CANCEL_BUTTON").assertIsNotDisplayed()
    }

    @Test
    fun testFriendRequestsAddFriendScreenInView() {
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                val friends = remember { mutableStateOf(emptyList<UserData>()) }
                FriendRequestsScreen(mockNavController,
                    friendRequests = emptyList(),
                    logRequests = emptyList(),
                    friends = friends,
                    addFriend = {},
                    updateRequest = { _, _, _ -> })
            }
        }

        composeTestRule.onNodeWithTag("ADD_ICON", useUnmergedTree = true).assertExists()
            .assertIsDisplayed().performClick()

        // Assert that the modal bottom sheet is displayed
        composeTestRule.onNodeWithTag("ADD_FRIEND_POPUP").assertIsDisplayed()

        // Assert the presence of UI elements within the modal bottom sheet
        composeTestRule.onNodeWithTag("ADD_FRIEND_HEADER").assertIsDisplayed()
        composeTestRule.onNodeWithTag("FRIEND_NAME_INPUT").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ADD_FRIEND_BUTTON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CANCEL_BUTTON").assertIsDisplayed()

        // Simulate entering text into the text field
        composeTestRule.onNodeWithTag("FRIEND_NAME_INPUT").performTextInput("JohnDoe")

        // Assert that the text field contains the entered text
        composeTestRule.onNodeWithTag("FRIEND_NAME_INPUT").assert(hasText("JohnDoe"))

        // Simulate clicking the create button
        composeTestRule.onNodeWithTag("ADD_FRIEND_BUTTON").performClick()

        // Assert that the modal bottom sheet is dismissed
        composeTestRule.onNodeWithTag("ADD_FRIEND_POPUP").assertDoesNotExist()
    }

    @Test
    fun testFriendRequestsCancelAddFriend() {
        composeTestRule.setContent {
            BottomNavGraph(mockNavController)
            Surface {
                val friends = remember { mutableStateOf(emptyList<UserData>()) }
                FriendRequestsScreen(mockNavController,
                    friendRequests = emptyList(),
                    logRequests = emptyList(),
                    friends = friends,
                    addFriend = {},
                    updateRequest = { _, _, _ -> })
            }
        }

        composeTestRule.onNodeWithTag("ADD_ICON", useUnmergedTree = true).assertExists()
            .assertIsDisplayed().performClick()

        composeTestRule.onNodeWithTag("ADD_FRIEND_POPUP").assertIsDisplayed()

        composeTestRule.onNodeWithTag("ADD_FRIEND_HEADER").assertIsDisplayed()
        composeTestRule.onNodeWithTag("FRIEND_NAME_INPUT").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ADD_FRIEND_BUTTON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CANCEL_BUTTON").assertIsDisplayed()

        composeTestRule.onNodeWithTag("FRIEND_NAME_INPUT").performTextInput("JohnDoe")
        composeTestRule.onNodeWithTag("FRIEND_NAME_INPUT").assert(hasText("JohnDoe"))

        composeTestRule.onNodeWithTag("CANCEL_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ADD_FRIEND_POPUP").assertDoesNotExist()
    }

}
