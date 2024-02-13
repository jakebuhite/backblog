package com.tabka.backblogapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.ui.shared.FriendsList
import com.tabka.backblogapp.ui.shared.PublicLogs
import com.tabka.backblogapp.ui.viewmodels.ProfileViewModel
import com.tabka.backblogapp.util.getAvatarResourceId
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileScreen(navController: NavController, friendId: String?, profileViewModel: ProfileViewModel) {
    val userDataState = profileViewModel.userData.observeAsState()
    val pageTitle: String = userDataState.value?.username ?: ""

    val userAvatar = userDataState.value?.avatarPreset ?: 1
    val composableScope = rememberCoroutineScope()

    // Logs
    val logState = profileViewModel.publicLogData.observeAsState()
    val publicLogs = logState.value ?: emptyList()

    // Friend's data
    val friends = profileViewModel.friendsData.collectAsState()

    // Get data
    composableScope.launch {
        profileViewModel.getUserData(friendId ?: "")
        profileViewModel.getPublicLogs(friendId ?: "")
        profileViewModel.getFriends(friendId ?: "")
    }

    // UI Content
    FriendsPageContent(navController, pageTitle, publicLogs, friends, userAvatar)
}

@Composable
fun FriendsPageContent(
    navController: NavController,
    pageTitle: String,
    publicLogs: List<LogData>,
    friends: State<List<UserData>>,
    userAvatar: Int,
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
                FriendsPageHeader(navController)
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
            TabScreen(navController, publicLogs, friends)
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun FriendsPageHeader(navController: NavController) {
    Image(
        painter = painterResource(id = R.drawable.user_add),
        contentDescription = "Add Friend icon",
        modifier = Modifier
            .height(35.dp)
            .width(35.dp)
            .clickable { navController.navigate("settings") } // TODO: Change this to add friend sheet
            .testTag("USER_FRIEND_ICON")
    )
}

@Composable
fun TabScreen(
    navController: NavController,
    publicLogs: List<LogData>,
    friends: State<List<UserData>>,
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
            1 -> FriendsPageFriendsList(navController, friends)
        }
    }
}

@Composable
fun FriendsPageFriendsList(
    navController: NavController,
    friends: State<List<UserData>>
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Friends", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .testTag("PAGE_SUB_TITLE")
        )
    }
    FriendsList(navController, friends)
}