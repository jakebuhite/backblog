//
//  LogData.kt
//  backblog
//
//  Created by Jake Buhite on 2/9/24.
//
package com.tabka.backblogapp.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogData(
    @SerialName("log_id") val logId: String?,
    val name: String?,
    @SerialName("creation_date") val creationDate: String?,
    @SerialName("last_modified_date") val lastModifiedDate: String?,
    @SerialName("is_visible") val isVisible: Boolean?,
    val owner: Owner?,
    val collaborators: MutableList<String>?,
    val order: Map<String, Int>?,
    @SerialName("movie_ids") val movieIds: MutableList<String>?,
    @SerialName("watched_ids") val watchedIds: MutableList<String>?
)

@Serializable
data class Owner(
    @SerialName("user_id") val userId: String?,
    val priority: Int?
)