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
import com.tabka.backblogapp.network.models.UserData
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.FirebaseError
import com.tabka.backblogapp.util.FirebaseExceptionType
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class UserRepositoryTest {

    @Mock
    private lateinit var mockDb: FirebaseFirestore

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockDocument: DocumentReference

    @Mock
    private lateinit var mockDocumentSnapshot: DocumentSnapshot

    @Mock
    private lateinit var mockQuery: Query

    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        userRepository = UserRepository(mockDb, mockAuth)
    }

    @Test
    fun testAddUserShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "fakeUserId"
        val username = "FakeUser"
        val avatarPreset = 1

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = userRepository.addUser(userId, username, avatarPreset)

        // Assert
        assert(result is DataResult.Success)
        verify(mockDocument).set(anyMap<String, Any>())
    }

    @Test
    fun testAddUserShouldReturnException(): Unit = runBlocking {
        // Arrange
        val userId = "fakeUserId"
        val username = "FakeUser"
        val avatarPreset = 1

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        // Simulate exception
        val exception = Exception("Simulated exception")
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = userRepository.addUser(userId, username, avatarPreset)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
        verify(mockDocument).set(anyMap<String, Any>())
    }

    @Test
    fun testGetUserShouldReturnSuccess() = runBlocking {
        // Arrange
        val userId = "fakeUserId"
        val joinDate = System.currentTimeMillis().toString()
        val userData = UserData(userId, "FakeUser", joinDate, 1, emptyMap(), emptyMap())
        val userDataMap = mapOf(
            "user_id" to userId,
            "username" to "FakeUser",
            "join_date" to joinDate,
            "avatar_preset" to 1,
            "friends" to emptyMap<String, Boolean>(),
            "blocked" to emptyMap<String, Boolean>()
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val mockDocumentSnapshot: DocumentSnapshot = mock(DocumentSnapshot::class.java)

        whenever(mockDocumentSnapshot.exists()).thenReturn(true)
        whenever(mockDocumentSnapshot.data).thenReturn(userDataMap)

        val task: Task<DocumentSnapshot> = Tasks.forResult(mockDocumentSnapshot)
        whenever(mockDocument.get()).thenReturn(task)

        // Act
        val result = userRepository.getUser(userId)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item == userData)
    }

    @Test
    fun testGetUserShouldReturnNotFound() = runBlocking {
        // Arrange
        val userId = "nonExistentUserId"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val mockDocumentSnapshot: DocumentSnapshot = mock(DocumentSnapshot::class.java)

        // Simulate the scenario where the document does not exist
        whenever(mockDocumentSnapshot.exists()).thenReturn(false)

        val task: Task<DocumentSnapshot> = Tasks.forResult(mockDocumentSnapshot)
        whenever(mockDocument.get()).thenReturn(task)

        // Act
        val result = userRepository.getUser(userId)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable.toString() == FirebaseError(FirebaseExceptionType.NOT_FOUND).toString())
    }

    @Test
    fun testGetUserShouldReturnException() = runBlocking {
        // Arrange
        val userId = "fakeUserId"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        // Simulate exception
        val exception = Exception("Simulated exception")
        val task: Task<DocumentSnapshot> = Tasks.forException(exception)
        whenever(mockDocument.get()).thenReturn(task)

        // Act
        val result = userRepository.getUser(userId)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testGetUserByUsernameShouldReturnSuccess() = runBlocking {
        // Arrange
        val username = "FakeUser"
        val joinDate = System.currentTimeMillis().toString()
        val userData = UserData("fakeUserId", username, joinDate, 1, emptyMap(), emptyMap())
        val userDataMap = mapOf(
            "user_id" to "fakeUserId",
            "username" to username,
            "join_date" to joinDate,
            "avatar_preset" to 1,
            "friends" to emptyMap<String, Boolean>(),
            "blocked" to emptyMap<String, Boolean>()
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)

        val querySnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)
        val task: Task<QuerySnapshot> = Tasks.forResult(querySnapshot)
        whenever(mockQuery.get()).thenReturn(task)

        whenever(task.result.isEmpty).thenReturn(false)

        val expectedDocumentSnapshots = listOf(
            mockDocumentSnapshot, mockDocumentSnapshot, mockDocumentSnapshot
        )
        whenever(task.result.documents).thenReturn(expectedDocumentSnapshots)
        whenever(mockDocumentSnapshot.data).thenReturn(userDataMap)

        // Act
        val result = userRepository.getUserByUsername(username)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item == userData)
    }

    @Test
    fun testGetUserByUsernameShouldReturnException() = runBlocking {
        // Arrange
        val username = "fakeUser"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)

        val exception = Exception("Simulated exception")
        val task: Task<QuerySnapshot> = Tasks.forException(exception)
        whenever(mockQuery.get()).thenReturn(task)

        // Act
        val result = userRepository.getUserByUsername(username)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testGetUserByUsernameShouldReturnNotFound() = runBlocking {
        // Arrange
        val username = "FakeUser"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)

        val querySnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)
        val task: Task<QuerySnapshot> = Tasks.forResult(querySnapshot)
        whenever(mockQuery.get()).thenReturn(task)

        whenever(task.result.isEmpty).thenReturn(true)

        // Act
        val result = userRepository.getUserByUsername(username)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable.toString() == FirebaseError(FirebaseExceptionType.NOT_FOUND).toString())
    }

    @Test
    fun testUpdateUserShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "fakeUserId"
        val updateData = mapOf(
            "username" to "NewUsername",
            "avatar_preset" to 2,
            "friends" to mapOf("friendId" to true),
            "blocked" to mapOf("blockedId" to true),
            "password" to "newPassword"
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val updateDataTask: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyMap())).thenReturn(updateDataTask)

        val updateUserTask: Task<Void> = Tasks.forResult(null)
        whenever(mockAuth.currentUser).thenReturn(mock())
        whenever(mockAuth.currentUser!!.updatePassword(anyString())).thenReturn(updateUserTask)

        // Act
        val result = userRepository.updateUser(userId, updateData)

        // Assert
        assert(result is DataResult.Success)
        verify(mockDocument).update(anyMap())
        verify(mockAuth.currentUser!!).updatePassword("newPassword")
    }

    @Test
    fun testUpdateUserWithoutUpdateShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "fakeUserId"
        val updateData = mapOf("password" to "newPassword")

        val updateUserTask: Task<Void> = Tasks.forResult(null)
        whenever(mockAuth.currentUser).thenReturn(mock())
        whenever(mockAuth.currentUser!!.updatePassword(anyString())).thenReturn(updateUserTask)

        // Act
        val result = userRepository.updateUser(userId, updateData)

        // Assert
        assert(result is DataResult.Success)
        verifyNoInteractions(mockDocument)
    }

    @Test
    fun testUpdateUserShouldReturnException(): Unit = runBlocking {
        // Arrange
        val userId = "fakeUserId"
        val updateData = mapOf("username" to "NewUsername")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        // Simulate exception
        val exception = Exception("Simulated exception")
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.update(anyMap())).thenReturn(task)

        // Act
        val result = userRepository.updateUser(userId, updateData)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable.toString() == FirebaseError(FirebaseExceptionType.FAILED_TRANSACTION).toString())
        verify(mockDocument).update(anyMap())
    }
}