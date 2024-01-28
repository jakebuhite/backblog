package com.tabka.backblogapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @SerialName("user_id") val userId: String?,
    val username: String?,
    val joinDate: String?,
    @SerialName("avatar_preset") val avatarPreset: Int?,
    val friends: Map<String, Boolean>?,
    val blocked: Map<String, Boolean>?
)
