package com.tabka.backblogapp.util

fun getErrorMessage(errorCode: String): String {
    return when (errorCode) {
        "ERROR_INVALID_EMAIL" -> "Invalid email."
        "ERROR_INVALID_PASSWORD" -> "Invalid password."
        "ERROR_INVALID_CREDENTIAL" -> "Incorrect email or password."
        "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "An account already exists with the same email address."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Email already in use."
        "ERROR_USER_DISABLED" -> "The user account has been disabled by an administrator."
        "ERROR_USER_DELETED" -> "User not found."
        else -> "There was an error performing authentication."
    }
}