package com.tabka.backblogapp

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.tabka.backblogapp.network.repository.FriendRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class FriendRepositoryTest {

    @Mock
    private lateinit var mockDb: FirebaseFirestore

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockDocument: DocumentReference

    @Mock
    private lateinit var mockQuery: Query

    @Mock
    private lateinit var mockDocumentSnapshot: DocumentSnapshot

    @Mock
    private lateinit var mockQuerySnapshot: QuerySnapshot

    private lateinit var friendRepo: FriendRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        friendRepo = FriendRepository(mockDb, mockAuth)
    }

    @Test
    fun testAddLogRequestShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val senderId = "sender123"
        val targetId = "target456"
        val logId = "log789"
        val requestDate = "2024-02-29"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("req123")

        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = friendRepo.addLogRequest(senderId, targetId, logId, requestDate)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item, true)
    }

    @Test
    fun testAddLogRequestShouldReturnException(): Unit = runBlocking {
        // Arrange
        val senderId = "sender123"
        val targetId = "target456"
        val logId = "log789"
        val requestDate = "2024-02-29"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("req123")

        val exception = Exception("Simulated exception")
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = friendRepo.addLogRequest(senderId, targetId, logId, requestDate)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testAddFriendRequestShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val senderId = "sender123"
        val targetId = "target456"
        val requestDate = "2024-02-29"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("req123")

        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = friendRepo.addFriendRequest(senderId, targetId, requestDate)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item, true)
    }

    @Test
    fun testAddFriendRequestShouldReturnException(): Unit = runBlocking {
        // Arrange
        val senderId = "sender123"
        val targetId = "target456"
        val requestDate = "2024-02-29"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("req123")

        val exception = Exception("Simulated exception")
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = friendRepo.addFriendRequest(senderId, targetId, requestDate)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testGetFriendRequestsShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forResult(mockQuerySnapshot)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)
        whenever(mockQuerySnapshot.documents).thenReturn(emptyList())

        // Act
        val result = friendRepo.getFriendRequests(userId)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.size, 0)
    }

    @Test
    fun testGetFriendRequestsShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val exception = Exception("Simulated exception")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forException(exception)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)

        // Act
        val result = friendRepo.getFriendRequests(userId)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.size, 0)
    }

    @Test
    fun testGetLogRequestsShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forResult(mockQuerySnapshot)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)
        whenever(mockQuerySnapshot.documents).thenReturn(emptyList())

        // Act
        val result = friendRepo.getLogRequests(userId)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.size, 0)
    }

    @Test
    fun testGetLogRequestsShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val exception = Exception("Simulated exception")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forException(exception)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)

        // Act
        val result = friendRepo.getLogRequests(userId)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.size, 0)
    }

    @Test
    fun testGetFriendsShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val emptyMap = mapOf<String, Any>()

        val userDocument = mockDocumentSnapshot
        whenever(userDocument.data).thenReturn(emptyMap)
        val taskUserDocument: Task<DocumentSnapshot> = Tasks.forResult(userDocument)
        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(userId)).thenReturn(mockDocument)
        whenever(mockDocument.get()).thenReturn(taskUserDocument)

        val friendsMap = mutableMapOf<String, Boolean>()
        val friendsData = mockDocumentSnapshot
        whenever(friendsData.data).thenReturn(friendsMap as Map<String, Any>?)
        val taskFriendsData: Task<DocumentSnapshot> = Tasks.forResult(friendsData)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.get()).thenReturn(taskFriendsData)

        // Act
        val result = friendRepo.getFriends(userId)

        // Assert
        assert(result.isEmpty())
    }

    @Test
    fun testGetFriendsShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val exception = Exception("Simulated exception")

        val taskUserDocument: Task<DocumentSnapshot> = Tasks.forException(exception)
        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(userId)).thenReturn(mockDocument)
        whenever(mockDocument.get()).thenReturn(taskUserDocument)

        // Act & Assert
        try {
            friendRepo.getFriends(userId)
            // FAILED
            Assert.fail()
        } catch (e: Exception) {
            assertNotNull(e)
        }
    }

    @Test
    fun testUpdateFriendRequestShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val friendRequestId = "friendReq123"
        val isAccepted = true

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val updateTask: Task<Void> = Tasks.forResult(null)
        val getTask: Task<DocumentSnapshot> = Tasks.forResult(mockDocumentSnapshot)
        whenever(mockDocument.get()).thenReturn(getTask)
        whenever(mockDocument.update(anyMap())).thenReturn(updateTask)
        whenever(mockDocumentSnapshot.exists()).thenReturn(true)
        whenever(mockDocument.update(anyString(), any())).thenReturn(updateTask)

        // Act
        val result = friendRepo.updateFriendRequest(friendRequestId, isAccepted)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item, true)
    }

    @Test
    fun testUpdateFriendRequestShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val friendRequestId = "friendReq123"
        val isAccepted = true
        val exception = Exception("Simulated exception")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = friendRepo.updateFriendRequest(friendRequestId, isAccepted)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testUpdateLogRequestShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val logRequestId = "logReq123"
        val isAccepted = true

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        val getTask: Task<DocumentSnapshot> = Tasks.forResult(mockDocumentSnapshot)
        whenever(mockDocument.get()).thenReturn(getTask)
        whenever(mockDocumentSnapshot.exists()).thenReturn(true)

        whenever(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
        whenever(mockQuerySnapshot.documents).thenReturn(emptyList())

        // Act
        val result = friendRepo.updateLogRequest(logRequestId, isAccepted)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item, true)
    }

    @Test
    fun testUpdateLogRequestShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val logRequestId = "logReq123"
        val isAccepted = true
        val exception = Exception("Simulated exception")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = friendRepo.updateLogRequest(logRequestId, isAccepted)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testRemoveFriendShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val friendId = "friend456"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = friendRepo.removeFriend(userId, friendId)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item, true)
    }

    @Test
    fun testRemoveFriendShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val friendId = "friend456"
        val exception = Exception("Simulated exception")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = friendRepo.removeFriend(userId, friendId)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testBlockUserShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val blockedId = "blocked456"
        val isFriend = true

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = friendRepo.blockUser(userId, blockedId, isFriend)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item, true)
    }

    @Test
    fun testBlockUserShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val blockedId = "blocked456"
        val isFriend = true
        val exception = Exception("Simulated exception")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = friendRepo.blockUser(userId, blockedId, isFriend)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testAddFriendToUserShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val friendId = "friend456"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyString(), any())).thenReturn(task)

        // Act
        val result = friendRepo.addFriendToUser(userId, friendId)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item, true)
    }

    @Test
    fun testAddFriendToUserShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val friendId = "friend456"
        val exception = Exception("Simulated exception")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.update(anyString(), any())).thenReturn(task)

        // Act
        val result = friendRepo.addFriendToUser(userId, friendId)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testAddCollaboratorShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val logId = "log456"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.update(anyMap())).thenReturn(Tasks.forResult(null))

        whenever(mockDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
        whenever(mockDocumentSnapshot.exists()).thenReturn(true)

        whenever(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
        whenever(mockQuerySnapshot.documents).thenReturn(emptyList())

        // Act
        val result = friendRepo.addCollaborator(userId, logId)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item, true)
    }

    @Test
    fun testAddCollaboratorShouldReturnFailure(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val logId = "log456"
        val exception = Exception("Simulated exception")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.update(anyMap())).thenReturn(Tasks.forResult(null))

        val taskDocument: Task<DocumentSnapshot> = Tasks.forException(exception)
        whenever(mockDocument.get()).thenReturn(taskDocument)

        // Get Logs
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forException(exception)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)
        whenever(mockQuerySnapshot.documents).thenReturn(mutableListOf(mockDocumentSnapshot))
        whenever(mockDocumentSnapshot.data).thenReturn(mapOf())


        // Act
        val result = friendRepo.addCollaborator(userId, logId)

        // Assert
        assert(result is DataResult.Failure)
    }
}