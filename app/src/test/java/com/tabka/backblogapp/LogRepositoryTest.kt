package com.tabka.backblogapp

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class LogRepositoryTest {

    @Mock
    private lateinit var mockDb: FirebaseFirestore

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockDocument: DocumentReference

    private lateinit var logRepository: LogRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        logRepository = LogRepository(mockDb)
    }

    @Test
    fun testAddLogShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val name = "My Log 123"
        val ownerId = "Bob123"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = logRepository.addLog(name, false, ownerId)

        // Assert
        assert(result is DataResult.Success)
        verify(mockDocument).set(anyMap<String, Any>())
    }

    @Test
    fun testAddLogShouldReturnException(): Unit = runBlocking {
        // Arrange
        val name = "My Log 123"
        val ownerId = "Bob123"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        // Simulate exception
        val exception = Exception("Simulated exception")
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = logRepository.addLog(name, false, ownerId)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
        verify(mockDocument).set(anyMap<String, Any>())
    }
/*
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
    }*/
}