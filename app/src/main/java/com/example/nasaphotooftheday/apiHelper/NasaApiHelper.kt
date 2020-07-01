package com.example.nasaphotooftheday.apiHelper

import com.example.nasaphotooftheday.model.PhotoOfTheDayResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiHelper {
    @GET("/planetary/apod")
    suspend fun getPhotoOfTheDay(@Query("api_key")  apiKey : String,@Query("date")date :String) : Response<PhotoOfTheDayResponse>
}