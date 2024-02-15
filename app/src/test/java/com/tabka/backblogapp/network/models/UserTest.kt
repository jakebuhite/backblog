package com.tabka.backblogapp.network.models

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test

class UserTest {
    private val userData = UserData(
        userId = "123",
        username = "john_doe",
        joinDate = "2024-02-12",
        avatarPreset = 2,
        friends = mapOf("friend1" to true, "friend2" to false),
        blocked = mapOf("blocked1" to true)
    )

    private val testJson = """{"user_id":"123","username":"john_doe","join_date":"2024-02-12","avatar_preset":2,"friends":{"friend1":true,"friend2":false},"blocked":{"blocked1":true}}"""

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