package com.tabka.backblogapp.network.models

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class FriendRequestTest {

    private val testJson = """{"sender_id":"sender123","target_id":"target456","request_date":"1706751265551","is_complete":true}"""
    private val friendRequestData = FriendRequestData(
        senderId = "sender123",
        targetId = "target456",
        requestDate = "1706751265551",
        isComplete = true
    )

    @Test
    fun testSerializationSuccess() {
        val jsonString = Json.encodeToString(friendRequestData)

        assertEquals(testJson, jsonString)
    }

    @Test
    fun testDeserializationSuccess() {
        val testData = Json.decodeFromString<FriendRequestData>(testJson)

        assertEquals(testData.senderId, friendRequestData.senderId)
        assertEquals(testData.targetId, friendRequestData.targetId)
        assertEquals(testData.requestDate, friendRequestData.requestDate)
        assertEquals(testData.isComplete, friendRequestData.isComplete)
    }
}
