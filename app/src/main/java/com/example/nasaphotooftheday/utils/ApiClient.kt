package com.example.nasaphotooftheday.utils

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object ApiClient {

    val retrofit: Retrofit by lazy    {
        val logging = HttpLoggingInterceptor()
        logging.apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .addInterceptor(logging)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
        return@lazy Retrofit.Builder()
            .baseUrl("https://api.nasa.gov")
            .addConverterFactory(
                Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
                    .asConverterFactory("application/json".toMediaType())
            )
            .client(httpClient.build())
            .build()
    }

}