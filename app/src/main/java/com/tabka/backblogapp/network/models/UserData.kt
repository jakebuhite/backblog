//
//  UserData.kt
//  backblog
//
//  Created by Jake Buhite on 2/9/24.
//
package com.tabka.backblogapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @SerialName("user_id") val userId: String? = null,
    val username: String? = null,
    @SerialName("join_date") val joinDate: String? = null,
    @SerialName("avatar_preset") val avatarPreset: Int? = 1,
    val friends: Map<String, Boolean>? = emptyMap(),
    val blocked: Map<String, Boolean>? = emptyMap()
)
