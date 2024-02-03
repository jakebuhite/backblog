package com.tabka.backblogapp.network.models

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test

class UserTest {
    private val testJson = """{"user_id":"123456","username":"Test123","join_date":"1706751265551","avatar_preset":1,"friends":{"user1":true,"user2":true},"blocked":{"user3":true,"user4":true}}"""
    private val userData = UserData(
        userId = "123456",
        username = "Test123",
        joinDate = "1706751265551",
        avatarPreset = 1,
        friends = mapOf("user1" to true, "user2" to true),
        blocked = mapOf("user3" to true, "user4" to true)
    )

    @Test
    fun testSerializationSuccess() {
        val jsonString = Json.encodeToString(userData)

        Assert.assertEquals(testJson, jsonString)
    }

    @Test
    fun testDeserializationSuccess() {
        val testData = Json.decodeFromString<UserData>(testJson)

        Assert.assertEquals(userData.userId, testData.userId)
        Assert.assertEquals(userData.username, testData.username)
        Assert.assertEquals(userData.joinDate, testData.joinDate)
        Assert.assertEquals(userData.avatarPreset, testData.avatarPreset)
        Assert.assertEquals(userData.friends, testData.friends)
        Assert.assertEquals(userData.blocked, testData.blocked)
    }
}