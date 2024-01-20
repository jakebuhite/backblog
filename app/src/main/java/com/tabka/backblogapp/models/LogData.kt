package com.tabka.backblogapp.models

data class LogData(
    val logId: String?,
    val name: String?,
    val priority: Int?,
    val status: String?,
    val movieIds: Map<String, Boolean>?,
    val watchedIds: Map<String, Boolean>?,
    val owner: Map<String, Any>?,
    val collaborators: Map<String, Map<String, Int>>?,
    val creationDate: String?,
    val lastModifiedDate: String?
)