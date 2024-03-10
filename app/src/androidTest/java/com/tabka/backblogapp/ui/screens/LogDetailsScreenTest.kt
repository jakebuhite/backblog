package com.tabka.backblogapp.ui.screens

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.MinimalMovieData
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import kotlinx.coroutines.Job
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeLogDetailsViewModel : LogDetailsViewModel() {
    override val logData: MutableLiveData<LogData> = MutableLiveData()
    override val collaboratorsList: MutableLiveData<List<UserData>> = MutableLiveData()
    override var movies: MutableLiveData<Map<String, MinimalMovieData>> = MutableLiveData(mapOf())
    override val watchedMovies: MutableLiveData<Map<String, MinimalMovieData>> = MutableLiveData(mapOf())
    override val isOwner: MutableLiveData<Boolean> = MutableLiveData(false)
    override val owner: MutableLiveData<UserData> = MutableLiveData()
    override val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    fun updateMoviesList(newList: Map<String, MinimalMovieData>) {
        movies.postValue(emptyMap())
        movies.postValue(newList)
    }

    fun updateWatchedMoviesList(newList: Map<String, MinimalMovieData>) {
        watchedMovies.postValue(emptyMap())
        watchedMovies.postValue(newList)
    }

    fun updateIsOwner(owner: Boolean) {
        isOwner.postValue(owner)
    }

    fun updateOwner(newOwner: UserData) {
        owner.postValue(newOwner)
    }

    fun updateIsLoading(newBool: Boolean) {
        isLoading.postValue(newBool)
    }

    fun updateCollaboratorsList(newList: List<UserData>) {
        collaboratorsList.postValue(newList)
    }

    override fun getLogData(logId: String) {
        logData.value = LogData(
            logId = "1234",
            name = "My Movie Log",
            creationDate = "2024-02-16",
            lastModifiedDate = "2024-02-16",
            isVisible = true,
            owner = Owner(
                userId = "5678",
                priority = 1
            ),
            collaborators = mutableListOf("collab1", "collab2"),
            order = mapOf("movie1" to 1, "movie2" to 2),
            movieIds = mutableListOf("movieId1", "movieId2"),
            watchedIds = mutableListOf("watchedId1", "watchedId2")
        )

        val fakeUser = UserData(
            userId = "user123",
            username = "bob123",
            joinDate = "3435345454",
            avatarPreset = 1,
            friends = emptyMap(),
            blocked= emptyMap()
        )
        updateOwner(fakeUser)
        updateIsOwner(true)
    }

    override suspend fun deleteLog(): Job? {
        return null
    }
}

@RunWith(AndroidJUnit4::class)
class LogDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: TestNavHostController
    private val logId = "1cPRZ8LEUYndLj53pxSV"
    private val logViewModel = LogViewModel()
    private val friendsViewModel = FriendsViewModel()
    private val logDetailsViewModel = FakeLogDetailsViewModel()

    @Before
    fun setup() {
        mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
        mockNavController.navigatorProvider.addNavigator(ComposeNavigator())
    }

    fun setContent() {
        composeTestRule.setContent {
            BottomNavGraph(navController = mockNavController)
            LogDetailsScreen(
                navController = mockNavController,
                logId = logId,
                friendsViewModel = friendsViewModel,
                logViewModel = logViewModel,
                logDetailsViewModel
            )
        }
    }

    @Test
    fun testLogDetailsScreenInView() {
        setContent()
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag("LOADING_PROGRESS").fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onNodeWithTag("OWNER_PICTURE").assertIsDisplayed()
        composeTestRule.onNodeWithText("My Movie Log").assertIsDisplayed()
        composeTestRule.onNodeWithTag("MOVIE_COUNT").assertIsDisplayed()
        // Buttons
        composeTestRule.onNodeWithTag("ADD_ICON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EDIT_ICON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SHUFFLE_ICON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ADD_MOVIE_ICON").assertIsDisplayed()
    }

    @Test
    fun testProgressBarInView() {
        logDetailsViewModel.updateIsLoading(true)
        setContent()
        composeTestRule.onNodeWithTag("LOADING_PROGRESS").assertIsDisplayed()
    }

    @Test
    fun testWith5Collaborators() {
        val userDataList = listOf(
            UserData(userId = "1", username = "UserOne", joinDate = "2022-01-01", avatarPreset = 2, friends = mapOf("2" to true), blocked = mapOf("5" to false)),
            UserData(userId = "2", username = "UserTwo", joinDate = "2022-02-01", avatarPreset = 3, friends = mapOf("1" to true), blocked = mapOf("4" to false)),
            UserData(userId = "3", username = "UserThree", joinDate = "2022-03-01", avatarPreset = 4, friends = mapOf("4" to true), blocked = mapOf("2" to false)),
            UserData(userId = "4", username = "UserFour", joinDate = "2022-04-01", avatarPreset = 1, friends = mapOf("3" to true), blocked = mapOf("1" to false)),
            UserData(userId = "5", username = "UserFive", joinDate = "2022-05-01", avatarPreset = 2, friends = mapOf("5" to true), blocked = mapOf("3" to false))
        )
        logDetailsViewModel.updateCollaboratorsList(userDataList)
        setContent()

        composeTestRule.onNodeWithTag("COLLAB_PICTURE_0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("COLLAB_PICTURE_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("COLLAB_PICTURE_2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("COLLAB_PICTURE_3").assertIsDisplayed()
        composeTestRule.onNodeWithTag("COLLAB_PICTURE_4").assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("VIEW_ALL_COLLABS_BUTTON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("VIEW_ALL_COLLABS_BUTTON").assertHasClickAction()
    }

    @Test
    fun testAddFriendsButtonClick() {
        logDetailsViewModel.updateIsOwner(true)
        setContent()
        composeTestRule.onNodeWithTag("ADD_ICON").performClick()
        composeTestRule.onNodeWithTag("ADD_COLLAB_LOG_NAME").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SHEET_CONTENT").assertIsDisplayed()
    }

    @Test
    fun testCollabsInAddButton() {
        val collabsList = listOf(
            UserData()
        )
        logDetailsViewModel.updateCollaboratorsList(collabsList)
        logDetailsViewModel.updateIsOwner(true)
        setContent()
        composeTestRule.onNodeWithTag("ADD_ICON").performClick()
        composeTestRule.onNodeWithTag("COLLABS_LIST_ADD_SHEET").assertIsDisplayed()
    }

    @Test
    fun testEditLogButtonClickSave() {
        setContent()
        composeTestRule.onNodeWithTag("EDIT_ICON").performClick()
        composeTestRule.onNodeWithTag("LOG_NAME_INPUT").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EDIT_SHEET_CONTENT").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EDIT_SAVE_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ALERT_DIALOG").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EDIT_SAVE_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ALERT_DIALOG_ACCEPT_BUTTON").performClick()
        composeTestRule.onNodeWithTag("EDIT_SHEET_CONTENT").assertIsNotDisplayed()
    }

    @Test
    fun testEditLogButtonShowsMovies() {
        val movieDataList = mapOf(
            "1" to MinimalMovieData(
                image = "/pathToBackdrop1.jpg",
                id = "1",
                title = null,
            ),
            "2" to MinimalMovieData(
                image = "/pathToBackdrop2.jpg",
                id = "2",
                title = "Example Movie Title 2",
            )
        )
        val watchedDataList = mapOf(
            "3" to MinimalMovieData(
                image = "/pathToBackdrop3.jpg",
                id = "3",
                title = "Example Movie Title 3",
            ),
            "4" to MinimalMovieData(
                image = "/pathToBackdrop4.jpg",
                id = "4",
                title = "Example Movie Title 4",
            )
        )
        logDetailsViewModel.updateMoviesList(movieDataList)
        logDetailsViewModel.updateWatchedMoviesList(watchedDataList)
        setContent()
        composeTestRule.onNodeWithTag("EDIT_ICON").performClick()
        composeTestRule.onAllNodesWithTag("REMOVE_MOVIE_ICON").fetchSemanticsNodes().isNotEmpty()
        composeTestRule.onAllNodesWithTag("EDIT_LOG_MOVIE_TITLE").fetchSemanticsNodes().isNotEmpty()
        composeTestRule.onAllNodesWithTag("DRAG_ICON").fetchSemanticsNodes().isNotEmpty()
    }

    @Test
    fun testEditLogButtonClickDeleteLog() {
        logDetailsViewModel.updateIsOwner(owner = true)
        setContent()
        composeTestRule.onNodeWithTag("EDIT_ICON").performClick()
        composeTestRule.onNodeWithTag("EDIT_DELETE_BUTTON").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EDIT_DELETE_BUTTON").performClick()
        composeTestRule.onNodeWithTag("ALERT_DIALOG").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ALERT_DIALOG_ACCEPT_BUTTON").performClick()
    }

    @Test
    fun testShuffleIconButtonClick() {
        setContent()
        composeTestRule.onNodeWithTag("SHUFFLE_ICON").performClick()
        composeTestRule.onNodeWithTag("ALERT_DIALOG").assertIsDisplayed()
    }

    @Test
    fun testInViewMovies() {
        val movieDataList = mapOf(
            "1" to MinimalMovieData(
                image = "/pathToBackdrop1.jpg",
                id = "1",
                title = "Example Movie Title 1",
            ),
            "2" to MinimalMovieData(
                image = "/pathToBackdrop2.jpg",
                id = "2",
                title = "Example Movie Title 2",
            )
        )
        val watchedDataList = mapOf(
            "3" to MinimalMovieData(
                image = "/pathToBackdrop3.jpg",
                id = "3",
                title = "Example Movie Title 3",
            ),
            "4" to MinimalMovieData(
                image = "/pathToBackdrop4.jpg",
                id = "4",
                title = "Example Movie Title 4",
            )
        )
        logDetailsViewModel.updateMoviesList(movieDataList)
        logDetailsViewModel.updateWatchedMoviesList(watchedDataList)
        setContent()
        composeTestRule.onAllNodesWithTag("MOVIE_ENTRY").fetchSemanticsNodes().isNotEmpty()
        composeTestRule.onNodeWithTag("MOVIES_LIST").assertIsDisplayed()
    }
}