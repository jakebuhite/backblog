package com.tabka.backblogapp.network.models

import com.google.firebase.firestore.PropertyName

data class LogData(
    @PropertyName("log_id")
    val logId: String?,
    val name: String?,
    @PropertyName("creation_date")
    val creationDate: String?,
    @PropertyName("last_modified_date")
    val lastModifiedDate: String?,
    @PropertyName("is_visible")
    val isVisible: Boolean?,
    var owner: Map<String, Any>?,
    val collaborators: Map<String, Map<String, Int>>?,
    @PropertyName("movie_ids")
    val movieIds: Map<String, Boolean>?,
    @PropertyName("watched_ids")
    val watchedIds: Map<String, Boolean>?
)