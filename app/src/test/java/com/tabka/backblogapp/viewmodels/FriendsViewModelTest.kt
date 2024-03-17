package com.tabka.backblogapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.repository.FriendRepository
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.ui.viewmodels.FriendsViewModel
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class FriendsViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var logRepository: LogRepository

    @Mock
    private lateinit var friendRepository: FriendRepository

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var currentUser: FirebaseUser // Mock FirebaseUser

    @Mock
    private lateinit var observer: Observer<UserData>

    @Mock
    private lateinit var strObserver: Observer<String>

    @Mock
    private lateinit var logObserver: Observer<List<LogData>>

    private lateinit var friendsViewModel: FriendsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        friendsViewModel = FriendsViewModel(auth, userRepository, logRepository, friendRepository)

        // Mock behavior for auth.currentUser
        whenever(auth.currentUser).thenReturn(currentUser)
        whenever(currentUser.uid).thenReturn("fakeUserId")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetUserDataShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val userData = UserData(
                userId = userId,
                username = "fakeUser"
            )

            friendsViewModel.userData.observeForever(observer)
            whenever(userRepository.getUser(anyString())).thenReturn(DataResult.Success(userData))

            // Act
            friendsViewModel.getUserData()

            // Assert
            assertEquals(friendsViewModel.userData.value?.userId, userId)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetUserDataNullUserIdShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val userData = UserData(
                userId = userId,
                username = "fakeUser"
            )

            friendsViewModel.userData.observeForever(observer)
            whenever(auth.currentUser).thenReturn(null)
            whenever(userRepository.getUser(anyString())).thenReturn(DataResult.Success(userData))

            // Act
            friendsViewModel.getUserData()

            // Assert
            assertEquals(friendsViewModel.userData.value, null)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetUserDataShouldReturnFailure() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            friendsViewModel.userData.observeForever(observer)

            val exception = Exception("Error getting user")
            whenever(userRepository.getUser(anyString())).thenReturn(DataResult.Failure(exception))

            // Act
            friendsViewModel.getUserData()

            // Assert
            assertEquals(friendsViewModel.userData.value, null)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetPublicLogsShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val logData = listOf(
                LogData(
                    logId = "log123",
                    name = "My Log",
                    creationDate = "now",
                    lastModifiedDate = "now",
                    isVisible = true,
                    owner = null,
                    collaborators = null,
                    order = null,
                    movieIds = null,
                    watchedIds = null
                )
            )

            friendsViewModel.publicLogData.observeForever(logObserver)
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(
                logData
            ))

            // Act
            friendsViewModel.getPublicLogs()

            // Assert
            assertEquals(friendsViewModel.publicLogData.value, logData)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetPublicLogsNullUserIdShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            friendsViewModel.publicLogData.observeForever(logObserver)
            whenever(auth.currentUser).thenReturn(null)
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(
                emptyList()
            ))

            // Act
            friendsViewModel.getPublicLogs()

            // Assert
            assertEquals(friendsViewModel.publicLogData.value, null)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetPublicLogsShouldReturnFailure() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            friendsViewModel.publicLogData.observeForever(logObserver)

            val exception = Exception("Error getting public logs")
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Failure(exception))

            // Act
            friendsViewModel.getPublicLogs()

            // Assert
            assertEquals(friendsViewModel.publicLogData.value, null)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetFriendsShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userData = listOf(
                UserData(
                    userId = "user123"
                ),
                UserData(
                    userId = "user456"
                )
            )

            whenever(friendRepository.getFriends(anyString())).thenReturn(
                userData
            )

            // Act
            friendsViewModel.getFriends()

            // Assert
            assertEquals(friendsViewModel.friendsData.value, userData)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetFriendsNullUserIdShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            whenever(auth.currentUser).thenReturn(null)
            whenever(friendRepository.getFriends(anyString())).thenReturn(
                emptyList()
            )

            // Act
            friendsViewModel.getFriends()

            // Assert
            assertEquals(friendsViewModel.friendsData.value, emptyList<UserData>())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testGetFriendsShouldHandleError() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val error = RuntimeException("Failed to get friends")

            // Mock friendRepository to throw an exception
            whenever(friendRepository.getFriends(anyString())).thenAnswer {
                throw error
            }

            // Act
            friendsViewModel.getFriends()

            // Assert
            assertEquals(friendsViewModel.friendsData.value, emptyList<UserData>())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestUserNotAuthenticated() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            friendsViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser).thenReturn(null)

            // Act
            friendsViewModel.sendFriendRequest("")

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "User not authenticated")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestUserCannotFriendThemselves() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"

            friendsViewModel.notificationMsg.observeForever(strObserver)
            friendsViewModel.userData.observeForever(observer)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            friendsViewModel.userData.value = UserData(userId = userId, username = username)

            // Act
            friendsViewModel.sendFriendRequest(username)

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "You cannot friend yourself!")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestGetUserByUsernameFailed() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"
            val targetUsername = "targetUsername"

            friendsViewModel.notificationMsg.observeForever(strObserver)
            friendsViewModel.userData.observeForever(observer)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Failure(Exception("Failed to get user by username"))
            )

            friendsViewModel.userData.value = UserData(
                userId = userId,
                username = username
            )

            // Act
            friendsViewModel.sendFriendRequest(targetUsername)

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "There was an error sending a request")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestUserAlreadyFriend() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"
            val targetUsername = "targetUsername"

            friendsViewModel.notificationMsg.observeForever(strObserver)
            friendsViewModel.userData.observeForever(observer)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(
                    UserData(
                        userId = userId,
                        username = username,
                        friends = mapOf(userId to true) // User already a friend
                    )
                )
            )
            friendsViewModel.userData.value = UserData(
                userId = userId,
                username = username,
            )

            // Act
            friendsViewModel.sendFriendRequest(targetUsername)

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "$targetUsername is already a friend!")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestException() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"
            val friendUsername = "fakeFriend"
            val targetId = "targetUserId"

            friendsViewModel.userData.observeForever(observer)
            friendsViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(
                    UserData(
                        userId = userId,
                        username = friendUsername
                    )
                )
            )
            friendsViewModel.userData.value = UserData(
                userId = userId,
                username = username
            )
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(
                DataResult.Failure(Exception("Simulated exception"))
            )

            // Act
            friendsViewModel.sendFriendRequest(friendUsername)

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "There was an error sending a request")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestAlreadySentRequestToUser() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"
            val friendUsername = "fakeFriend"
            val userRequests = listOf(
                FriendRequestData(
                    requestId = "req123",
                    senderId = userId,
                    targetId = userId,
                    requestDate = "now",
                    isComplete = false
                )
            )

            friendsViewModel.userData.observeForever(observer)
            friendsViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(
                    UserData(
                        userId = userId,
                        username = friendUsername
                    )
                )
            )
            friendsViewModel.userData.value = UserData(
                userId = userId,
                username = username
            )
            whenever(friendRepository.getFriendRequests(anyString())).thenReturn(DataResult.Success(userRequests))

            // Act
            friendsViewModel.sendFriendRequest(friendUsername)

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "You've already sent a request to $friendUsername!")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestReceivedException() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"
            val friendUsername = "fakeFriend"
            val targetId = "targetUserId"
            val userRequests = listOf(
                FriendRequestData(
                    requestId = "req123",
                    senderId = userId,
                    targetId = targetId,
                    requestDate = "now",
                    isComplete = false
                )
            )

            friendsViewModel.userData.observeForever(observer)
            friendsViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(targetId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(
                    UserData(
                        userId = userId,
                        username = friendUsername
                    )
                )
            )
            friendsViewModel.userData.value = UserData(
                userId = targetId,
                username = username
            )

            whenever(friendRepository.getFriendRequests(userId)).thenReturn(DataResult.Success(userRequests))
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(
                DataResult.Failure(Exception("Simulated exception"))
            )

            // Act
            friendsViewModel.sendFriendRequest(friendUsername)

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "There was an error sending a request")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestAlreadyReceivedRequestFromUser() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"
            val friendUsername = "fakeFriend"
            val targetId = "targetUserId"
            val userRequests = listOf(
                FriendRequestData(
                    requestId = "req123",
                    senderId = userId,
                    targetId = targetId,
                    requestDate = "now",
                    isComplete = false
                )
            )

            friendsViewModel.userData.observeForever(observer)
            friendsViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(targetId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(
                    UserData(
                        userId = userId,
                        username = friendUsername
                    )
                )
            )
            friendsViewModel.userData.value = UserData(
                userId = targetId,
                username = username
            )
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(
                DataResult.Success(userRequests)
            )
            whenever(friendRepository.getFriendRequests(userId)).thenReturn(DataResult.Success(
                listOf()
            ))

            // Act
            friendsViewModel.sendFriendRequest(friendUsername)

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "$friendUsername has already sent you a friend request!")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"
            val friendUsername = "fakeFriend"
            val targetId = "targetUserId"
            val userRequests = emptyList<FriendRequestData>()

            friendsViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(UserData(userId = userId, username = friendUsername))
            )
            friendsViewModel.userData.value = UserData(
                userId = userId,
                username = username
            )
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(DataResult.Success(userRequests))
            whenever(friendRepository.getFriendRequests(userId)).thenReturn(DataResult.Success(userRequests))
            whenever(friendRepository.addFriendRequest(anyString(), anyString(), anyString())).thenReturn(DataResult.Success(true))

            // Act
            friendsViewModel.sendFriendRequest(friendUsername)

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "Friend request sent to $friendUsername!")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestHandleErrorAddingFriendRequest() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"
            val friendUsername = "fakeFriend"
            val targetId = "targetUserId"
            val userRequests = emptyList<FriendRequestData>()

            friendsViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(UserData(userId = userId, username, friendUsername))
            )
            friendsViewModel.userData.value = UserData(
                userId = userId,
                username = username
            )
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(DataResult.Success(userRequests))
            whenever(friendRepository.getFriendRequests(userId)).thenReturn(DataResult.Success(userRequests))

            val error = RuntimeException("Error adding friend request")
            whenever(friendRepository.addFriendRequest(anyString(), anyString(), anyString())).thenReturn(DataResult.Failure(error))

            // Act
            friendsViewModel.sendFriendRequest(friendUsername)

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "There was an error sending a request")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testClearMessage() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            friendsViewModel.userData.observeForever(observer)
            friendsViewModel.notificationMsg.observeForever(strObserver)

            friendsViewModel.notificationMsg.value = "Test123"

            // Act
            friendsViewModel.clearMessage()

            // Assert
            assertEquals(friendsViewModel.notificationMsg.value, "")
        } finally {
            Dispatchers.resetMain()
        }
    }
}