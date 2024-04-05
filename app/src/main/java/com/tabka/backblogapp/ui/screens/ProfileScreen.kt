package com.tabka.backblogapp.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tabka.backblogapp.R
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.ui.shared.FriendsList
import com.tabka.backblogapp.ui.shared.PublicLogs
import com.tabka.backblogapp.ui.viewmodels.LogViewModel
import com.tabka.backblogapp.ui.viewmodels.ProfileViewModel
import com.tabka.backblogapp.util.getAvatarResourceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileScreen(navController: NavHostController, friendId: String?, profileViewModel: ProfileViewModel, logViewModel: LogViewModel) {
    val userDataState = profileViewModel.userData.observeAsState()
    val pageTitle: String = userDataState.value?.username ?: ""

    val userAvatar = userDataState.value?.avatarPreset ?: 1
    val composableScope = rememberCoroutineScope()

    // Logs
    val logState = profileViewModel.publicLogData.observeAsState()
    val publicLogs = logState.value ?: emptyList()

    // Friend's data
    val friends = profileViewModel.friendsData.collectAsState()

    val alreadyFriend = friends.value.any { it.userId == Firebase.auth.currentUser?.uid }

    // Notification message
    val notificationMsg = profileViewModel.notificationMsg.observeAsState()

    val context = LocalContext.current
    if (notificationMsg.value != "") {
        Toast.makeText(context, notificationMsg.value, Toast.LENGTH_SHORT).show()
        profileViewModel.clearMessage()
    }

    // Get data
    composableScope.launch {
        profileViewModel.getUserData(friendId ?: "")
        profileViewModel.getPublicLogs(friendId ?: "")
        profileViewModel.getFriends(friendId ?: "")
    }

    val addFriend: () -> Unit = { profileViewModel.sendFriendRequest() }
    val removeFriend: () -> Unit = { profileViewModel.removeFriend() }
    val blockUser: () -> Unit = { profileViewModel.blockUser() }

    // UI Content
    FriendsPageContent(navController,
        pageTitle,
        publicLogs,
        friends,
        userAvatar,
        logViewModel,
        alreadyFriend,
        addFriend,
        removeFriend,
        blockUser,
        composableScope
    )
    Box(modifier = Modifier.offset(x = 16.dp, y = 20.dp)) {
        BackButton(navController = navController, visible = true)
    }
}

@Composable
fun FriendsPageContent(
    navController: NavHostController,
    pageTitle: String,
    publicLogs: List<LogData>,
    friends: State<List<UserData>>,
    userAvatar: Int,
    logViewModel: LogViewModel,
    alreadyFriend: Boolean,
    onAddFriendSelected: () -> Unit,
    onRemoveFriendSelected: () -> Unit,
    onBlockUserSelected: () -> Unit,
    composableScope: CoroutineScope
) {
    val scrollState = rememberScrollState()
    var showBottomSheet by remember { mutableStateOf(false) }

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
            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()) {
                FriendsPageHeader(navController) { showBottomSheet = true }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = getAvatarResourceId(userAvatar).second),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(45.dp)
                )
                PageTitle(pageTitle)
            }
            TabScreen(navController, publicLogs, friends, logViewModel)
            if (showBottomSheet) {
                AddFriendOrBlockDialog(
                    onAddFriendSelected = {
                        onAddFriendSelected()
                                          },
                    onRemoveFriendSelected = {
                        onRemoveFriendSelected()
                        navController.navigate("friends")
                                             },
                    onBlockUserSelected = {
                        onBlockUserSelected()
                        navController.navigate("friends")
                                          },
                    dismissBottomSheet = { showBottomSheet = false },
                    alreadyFriend = alreadyFriend,
                    composableScope = composableScope
                )
            }
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun FriendsPageHeader(navController: NavHostController, showBottomSheet: () -> Unit) {
    Row {
        Spacer(Modifier.weight(1f))
        Icon(Icons.Rounded.Person,
            contentDescription = "Add Friend icon",
            modifier = Modifier
                .height(45.dp)
                .width(45.dp)
                .offset(x = (-10).dp)
                .clickable { showBottomSheet() }
                .testTag("USER_FRIEND_ICON"),
            tint = Color.White
        )
    }
}

@Composable
fun TabScreen(
    navController: NavHostController,
    publicLogs: List<LogData>,
    friends: State<List<UserData>>,
    logViewModel: LogViewModel
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
            0 -> PublicLogs(navController, publicLogs, logViewModel)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendOrBlockDialog(
    onAddFriendSelected: () -> Unit,
    onRemoveFriendSelected: () -> Unit,
    onBlockUserSelected: () -> Unit,
    dismissBottomSheet: () -> Unit,
    alreadyFriend: Boolean,
    composableScope: CoroutineScope
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { dismissBottomSheet() },
        sheetState = sheetState,
        containerColor = colorResource(id = R.color.bottomnav)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (alreadyFriend) {
                Button(onClick = {
                    composableScope.launch {
                        onRemoveFriendSelected()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            dismissBottomSheet()
                        }
                    }
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(35),
                    modifier = Modifier
                        .height(55.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(
                            1.dp,
                            Color(0xFFDC3545),
                            shape = RoundedCornerShape(30.dp)
                        )) {
                    Text("REMOVE FRIEND", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = Color(0xFFDC3545))
                }
            } else {
                Button(onClick = {
                    composableScope.launch {
                        onAddFriendSelected()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            dismissBottomSheet()
                        }
                    }
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.sky_blue),
                        disabledContainerColor = Color.Gray
                    )) {
                    Text("ADD FRIEND", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF303437))
            )

            Button(onClick = {
                composableScope.launch {
                    onBlockUserSelected()
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        dismissBottomSheet()
                    }
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
                shape = RoundedCornerShape(35),
                modifier = Modifier
                    .height(55.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(
                        1.dp,
                        Color(0xFFDC3545),
                        shape = RoundedCornerShape(30.dp)
                    )) {
                Text("BLOCK USER", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = Color(0xFFDC3545))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}