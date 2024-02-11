package com.tabka.backblogapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.LogRequestData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.util.getAvatarResourceId
import kotlinx.coroutines.launch
import kotlin.math.ceil

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FriendsScreen(navController: NavController, friendsViewModel: FriendsViewModel) {
    val userDataState = friendsViewModel.userData.observeAsState()
    val pageTitle: String = userDataState.value?.username ?: ""
    val userAvatar = userDataState.value?.avatarPreset ?: 1
    val composableScope = rememberCoroutineScope()

    // Logs
    val logState = friendsViewModel.publicLogData.observeAsState()
    val publicLogs = logState.value ?: emptyList()

    // Friend Requests (And the sender's data)
    val friendReqState = friendsViewModel.friendReqData.observeAsState()
    val friendReqs = friendReqState.value ?: emptyList()

    // Log Requests (And the sender's data)
    val logReqState = friendsViewModel.logReqData.observeAsState()
    val logReqs = logReqState.value ?: emptyList()

    // Friend's data
    val friends = friendsViewModel.friendsData.collectAsState()

    // Get data
    composableScope.launch {
        friendsViewModel.getUserData()
        friendsViewModel.getPublicLogs()
        friendsViewModel.getFriends()
        friendsViewModel.getFriendRequests()
        friendsViewModel.getLogRequests()
    }

    // UI Content
    FriendsContent(navController, pageTitle, publicLogs,
        friendReqs, logReqs, friends, userAvatar) {
        friendsViewModel.sendFriendRequest(it)
    }

}

@Composable
fun FriendsContent(
    navController: NavController,
    pageTitle: String,
    publicLogs: List<LogData>,
    friendRequests:List<Pair<FriendRequestData, UserData>>,
    logRequests: List<Pair<LogRequestData, UserData>>,
    friends: State<List<UserData>>,
    userAvatar: Int,
    addFriend: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BackgroundGradient()

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Row(horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()) {
                FriendHeader(navController)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = getAvatarResourceId(userAvatar).second),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(50.dp)
                )
                pageTitle(pageTitle)
            }
            TabScreen(navController, publicLogs, friendRequests, logRequests, friends, addFriend)
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun FriendHeader(navController: NavController) {
    Image(
        painter = painterResource(id = R.drawable.settings_icon),
        contentDescription = "Settings icon",
        modifier = Modifier
            .height(35.dp)
            .width(35.dp)
            .clickable { navController.navigate("settings") }
            .testTag("SETTINGS_ICON")
    )
}

@Composable
fun TabScreen(
    navController: NavController,
    publicLogs: List<LogData>,
    friendRequests: List<Pair<FriendRequestData, UserData>>,
    logRequests: List<Pair<LogRequestData, UserData>>,
    friends: State<List<UserData>>,
    addFriend: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Logs", "Friends")

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(
            selectedTabIndex = selectedTab,
            backgroundColor = Color.Transparent,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF3891E1)
                    )
                }
            },
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                title,
                                color = if (selectedTab == index) Color(0xFF3891E1) else Color(0xFF979C9E),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            // Add the notification badge if there are friend or log requests
                            if ((index == 1) && (friendRequests.isNotEmpty() || logRequests.isNotEmpty())) {
                                Spacer(modifier = Modifier.width(4.dp)) // Adjust spacing as needed
                                Image(
                                    painter = painterResource(id = R.drawable.badge_notification),
                                    contentDescription = "Notification Badge",
                                    modifier = Modifier.size(5.dp)
                                )
                            }
                        }
                    },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        when (selectedTab) {
            0 -> PublicLogs(navController, publicLogs)
            1 -> FriendRequestsScreen(friendRequests, logRequests, friends, addFriend)
        }
    }
}

@Composable
fun PublicLogs(navController: NavController, publicLogs: List<LogData>) {
    Text("Public Logs", style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .testTag("PAGE_SUB_TITLE"))
    DisplayPublicLogs(navController = navController, allLogs = publicLogs)
}

@Composable
fun DisplayPublicLogs(navController: NavController, allLogs: List<LogData>?) {
    val multiplier = ceil(allLogs!!.size / 2.0).toInt()
    val containerHeight: Dp = (185 * multiplier).dp

    Box(
        modifier = Modifier
            .height(containerHeight)
            .fillMaxWidth()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(allLogs.size, key = { index -> allLogs[index].logId!! }) { index ->
                val log = allLogs[index]
                Card(
                    modifier = Modifier
                        .size(175.dp)
                        .clickable { navController.navigate("public_log_details_${log.logId}") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors( containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.creator),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = Color.Black.copy(alpha = 0.75f),
                                    shape = RoundedCornerShape(5.dp)
                                )
                        )

                        // Text overlay
                        Text(
                            text = "${log.name}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .align(Alignment.Center)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}