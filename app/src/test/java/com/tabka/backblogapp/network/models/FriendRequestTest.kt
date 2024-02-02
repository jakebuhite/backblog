package com.tabka.backblogapp.network.models

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

/*class FriendRequestDataTest {

    @Test
    fun testSerialization() {
        // Given
        val friendRequestData = FriendRequestData(
            senderId = "sender123",
            targetId = "target456",
            requestDate = "2024-02-02",
            isComplete = true
        )

        // When
        val jsonString = Json.encodeToString(friendRequestData)

        // Then
        val expectedJson = """{"sender_id":"sender123","target_id":"target456","request_date":"2024-02-02","is_complete":true}"""
        assertEquals(expectedJson, jsonString)
    }

    @Test
    fun testDeserialization() {
        // Given
        val jsonString = """{"sender_id":"sender123","target_id":"target456","request_date":"2024-02-02","is_complete":true}"""

        // When
        val friendRequestData = Json.decodeFromString<FriendRequestData>(jsonString)

        // Then
        assertEquals("sender123", friendRequestData.senderId)
        assertEquals("target456", friendRequestData.targetId)
        assertEquals("2024-02-02", friendRequestData.requestDate)
        assertEquals(true, friendRequestData.isComplete)
    }
}*/
