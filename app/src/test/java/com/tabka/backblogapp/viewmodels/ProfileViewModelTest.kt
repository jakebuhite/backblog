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
import com.tabka.backblogapp.ui.viewmodels.ProfileViewModel
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

class ProfileViewModelTest {
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

    private lateinit var profileViewModel: ProfileViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        profileViewModel = ProfileViewModel(auth, userRepository, logRepository, friendRepository)

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

            profileViewModel.userData.observeForever(observer)
            whenever(userRepository.getUser(anyString())).thenReturn(DataResult.Success(userData))

            // Act
            profileViewModel.getUserData(userId)

            // Assert
            assertEquals(profileViewModel.userData.value?.userId, userId)
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

            profileViewModel.userData.observeForever(observer)
            whenever(auth.currentUser).thenReturn(null)
            whenever(userRepository.getUser(anyString())).thenReturn(DataResult.Success(userData))

            // Act
            profileViewModel.getUserData(userId)

            // Assert
            assertEquals(profileViewModel.userData.value, null)
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
            val userId = "fakeUserId"

            profileViewModel.userData.observeForever(observer)

            val exception = Exception("Error getting user")
            whenever(userRepository.getUser(anyString())).thenReturn(DataResult.Failure(exception))

            // Act
            profileViewModel.getUserData(userId)

            // Assert
            assertEquals(profileViewModel.userData.value, null)
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
            val userId = "fakeUserId"
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

            profileViewModel.publicLogData.observeForever(logObserver)
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(
                logData
            ))

            // Act
            profileViewModel.getPublicLogs(userId)

            // Assert
            assertEquals(profileViewModel.publicLogData.value, logData)
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
            val userId = "fakeUserId"

            profileViewModel.publicLogData.observeForever(logObserver)
            whenever(auth.currentUser).thenReturn(null)
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Success(
                emptyList()
            ))

            // Act
            profileViewModel.getPublicLogs(userId)

            // Assert
            assertEquals(profileViewModel.publicLogData.value, null)
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
            val userId = "fakeUserId"

            profileViewModel.publicLogData.observeForever(logObserver)

            val exception = Exception("Error getting public logs")
            whenever(logRepository.getLogs(anyString(), anyBoolean())).thenReturn(DataResult.Failure(exception))

            // Act
            profileViewModel.getPublicLogs(userId)

            // Assert
            assertEquals(profileViewModel.publicLogData.value, null)
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
            val userId = "fakeUserId"
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
            profileViewModel.getFriends(userId)

            // Assert
            assertEquals(profileViewModel.friendsData.value, userData)
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
            val userId = "fakeUserId"

            whenever(auth.currentUser).thenReturn(null)
            whenever(friendRepository.getFriends(anyString())).thenReturn(
                emptyList()
            )

            // Act
            profileViewModel.getFriends(userId)

            // Assert
            assertEquals(profileViewModel.friendsData.value, emptyList<UserData>())
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
            val userId = "fakeUserId"
            val error = RuntimeException("Failed to get friends")

            // Mock friendRepository to throw an exception
            whenever(friendRepository.getFriends(anyString())).thenAnswer {
                throw error
            }

            // Act
            profileViewModel.getFriends(userId)

            // Assert
            assertEquals(profileViewModel.friendsData.value, emptyList<UserData>())
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
            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser).thenReturn(null)

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "User not authenticated")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testSendFriendRequestUserNotFound() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"

            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            profileViewModel.userData.value = null

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "User not found")
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

            profileViewModel.notificationMsg.observeForever(strObserver)
            profileViewModel.userData.observeForever(observer)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Failure(Exception("Failed to get user by username"))
            )

            profileViewModel.userData.value = UserData(
                userId = userId,
                username = username
            )

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "There was an error sending a request")
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

            profileViewModel.notificationMsg.observeForever(strObserver)
            profileViewModel.userData.observeForever(observer)
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
            profileViewModel.userData.value = UserData(
                userId = userId,
                username = username,
            )

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "fakeUsername is already a friend!")
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
            val friendUsername = "fakeFriend"
            val targetId = "targetUserId"

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(
                    UserData(
                        userId = userId,
                        username = friendUsername
                    )
                )
            )
            profileViewModel.userData.value = UserData(
                userId = userId,
                username = friendUsername
            )
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(
                DataResult.Failure(Exception("Simulated exception"))
            )

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "There was an error sending a request")
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

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(
                    UserData(
                        userId = userId,
                        username = friendUsername
                    )
                )
            )
            profileViewModel.userData.value = UserData(
                userId = userId,
                username = friendUsername
            )
            whenever(friendRepository.getFriendRequests(anyString())).thenReturn(DataResult.Success(userRequests))

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "You've already sent a request to $friendUsername!")
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

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(
                    UserData(
                        userId = targetId,
                        username = friendUsername
                    )
                )
            )
            profileViewModel.userData.value = UserData(
                userId = targetId,
                username = friendUsername
            )

            whenever(friendRepository.getFriendRequests(userId)).thenReturn(DataResult.Success(userRequests))
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(
                DataResult.Failure(Exception("Simulated exception"))
            )

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "There was an error sending a request")
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
            val friendUsername = "fakeFriend"
            val targetId = "targetUserId"
            val userRequests = listOf(
                FriendRequestData(
                    requestId = "req123",
                    senderId = userId,
                    targetId = userId,
                    requestDate = "now",
                    isComplete = false
                )
            )

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(
                    UserData(
                        userId = userId,
                        username = friendUsername
                    )
                )
            )
            profileViewModel.userData.value = UserData(
                userId = userId,
                username = friendUsername
            )
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(DataResult.Success(
                listOf()
            ))
            whenever(friendRepository.getFriendRequests(userId)).thenReturn(DataResult.Success(userRequests))

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "You've already sent a request to $friendUsername!")
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
            val friendUsername = "fakeFriend"
            val targetId = "targetUserId"
            val userRequests = emptyList<FriendRequestData>()

            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(UserData(userId = userId))
            )
            profileViewModel.userData.value = UserData(
                userId = userId,
                username = friendUsername
            )
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(DataResult.Success(userRequests))
            whenever(friendRepository.getFriendRequests(userId)).thenReturn(DataResult.Success(userRequests))
            whenever(friendRepository.addFriendRequest(anyString(), anyString(), anyString())).thenReturn(DataResult.Success(true))

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "Friend request sent to $friendUsername!")
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
            val friendUsername = "fakeFriend"
            val targetId = "targetUserId"
            val userRequests = emptyList<FriendRequestData>()

            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(userId)
            whenever(userRepository.getUserByUsername(anyString())).thenReturn(
                DataResult.Success(UserData(userId = userId))
            )
            profileViewModel.userData.value = UserData(
                userId = userId,
                username = friendUsername
            )
            whenever(friendRepository.getFriendRequests(targetId)).thenReturn(DataResult.Success(userRequests))
            whenever(friendRepository.getFriendRequests(userId)).thenReturn(DataResult.Success(userRequests))

            val error = RuntimeException("Error adding friend request")
            whenever(friendRepository.addFriendRequest(anyString(), anyString(), anyString())).thenReturn(DataResult.Failure(error))

            // Act
            profileViewModel.sendFriendRequest()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "There was an error sending a request")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testRemoveFriendUserNotAuthenticated() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(null)

            // Act
            profileViewModel.removeFriend()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "User not authenticated")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testRemoveFriendUserNotFound() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)

            profileViewModel.userData.value = null
            whenever(auth.currentUser?.uid).thenReturn(userId)

            // Act
            profileViewModel.removeFriend()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "User not found")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testRemoveFriendSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)

            profileViewModel.userData.value = UserData(
                userId = userId,
                username = username
            )
            whenever(auth.currentUser?.uid).thenReturn(userId)

            whenever(friendRepository.removeFriend(anyString(), anyString())).thenReturn(
                DataResult.Success(true)
            )

            // Act
            profileViewModel.removeFriend()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "Removed fakeUsername as a friend!")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testRemoveFriendFailed() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)

            profileViewModel.userData.value = UserData(
                userId = userId,
                username = username
            )
            whenever(auth.currentUser?.uid).thenReturn(userId)

            whenever(friendRepository.removeFriend(anyString(), anyString())).thenReturn(
                DataResult.Failure(Exception("Simulated exception"))
            )

            // Act
            profileViewModel.removeFriend()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "Failed to remove user as a friend!")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testBlockUserNotAuthenticated() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            profileViewModel.notificationMsg.observeForever(strObserver)
            whenever(auth.currentUser?.uid).thenReturn(null)

            // Act
            profileViewModel.blockUser()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "User not authenticated")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testBlockUserNotFound() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)

            profileViewModel.userData.value = null
            whenever(auth.currentUser?.uid).thenReturn(userId)

            // Act
            profileViewModel.blockUser()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "User not found")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testBlockUserSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)

            profileViewModel.userData.value = UserData(
                userId = userId,
                username = username
            )
            whenever(auth.currentUser?.uid).thenReturn(userId)

            whenever(friendRepository.blockUser(anyString(), anyString(), anyBoolean())).thenReturn(
                DataResult.Success(true)
            )

            // Act
            profileViewModel.blockUser()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "Successfully blocked $username!")
        } finally {
            Dispatchers.resetMain()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testBlockUserFailed() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val userId = "fakeUserId"
            val username = "fakeUsername"

            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)

            profileViewModel.userData.value = UserData(
                userId = userId,
                username = username,
                friends = mapOf(username to true)
            )
            whenever(auth.currentUser?.uid).thenReturn(userId)

            whenever(friendRepository.blockUser(anyString(), anyString(), anyBoolean())).thenReturn(
                DataResult.Failure(Exception("Simulated exception"))
            )

            // Act
            profileViewModel.blockUser()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "Failed to block user")
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
            profileViewModel.userData.observeForever(observer)
            profileViewModel.notificationMsg.observeForever(strObserver)

            profileViewModel.notificationMsg.value = "Test123"

            // Act
            profileViewModel.clearMessage()

            // Assert
            assertEquals(profileViewModel.notificationMsg.value, "")
        } finally {
            Dispatchers.resetMain()
        }
    }
}