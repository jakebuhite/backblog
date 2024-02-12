package com.tabka.backblogapp.network.models

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class LogRequestDataTest {

    private val testJson = """{"request_id":"req123","sender_id":"test123","target_id":"test456","log_id":"log123","request_date":"1706751265551","is_complete":true}"""
    private val logRequestData = LogRequestData(
        requestId = "req123",
        senderId = "test123",
        targetId = "test456",
        logId = "log123",
        requestDate = "1706751265551",
        isComplete = true
    )

    @Test
    fun testSerializationSuccess() {
        val jsonString = Json.encodeToString(logRequestData)

        assertEquals(testJson, jsonString)
    }

    @Test
    fun testDeserializationSuccess() {
        val testData = Json.decodeFromString<LogRequestData>(testJson)

        assertEquals(testData.requestId, logRequestData.requestId)
        assertEquals(testData.senderId, logRequestData.senderId)
        assertEquals(testData.targetId, logRequestData.targetId)
        assertEquals(testData.requestDate, logRequestData.requestDate)
        assertEquals(testData.isComplete, logRequestData.isComplete)
    }
}
