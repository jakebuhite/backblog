package com.tabka.backblogapp.network.models

import com.google.firebase.firestore.PropertyName

data class UserData(
    @PropertyName("user_id")
    val userId: String?,
    val username: String?,
    @PropertyName("join_date")
    val joinDate: String?,
    @PropertyName("avatar_preset")
    val avatarPreset: Int?,
    val friends: Map<String, Boolean>?,
    val blocked: Map<String, Boolean>?
)
