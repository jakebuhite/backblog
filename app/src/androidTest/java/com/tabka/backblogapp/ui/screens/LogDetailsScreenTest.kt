
package com.tabka.backblogapp.ui.screens

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.models.tmdb.Genre
import com.tabka.backblogapp.network.models.tmdb.MovieData
import com.tabka.backblogapp.network.models.tmdb.ProductionCompany
import com.tabka.backblogapp.ui.bottomnav.BottomNavGraph
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogDetailsViewModel
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import kotlinx.coroutines.Job
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

class FakeLogDetailsViewModel() : LogDetailsViewModel() {

    override val logData: MutableLiveData<LogData> = MutableLiveData()
    override val collaboratorsList: MutableLiveData<List<UserData>> = MutableLiveData()
    override val movies: MutableLiveData<List<MovieData>> = MutableLiveData()
    override val watchedMovies: MutableLiveData<List<MovieData>> = MutableLiveData()
    override val isOwner: MutableLiveData<Boolean> = MutableLiveData(false)

    fun updateMoviesList(newList: List<MovieData>) {
        movies.postValue(newList)
    }
    fun updateWatchedMoviesList(newList: List<MovieData>) {
        watchedMovies.postValue(newList)
    }

    fun updateOwner(owner: Boolean) {
        isOwner.postValue(owner)
    }

    fun updateCollaboratorsList(newList: List<UserData>) {
        collaboratorsList.postValue(newList)
    }

    fun resetCollaboratorsList() {
        collaboratorsList.value = emptyList()
    }
    override suspend fun getLogData(logId: String) {
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
        composeTestRule.onNodeWithTag("CREATOR_PICTURE").assertIsDisplayed()
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

        composeTestRule.onAllNodesWithTag("COLLABS_PICTURE").assertCountEquals(4)
        composeTestRule.onNodeWithTag("VIEW_ALL_COLLABS_BUTTON").assertIsDisplayed()
    }

    @Test
    fun testAddFriendsButtonClick() {
        logDetailsViewModel.updateOwner(true)
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
        logDetailsViewModel.updateOwner(true)
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
    fun testEditLogButtonClickDeleteLog() {
        logDetailsViewModel.updateOwner(owner = true)
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
        val movieDataList = listOf(
            MovieData(
                adult = false,
                backdropPath = "/pathToBackdrop1.jpg",
                belongsToCollection = null,
                budget = 100000000,
                genres = null,
                homepage = "https://examplemovie1.com",
                id = 1,
                imdbId = "tt1234567",
                originalLanguage = "en",
                originalTitle = "Example Movie Title 1",
                overview = "This is a brief overview of the first example movie.",
                popularity = 8.5,
                posterPath = "/pathToPoster1.jpg",
                productionCompanies = listOf(
                    ProductionCompany(id = 1, logoPath = "/logoPath1.jpg", name = "Example Production Company 1", originCountry = "US")
                ),
                productionCountries = listOf(mapOf("iso_3166_1" to "US", "name" to "United States of America")),
                releaseDate = "2024-01-01",
                revenue = 500000000,
                runtime = 120,
                spokenLanguages = listOf(mapOf("iso_639_1" to "en", "name" to "English")),
                status = "Released",
                tagline = "This is the tagline for the first movie.",
                title = "Example Movie Title 1",
                video = false,
                voteAverage = 7.8,
                voteCount = 256,
                images = null,
                releaseDates = null,
                watchProviders = null,
                credits = null
            ),
            MovieData(
                adult = false,
                backdropPath = "/pathToBackdrop2.jpg",
                belongsToCollection = null,
                budget = 150000000,
                genres = listOf(Genre(id = 14, name = "Fantasy"), Genre(id = 878, name = "Science Fiction")),
                homepage = "https://examplemovie2.com",
                id = 2,
                imdbId = "tt7654321",
                originalLanguage = "en",
                originalTitle = "Example Movie Title 2",
                overview = "This is a brief overview of the second example movie.",
                popularity = 9.3,
                posterPath = "/pathToPoster2.jpg",
                productionCompanies = listOf(
                    ProductionCompany(id = 2, logoPath = "/logoPath2.jpg", name = "Example Production Company 2", originCountry = "UK")
                ),
                productionCountries = listOf(mapOf("iso_3166_1" to "GB", "name" to "United Kingdom")),
                releaseDate = "2024-06-01",
                revenue = 750000000,
                runtime = 140,
                spokenLanguages = listOf(mapOf("iso_639_1" to "en", "name" to "English")),
                status = "Released",
                tagline = "This is the tagline for the second movie.",
                title = "Example Movie Title 2",
                video = false,
                voteAverage = 8.2,
                voteCount = 422,
                images = null,
                releaseDates = null,
                watchProviders = null,
                credits = null
            )
        )
        logDetailsViewModel.updateMoviesList(movieDataList)
        logDetailsViewModel.updateWatchedMoviesList(movieDataList)
        setContent()
        composeTestRule.onAllNodesWithTag("MOVIE_ENTRY").fetchSemanticsNodes().isNotEmpty()
        composeTestRule.onNodeWithTag("MOVIES_LIST").assertIsDisplayed()
        composeTestRule.onNodeWithTag("WATCHED_MOVIES_LIST").assertIsDisplayed()
    }
}
