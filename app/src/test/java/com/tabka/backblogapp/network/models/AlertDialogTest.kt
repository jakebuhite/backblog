package com.tabka.backblogapp.network.models

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class AlertDialogTest {
    @Test
    fun testCreateAlertDialogShouldReturnCorrectInstance() {
        // Arrange
        val header = "Alert Header"
        val message = "Alert Message"
        val dismissText = "Dismiss"
        val acceptText = "Accept"
        val dismissColor = Color.Red
        val acceptColor = Color.Green

        // Act
        val dismiss = Dismiss(dismissText, dismissColor)
        val accept = Accept(acceptText, acceptColor) { println("Accept Clicked") }
        val alertDialog = AlertDialog(true, header, message, dismiss, accept)

        // Assert
        assertEquals(true, alertDialog.isVisible)
        assertEquals(header, alertDialog.header)
        assertEquals(message, alertDialog.message)
        assertEquals(dismiss, alertDialog.dismiss)
        assertEquals(accept, alertDialog.accept)
    }

    @Test
    fun testCreateDismissShouldReturnCorrectInstance() {
        // Arrange
        val dismissText = "Dismiss"
        val dismissColor = Color.Red

        // Act
        val dismiss = Dismiss(dismissText, dismissColor)

        // Assert
        assertEquals(dismissText, dismiss.text)
        assertEquals(dismissColor, dismiss.textColor)
    }

    @Test
    fun testCreateAcceptShouldReturnCorrectInstance() {
        // Arrange
        val acceptText = "Accept"
        val acceptColor = Color.Green

        // Act
        val accept = Accept(acceptText, acceptColor) { println("Accept Clicked") }

        // Assert
        assertEquals(acceptText, accept.text)
        assertEquals(acceptColor, accept.textColor)
    }

    @Test
    fun testAcceptActionShouldReturnCorrectInstance() {
        // Arrange
        var actionTriggered = false
        val accept = Accept("Accept", Color.Black) { actionTriggered = true }

        // Act
        accept.action.invoke()

        // Assert
        assertEquals(true, actionTriggered)
    }
}
