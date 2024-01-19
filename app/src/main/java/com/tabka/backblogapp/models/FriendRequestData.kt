package com.tabka.backblogapp.models

data class FriendRequestData (
    val senderId: String?,
    val targetId: String?,
    val requestDate: String?,
    val isComplete: Boolean?
)