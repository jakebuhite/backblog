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
    val collaborators: Map<String, Map<String, Int>>?,
    @SerialName("movie_ids") val movieIds: Map<String, Boolean>?,
    @SerialName("watched_ids") val watchedIds: Map<String, Boolean>?
)

@Serializable
data class Owner(
    @SerialName("user_id") val userId: String?,
    val priority: Int?
)