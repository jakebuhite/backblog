//
//  LogRequestData.kt
//  backblog
//
//  Created by Jake Buhite on 2/9/24.
//

package com.tabka.backblogapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogRequestData (
    @SerialName("request_id") val requestId: String?,
    @SerialName("sender_id") val senderId: String?,
    @SerialName("target_id") val targetId: String?,
    @SerialName("log_id") val logId: String?,
    @SerialName("request_date") val requestDate: String?,
    @SerialName("is_complete") val isComplete: Boolean?
)