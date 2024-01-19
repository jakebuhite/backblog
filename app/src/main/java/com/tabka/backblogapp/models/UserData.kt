package com.tabka.backblogapp.models

data class UserData(
    val userId: String?,
    val username: String?,
    val joinDate: String?,
    val avatarPreset: Int?,
    val friends: Map<String, Boolean>?,
    val blocked: Map<String, Boolean>?
)
