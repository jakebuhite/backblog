package com.tabka.backblogapp

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.tabka.backblogapp.network.models.FriendRequestData
import com.tabka.backblogapp.network.models.LogRequestData
import com.tabka.backblogapp.network.repository.FriendRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class FriendRepositoryTest {

    @Mock
    private lateinit var db: FirebaseFirestore

    @Mock
    private lateinit var colRef: CollectionReference

    @Mock
    private lateinit var docRef: DocumentReference

    private lateinit var friendRepo: FriendRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        friendRepo = FriendRepository(db)
    }

    @Test
    fun `addFriendRequest should succeed and return Result success`() = runBlocking {
        // Arrange
        val senderId = "sender123"
        val targetId = "target456"
        val requestDate = System.currentTimeMillis().toString()
        val documentReference = mock<DocumentReference>()
        val task: Task<DocumentReference> = Tasks.forResult(documentReference)

        whenever(db.collection(any())).thenReturn(colRef)
        whenever(colRef.add(FriendRequestData(
            senderId = senderId,
            targetId = targetId,
            requestDate = requestDate,
            isComplete = false
        ))).thenReturn(task)

        // Act
        val result = friendRepo.addFriendRequest(senderId, targetId, requestDate)

        // Assert
        assertTrue(task.isSuccessful)
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `addFriendRequest should fail and return Result failure on exception`() = runBlocking {
        // Arrange
        val senderId = "sender123"
        val targetId = "target456"
        val requestDate = System.currentTimeMillis().toString()
        val exception = mock<Exception>()
        val task: Task<DocumentReference> = Tasks.forException(exception)

        whenever(db.collection(any())).thenReturn(colRef)
        whenever(colRef.add(FriendRequestData(
            senderId = senderId,
            targetId = targetId,
            requestDate = requestDate,
            isComplete = false
        ))).thenReturn(task)

        // Act
        val result = friendRepo.addFriendRequest(senderId, targetId, requestDate)

        // Assert
        assertFalse(task.isSuccessful)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `addLogRequest should succeed and return Result success`() = runBlocking {
        // Arrange
        val senderId = "sender123"
        val targetId = "target456"
        val logId = "dlfkslij342io432o"
        val requestDate = System.currentTimeMillis().toString()

        val documentReference = mock<DocumentReference>()
        val task: Task<DocumentReference> = Tasks.forResult(documentReference)

        whenever(db.collection(any())).thenReturn(colRef)
        whenever(colRef.add(LogRequestData(
            sender_id = senderId,
            target_id = targetId,
            logId = logId,
            request_date = requestDate,
            is_complete = false
        )
        )).thenReturn(task)

        // Act
        val result = friendRepo.addLogRequest(senderId, targetId, logId, requestDate)

        // Assert
        assertTrue(task.isSuccessful)
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `addLogRequest should fail and return Result failure on exception`() = runBlocking {
        // Arrange
        val senderId = "sender123"
        val targetId = "target456"
        val logId = "dlfkslij342io432o"
        val requestDate = System.currentTimeMillis().toString()

        val exception = mock<Exception>()
        val task: Task<DocumentReference> = Tasks.forException(exception)

        whenever(db.collection(any())).thenReturn(colRef)
        whenever(colRef.add(LogRequestData(
            sender_id = senderId,
            target_id = targetId,
            logId = logId,
            request_date = requestDate,
            is_complete = false
        ))).thenReturn(task)

        // Act
        val result = friendRepo.addLogRequest(senderId, targetId, logId, requestDate)

        // Assert
        assertFalse(task.isSuccessful)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getFriends should return list of friend user data`() = runBlocking {
        // Arrange
        val userDoc = mock<DocumentSnapshot>()
        val friendDocRef = mock<DocumentReference>()

        val friendDoc1 = mock<DocumentSnapshot>()
        val friendDoc2 = mock<DocumentSnapshot>()

        whenever(db.collection("users")).thenReturn(colRef)
        whenever(colRef.document("john123")).thenReturn(docRef)
        whenever(docRef.get()).thenReturn(Tasks.forResult(userDoc))
        whenever(userDoc.data).thenReturn(
            mapOf(
                "friends" to mapOf(
                    "friend1" to true,
                    "friend2" to true
                )
            )
        )

        // Get (Mock) friend1 document retrieval
        whenever(colRef.document("friend1")).thenReturn(friendDocRef)
        whenever(friendDocRef.get()).thenReturn(Tasks.forResult(friendDoc1))
        whenever(friendDoc1.id).thenReturn("friend1")
        whenever(friendDoc1.getString("name")).thenReturn("Friend One")

        // Get (Mock) friend2 document
        whenever(colRef.document("friend2")).thenReturn(friendDocRef)
        whenever(friendDocRef.get()).thenReturn(Tasks.forResult(friendDoc2))
        whenever(friendDoc2.id).thenReturn("friend2")
        whenever(friendDoc2.getString("name")).thenReturn("Friend Two")

        // Act
        val result = friendRepo.getFriends("john123")

        // Assert
        assertTrue(result[0].user_id == "friend1" || result[0].user_id == "friend2")
        assertEquals(2, result.size)
    }

}