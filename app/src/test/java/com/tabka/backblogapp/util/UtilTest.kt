package com.tabka.backblogapp.util

import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {

    @Serializable
    data class TestData(val value: String)

    @Test
    fun testToJsonElementShouldUseUnknownTypes() {
        // Arrange
        val testData = TestData("example")

        // Act
        val jsonElement = testData.toJsonElement().toString()

        // Assert
        val expectedJson = """{"value":"example"}"""
        assertEquals(expectedJson, jsonElement)
    }

    @Test
    fun testGetAvatarResourceIdShouldReturnCorrectPairs() {
        // Arrange
        val expected1 = "Quasar"
        val expected2 = "Cipher"
        val expected3 = "Nova"
        val expected4 = "Flux"
        val expected5 = "Torrent"
        val expected6 = "Odyssey"
        val defaultAvatar = "Quasar"

        // Act
        val actual1 = getAvatarResourceId(1)
        val actual2 = getAvatarResourceId(2)
        val actual3 = getAvatarResourceId(3)
        val actual4 = getAvatarResourceId(4)
        val actual5 = getAvatarResourceId(5)
        val actual6 = getAvatarResourceId(6)
        val actualDefaultAvatar = getAvatarResourceId(10)

        // Assert
        assertEquals(expected1, actual1.first)
        assertEquals(expected2, actual2.first)
        assertEquals(expected3, actual3.first)
        assertEquals(expected4, actual4.first)
        assertEquals(expected5, actual5.first)
        assertEquals(expected6, actual6.first)
        assertEquals(defaultAvatar, actualDefaultAvatar.first)
    }

    @Test
    fun testGetErrorMessageShouldReturnCorrectMessage() {
        // Arrange
        val errorCode1 = "ERROR_INVALID_EMAIL"
        val errorCode2 = "ERROR_INVALID_PASSWORD"
        val errorCode3 = "ERROR_INVALID_CREDENTIAL"
        val errorCode4 = "ERROR_CREDENTIAL_ALREADY_IN_USE"
        val errorCode5 = "ERROR_EMAIL_ALREADY_IN_USE"
        val errorCode6 = "ERROR_USER_DISABLED"
        val errorCode7 = "ERROR_USER_DELETED"
        val defaultErrorCode = "SOME_OTHER_ERROR"

        // Act
        val actualMessage1 = getErrorMessage(errorCode1)
        val actualMessage2 = getErrorMessage(errorCode2)
        val actualMessage3 = getErrorMessage(errorCode3)
        val actualMessage4 = getErrorMessage(errorCode4)
        val actualMessage5 = getErrorMessage(errorCode5)
        val actualMessage6 = getErrorMessage(errorCode6)
        val actualMessage7 = getErrorMessage(errorCode7)
        val actualDefaultMessage = getErrorMessage(defaultErrorCode)

        // Assert
        assertEquals("Invalid email.", actualMessage1)
        assertEquals("Invalid password.", actualMessage2)
        assertEquals("Incorrect email or password.", actualMessage3)
        assertEquals("An account already exists with the same email address.", actualMessage4)
        assertEquals("Email already in use.", actualMessage5)
        assertEquals("The user account has been disabled by an administrator.", actualMessage6)
        assertEquals("User not found.", actualMessage7)
        assertEquals("There was an error performing authentication.", actualDefaultMessage)
    }
}
