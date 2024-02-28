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
import com.tabka.backblogapp.network.models.LogData
import com.tabka.backblogapp.network.models.Owner
import com.tabka.backblogapp.network.repository.LogRepository
import com.tabka.backblogapp.util.DataResult
import com.tabka.backblogapp.util.FirebaseError
import com.tabka.backblogapp.util.FirebaseExceptionType
import com.tabka.backblogapp.util.toJsonElement
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class LogRepositoryTest {

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

    private lateinit var logRepository: LogRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        logRepository = LogRepository(mockDb, mockAuth)
    }

    @Test
    fun testAddLogShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val name = "My Log 123"
        val ownerId = "Bob123"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("doc123")

        // Get Logs mock
        whenever(mockCollection.whereArrayContains(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forResult(mockQuerySnapshot)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)

        whenever(mockQuerySnapshot.documents).thenReturn(mutableListOf())

        whenever(mockQuerySnapshot.documents.map<DocumentSnapshot, LogData> { doc ->
            Json.decodeFromString(Json.encodeToString(doc.data.toJsonElement()))
        }).thenReturn(emptyList())

        // Back to Add Logs
        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = logRepository.addLog(name, false, ownerId)

        // Assert
        assert(result is DataResult.Success)
    }

    @Test
    fun testAddLogShouldReturnException(): Unit = runBlocking {
        // Arrange
        val name = "My Log 123"
        val ownerId = "Bob123"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("doc123")

        // Get Logs mock
        whenever(mockCollection.whereArrayContains(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forResult(mockQuerySnapshot)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)

        whenever(mockQuerySnapshot.documents).thenReturn(mutableListOf())

        whenever(mockQuerySnapshot.documents.map<DocumentSnapshot, LogData> { doc ->
            Json.decodeFromString(Json.encodeToString(doc.data.toJsonElement()))
        }).thenReturn(emptyList())

        val exception = Exception("Simulated exception")
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = logRepository.addLog(name, false, ownerId)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testAddLogSyncShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val name = "My Log 123"
        val ownerId = "Bob123"
        val creationDate = System.currentTimeMillis().toString()

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("doc123")

        val task: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = logRepository.addLog(name, ownerId, 0, creationDate, mutableListOf(), mutableListOf())

        // Assert
        assert(result is DataResult.Success)
    }

    @Test
    fun testAddLogSyncShouldReturnException(): Unit = runBlocking {
        // Arrange
        val name = "My Log 123"
        val ownerId = "Bob123"
        val creationDate = System.currentTimeMillis().toString()

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("doc123")

        val exception = Exception("Simulated exception")
        val task: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.set(anyMap<String, Any>())).thenReturn(task)

        // Act
        val result = logRepository.addLog(name, ownerId, 0, creationDate, mutableListOf(), mutableListOf())

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testGetLogExistShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val owner = Owner(
            userId = "user123",
            priority = 0
        )

        val logData = LogData(
            logId = "log123",
            name = "My log",
            creationDate = "34345564654",
            lastModifiedDate = "3244365690",
            isVisible = true,
            owner = owner,
            collaborators = mutableListOf(),
            order = emptyMap(),
            movieIds = mutableListOf(),
            watchedIds = mutableListOf()
        )

        val logDataMap = mapOf(
            "log_id" to "log123",
            "name" to "My log",
            "creation_date" to "34345564654",
            "last_modified_date" to "3244365690",
            "is_visible" to true,
            "owner" to null,
            "collaborators" to mutableListOf<String>(),
            "order" to emptyMap<String, Int>(),
            "movie_ids" to mutableListOf<String>(),
            "watched_ids" to mutableListOf<String>()
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val task: Task<DocumentSnapshot> = Tasks.forResult(mockDocumentSnapshot)
        whenever(mockDocument.get()).thenReturn(task)

        whenever(task.result.exists()).thenReturn(true)

        whenever(mockDocumentSnapshot.data).thenReturn(logDataMap)

        // Act
        val result = logRepository.getLog(logData.logId!!)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.logId, logData.logId)
    }

    @Test
    fun testGetLogNotExistShouldReturnException(): Unit = runBlocking {
        // Arrange
        val logId = "log123"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val task: Task<DocumentSnapshot> = Tasks.forResult(mockDocumentSnapshot)
        whenever(mockDocument.get()).thenReturn(task)

        whenever(task.result.exists()).thenReturn(false)

        // Act
        val result = logRepository.getLog(logId)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable.toString() == FirebaseError(FirebaseExceptionType.NOT_FOUND).toString())
    }

    @Test
    fun testGetLogShouldReturnException(): Unit = runBlocking {
        // Arrange
        val logId = "log123"

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        val exception = Exception("Simulated exception")
        val task: Task<DocumentSnapshot> = Tasks.forException(exception)
        whenever(mockDocument.get()).thenReturn(task)

        // Act
        val result = logRepository.getLog(logId)

        // Assert
        assert(result is DataResult.Failure)
        assert((result as DataResult.Failure).throwable == exception)
    }

    @Test
    fun testGetLogsForUserOwnedPrivateShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val private = true
        val logData = LogData(
            logId = "log123",
            name = "My log",
            collaborators = mutableListOf(userId),
            creationDate = "3453454534",
            lastModifiedDate = "43545654654",
            isVisible = true,
            owner = Owner(userId = userId, priority = 0),
            order = emptyMap(),
            movieIds = mutableListOf(),
            watchedIds = mutableListOf()
        )
        val logDataMap = mapOf(
            "log_id" to "log123",
            "name" to "My log",
            "collaborators" to mutableListOf(userId),
            "creation_date" to "3453454534",
            "last_modified_date" to "43545654654",
            "is_visible" to true,
            "owner" to mapOf("user_id" to userId, "priority" to 0),
            "order" to emptyMap<String, Int>(),
            "movie_ids" to mutableListOf<String>(),
            "watched_ids" to mutableListOf<String>()
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forResult(mockQuerySnapshot)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)
        whenever(mockQuerySnapshot.documents).thenReturn(mutableListOf(mockDocumentSnapshot))
        whenever(mockDocumentSnapshot.data).thenReturn(logDataMap)

        // Act
        val result = logRepository.getLogs(userId, private)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.first(), logData)
    }

    @Test
    fun testGetLogsForUserOwnedPublicShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val private = false
        val logData = LogData(
            logId = "log123",
            name = "My log",
            collaborators = mutableListOf(userId),
            creationDate = "3453454534",
            lastModifiedDate = "43545654654",
            isVisible = true,
            owner = Owner(userId = userId, priority = 0),
            order = emptyMap(),
            movieIds = mutableListOf(),
            watchedIds = mutableListOf()
        )
        val logDataMap = mapOf(
            "log_id" to "log123",
            "name" to "My log",
            "collaborators" to mutableListOf(userId),
            "creation_date" to "3453454534",
            "last_modified_date" to "43545654654",
            "is_visible" to true,
            "owner" to mapOf("user_id" to userId, "priority" to 0),
            "order" to emptyMap<String, Int>(),
            "movie_ids" to mutableListOf<String>(),
            "watched_ids" to mutableListOf<String>()
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereArrayContains(anyString(), any())).thenReturn(mockQuery)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forResult(mockQuerySnapshot)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)
        whenever(mockQuerySnapshot.documents).thenReturn(mutableListOf(mockDocumentSnapshot))
        whenever(mockDocumentSnapshot.data).thenReturn(logDataMap)

        // Act
        val result = logRepository.getLogs(userId, private)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.first(), logData)
    }

    @Test
    fun testGetLogsForCollaboratorPrivateShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val private = true
        val logData = LogData(
            logId = "log123",
            name = "My log",
            collaborators = mutableListOf(userId),
            creationDate = "3453454534",
            lastModifiedDate = "43545654654",
            isVisible = false,
            owner = Owner(userId = userId, priority = 0),
            order = emptyMap(),
            movieIds = mutableListOf(),
            watchedIds = mutableListOf()
        )
        val logDataMap = mapOf(
            "log_id" to "log123",
            "name" to "My log",
            "collaborators" to mutableListOf(userId),
            "creation_date" to "3453454534",
            "last_modified_date" to "43545654654",
            "is_visible" to false,
            "owner" to mapOf("user_id" to userId, "priority" to 0),
            "order" to emptyMap<String, Int>(),
            "movie_ids" to mutableListOf<String>(),
            "watched_ids" to mutableListOf<String>()
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forResult(mockQuerySnapshot)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)
        whenever(mockQuerySnapshot.documents).thenReturn(mutableListOf(mockDocumentSnapshot))
        whenever(mockDocumentSnapshot.data).thenReturn(logDataMap)

        // Act
        val result = logRepository.getLogs(userId, private)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.first(), logData)
    }

    @Test
    fun testGetLogsForCollaboratorPublicShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val private = false
        val logData = LogData(
            logId = "log123",
            name = "My log",
            collaborators = mutableListOf(userId),
            creationDate = "3453454534",
            lastModifiedDate = "43545654654",
            isVisible = true,
            owner = Owner(userId = "anotherUser123", priority = 0),
            order = emptyMap(),
            movieIds = mutableListOf(),
            watchedIds = mutableListOf()
        )
        val logDataMap = mapOf(
            "log_id" to "log123",
            "name" to "My log",
            "collaborators" to mutableListOf(userId),
            "creation_date" to "3453454534",
            "last_modified_date" to "43545654654",
            "is_visible" to true,
            "owner" to mapOf("user_id" to "anotherUser123", "priority" to 0),
            "order" to emptyMap<String, Int>(),
            "movie_ids" to mutableListOf<String>(),
            "watched_ids" to mutableListOf<String>()
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        whenever(mockCollection.whereArrayContains(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forResult(mockQuerySnapshot)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)
        whenever(mockQuerySnapshot.documents).thenReturn(mutableListOf(mockDocumentSnapshot))
        whenever(mockDocumentSnapshot.data).thenReturn(logDataMap)

        // Act
        val result = logRepository.getLogs(userId, private)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.first(), logData)
    }

    @Test
    fun testGetLogsShouldReturnEmptyListWhenNoLogsFound(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val private = false

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val taskQuerySnapshot: Task<QuerySnapshot> = Tasks.forResult(mockQuerySnapshot)
        whenever(mockQuery.get()).thenReturn(taskQuerySnapshot)
        whenever(mockQuerySnapshot.documents).thenReturn(emptyList())

        // Act
        val result = logRepository.getLogs(userId, private)

        // Assert
        assert(result is DataResult.Success)
        assertEquals((result as DataResult.Success).item.size, 0)
    }

    @Test
    fun testGetLogsShouldReturnFailureOnException(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val private = false
        val exception = Exception("Simulated exception")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.whereEqualTo(anyString(), any())).thenReturn(mockQuery)
        val task: Task<QuerySnapshot> = Tasks.forException(exception)
        whenever(mockQuery.get()).thenReturn(task)

        // Act
        val result = logRepository.getLogs(userId, private)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item.isEmpty())
    }

    @Test
    fun testUpdateLogShouldReturnSuccess(): Unit = runBlocking {
        // Arrange
        val logId = "log123"
        val updateData = mapOf(
            "name" to "Updated Log Name",
            "is_visible" to true,
            "movie_ids" to listOf("movie123", "movie456"),
            "watched_ids" to listOf("watched123", "watched456")
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(logId)).thenReturn(mockDocument)
        whenever(mockDocument.update(anyMap())).thenReturn(Tasks.forResult(null))

        // Act
        val result = logRepository.updateLog(logId, updateData)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item)
    }

    @Test
    fun testUpdateLogShouldReturnSuccessNoFieldsUpdated(): Unit = runBlocking {
        // Arrange
        val logId = "log123"
        val updateData = mapOf<String, Any?>()

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(logId)).thenReturn(mockDocument)
        whenever(mockDocument.update(anyMap())).thenReturn(Tasks.forResult(null))

        // Act
        val result = logRepository.updateLog(logId, updateData)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item)
    }

    @Test
    fun testUpdateLogShouldReturnFailureInvalidLogId() = runBlocking {
        // Arrange
        val logId = ""
        val updateData = mapOf(
            "name" to "Updated Log Name"
        )

        // Act
        val result = logRepository.updateLog(logId, updateData)

        // Assert
        assert(result is DataResult.Failure)
        assertEquals(FirebaseError(FirebaseExceptionType.FAILED_TRANSACTION).toString(),
            (result as DataResult.Failure).throwable.toString())
    }

    @Test
    fun testUpdateLogShouldReturnFailureFailedFirestoreUpdate() = runBlocking {
        // Arrange
        val logId = "log123"
        val updateData = mapOf(
            "name" to "Updated Log Name"
        )

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(logId)).thenReturn(mockDocument)
        val exception = Exception("Simulated exception")
        whenever(mockDocument.update(anyMap())).thenReturn(Tasks.forException(exception))

        // Act
        val result = logRepository.updateLog(logId, updateData)

        // Assert
        assert(result is DataResult.Failure)
        assertEquals(
            FirebaseError(FirebaseExceptionType.FAILED_TRANSACTION).toString(),
            (result as DataResult.Failure).throwable.toString()
        )
    }

    @Test
    fun testDeleteLogSuccess(): Unit = runBlocking {
        // Arrange
        val logId = "log123"

        val deleteTask: Task<Void> = Tasks.forResult(null)
        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(logId)).thenReturn(mockDocument)
        whenever(mockDocument.delete()).thenReturn(deleteTask)

        // Act
        val result = logRepository.deleteLog(logId)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item)
    }

    @Test
    fun testDeleteLogNoLogFound(): Unit = runBlocking {
        // Arrange
        val logId = "log123"

        val deleteTask: Task<Void> = Tasks.forException(Exception("Document does not exist"))
        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(logId)).thenReturn(mockDocument)
        whenever(mockDocument.delete()).thenReturn(deleteTask)

        // Act
        val result = logRepository.deleteLog(logId)

        // Assert
        assert(result is DataResult.Failure)
        assertEquals("java.lang.Exception: Document does not exist", (result as DataResult.Failure).throwable.toString())
    }

    @Test
    fun testDeleteLogFirestoreException(): Unit = runBlocking {
        // Arrange
        val logId = "log123"

        val exception = Exception("Simulated exception")
        val deleteTask: Task<Void> = Tasks.forException(exception)
        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(logId)).thenReturn(mockDocument)
        whenever(mockDocument.delete()).thenReturn(deleteTask)

        // Act
        val result = logRepository.deleteLog(logId)

        // Assert
        assert(result is DataResult.Failure)
        assertEquals("java.lang.Exception: Simulated exception", (result as DataResult.Failure).throwable.toString())
    }

    @Test
    fun testUpdateUserLogOrderSuccess(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val logIds = listOf(
            Pair("log1", true),
            Pair("log2", false),
            Pair("log3", true)
        )

        logIds.mapIndexed { index, log ->
            val priorityField = if (log.second) "owner.priority" else "order.$userId"
            val updateTask: Task<Void> = Tasks.forResult(null)
            whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
            whenever(mockCollection.document(log.first)).thenReturn(mockDocument)
            whenever(mockDocument.update(priorityField, index)).thenReturn(updateTask)
            updateTask
        }

        // Act
        val result = logRepository.updateUserLogOrder(userId, logIds)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item)
    }

    @Test
    fun testUpdateUserLogOrderEmptyList(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val logIds = emptyList<Pair<String, Boolean>>()

        // Act
        val result = logRepository.updateUserLogOrder(userId, logIds)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item)
    }

    @Test
    fun testUpdateUserLogOrderFirestoreException(): Unit = runBlocking {
        // Arrange
        val userId = "user123"
        val logIds = listOf(
            Pair("log1", true),
            Pair("log2", false),
            Pair("log3", true)
        )

        val exception = Exception("Firestore update failed")
        logIds.mapIndexed { index, log ->
            val priorityField = if (log.second) "owner.priority" else "order.$userId"
            val updateTask: Task<Void> = Tasks.forException(exception)
            whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
            whenever(mockCollection.document(log.first)).thenReturn(mockDocument)
            whenever(mockDocument.update(priorityField, index)).thenReturn(updateTask)
            updateTask
        }

        // Act
        val result = logRepository.updateUserLogOrder(userId, logIds)

        // Assert
        assert(result is DataResult.Failure)
        assertEquals("java.lang.Exception: Firestore update failed", (result as DataResult.Failure).throwable.toString())
    }

    @Test
    fun testAddCollaboratorsSuccess(): Unit = runBlocking {
        // Arrange
        val logId = "log123"
        val collaborators = listOf("user1", "user2", "user3")

        whenever(mockAuth.currentUser).thenReturn(mock())
        whenever(mockAuth.currentUser?.uid).thenReturn("currentUserId")

        val task: Task<Void> = Tasks.forResult(null)

        whenever(mockDb.collection(any())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("fakeLog123")
        whenever(mockDocument.set(any())).thenReturn(task)

        // Act
        val result = logRepository.addCollaborators(logId, collaborators)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item)
    }

    @Test
    fun testAddCollaboratorsNoCurrentUser(): Unit = runBlocking {
        // Arrange
        val logId = "log123"
        val collaborators = listOf("user1", "user2", "user3")

        whenever(mockAuth.currentUser).thenReturn(null)

        // Act
        val result = logRepository.addCollaborators(logId, collaborators)

        // Assert
        assert(result is DataResult.Failure)
        assertEquals(FirebaseError(FirebaseExceptionType.FAILED_TRANSACTION).toString(), (result as DataResult.Failure).throwable.toString())
    }

    @Test
    fun testAddCollaboratorsFailure(): Unit = runBlocking {
        // Arrange
        val logId = "log123"
        val collaborators = listOf("user1", "user2", "user3")

        whenever(mockAuth.currentUser).thenReturn(mock())
        whenever(mockAuth.currentUser?.uid).thenReturn("currentUserId")

        val exception = Exception("Firestore update failed")
        val updateTask: Task<Void> = Tasks.forException(exception)

        whenever(mockDb.collection(any())).thenReturn(mockCollection)
        whenever(mockCollection.document(any())).thenReturn(mockDocument)
        whenever(mockCollection.document()).thenReturn(mockDocument)
        whenever(mockDocument.id).thenReturn("fakeLog123")
        whenever(mockDocument.set(any())).thenReturn(updateTask)

        // Act
        val result = logRepository.addCollaborators(logId, collaborators)

        // Assert
        assert(result is DataResult.Failure)
        assertEquals("Firestore update failed", (result as DataResult.Failure).throwable.message)
    }

    @Test
    fun testRemoveCollaboratorsSuccess(): Unit = runBlocking {
        // Arrange
        val logId = "log123"
        val collaborators = listOf("user1", "user2", "user3")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(any())).thenReturn(mockDocument)

        val updateTask: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyMap())).thenReturn(updateTask)

        // Act
        val result = logRepository.removeCollaborators(logId, collaborators)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item)
    }

    @Test
    fun testRemoveCollaboratorsEmptyList(): Unit = runBlocking {
        // Arrange
        val logId = "log123"
        val collaborators = emptyList<String>()

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(any())).thenReturn(mockDocument)

        val updateTask: Task<Void> = Tasks.forResult(null)
        whenever(mockDocument.update(anyMap())).thenReturn(updateTask)

        // Act
        val result = logRepository.removeCollaborators(logId, collaborators)

        // Assert
        assert(result is DataResult.Success)
        assert((result as DataResult.Success).item)
    }

    @Test
    fun testRemoveCollaboratorsFirestoreException(): Unit = runBlocking {
        // Arrange
        val logId = "log123"
        val collaborators = listOf("user1", "user2", "user3")

        whenever(mockDb.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(any())).thenReturn(mockDocument)

        val exception = Exception("Firestore update failed")
        val updateTask: Task<Void> = Tasks.forException(exception)
        whenever(mockDocument.update(anyMap())).thenReturn(updateTask)

        // Act
        val result = logRepository.removeCollaborators(logId, collaborators)

        // Assert
        assert(result is DataResult.Failure)
        assertEquals("java.lang.Exception: Firestore update failed", (result as DataResult.Failure).throwable.toString())
    }
}