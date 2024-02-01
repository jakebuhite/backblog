package com.tabka.backblogapp.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

@OptIn(ExperimentalSerializationApi::class)
object ApiClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val format = Json { explicitNulls = false }
    private val contentType = "application/json".toMediaType()

    val movieApiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(format.asConverterFactory(contentType))
            .build()
            .create(ApiService::class.java)
    }
}