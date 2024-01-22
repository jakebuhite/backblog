package com.tabka.backblogapp.network.models

import com.google.firebase.firestore.PropertyName

data class FriendRequestData (
    @PropertyName("sender_id")
    val senderId: String?,
    @PropertyName("target_id")
    val targetId: String?,
    @PropertyName("request_date")
    val requestDate: String?,
    @PropertyName("is_complete")
    val isComplete: Boolean?
)