package com.tabka.backblogapp.network.models

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test

class LogTest {
    private val testJson = """{"log_id":"123456","name":"Sample Log","creation_date":"1706751265551","last_modified_date":"1706751265551","is_visible":true,"owner":{"user_id":"user123","priority":1},"collaborators":{"collaborator1":{"priority":2},"collaborator2":{"priority":1}},"movie_ids":{"movie1":true,"movie2":false},"watched_ids":{"movie1":true,"movie2":true}}"""
    private val logData = LogData(
        logId = "123456",
        name = "Sample Log",
        creationDate = "1706751265551",
        lastModifiedDate = "1706751265551",
        isVisible = true,
        owner = Owner(userId = "user123", priority = 1),
        collaborators = mapOf(
            "collaborator1" to mapOf("priority" to 2),
            "collaborator2" to mapOf("priority" to 1)
        ),
        movieIds = mutableMapOf("movie1" to true, "movie2" to false),
        watchedIds = mapOf("movie1" to true, "movie2" to true)
    )

    @Test
    fun testSerializationSuccess() {
        val jsonString = Json.encodeToString(logData)

        Assert.assertEquals(testJson, jsonString)
    }

    @Test
    fun testDeserializationSuccess() {
        val testData = Json.decodeFromString<LogData>(testJson)

        Assert.assertEquals(logData.logId, testData.logId)
        Assert.assertEquals(logData.name, testData.name)
        Assert.assertEquals(logData.creationDate, testData.creationDate)
        Assert.assertEquals(logData.lastModifiedDate, testData.lastModifiedDate)
        Assert.assertEquals(logData.isVisible, testData.isVisible)
        Assert.assertEquals(logData.owner, testData.owner)
        Assert.assertEquals(logData.collaborators, testData.collaborators)
        Assert.assertEquals(logData.movieIds, testData.movieIds)
        Assert.assertEquals(logData.watchedIds, testData.watchedIds)
    }
}