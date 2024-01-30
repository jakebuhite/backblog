package com.tabka.backblogapp.util

import kotlinx.serialization.SerialName
import kotlinx.serialization.json.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

// Convert Firebase Firestore maps to JSON element for serialization
fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is JsonElement -> this
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Array<*> -> JsonArray(map { it.toJsonElement() })
    is List<*> -> JsonArray(map { it.toJsonElement() })
    is Map<*, *> -> JsonObject(map { it.key.toString() to it.value.toJsonElement() }.toMap())
    else -> {
        val jsonEncoder = Json {
            encodeDefaults = true
        }

        val properties = this::class.memberProperties
        val jsonObject = JsonObject(properties.associate { property ->
            val serialName = property.findAnnotation<SerialName>()?.value
            val propertyName = serialName ?: property.name
            propertyName to jsonEncoder.encodeToJsonElement(property.getter.call(this))
        })

        jsonObject
    }
}