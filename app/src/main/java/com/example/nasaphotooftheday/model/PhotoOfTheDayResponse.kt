package com.example.nasaphotooftheday.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoOfTheDayResponse(
    val copyright: String?=null,
    val date: String,
    val explanation: String,
    val hdurl: String? = null,
    @SerialName("media_type")
    val mediaType: String,
    @SerialName("service_version")
    val serviceVersion: String,
    val title: String?=null,
    val url: String

)