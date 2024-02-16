package com.tabka.backblogapp

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class LogRepositoryTest {

    @Mock
    private lateinit var mockDb: FirebaseFirestore

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
        logRepository = LogRepository(mockDb)
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
}