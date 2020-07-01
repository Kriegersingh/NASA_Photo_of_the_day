package com.example.nasaphotooftheday.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaphotooftheday.apiHelper.NasaApiHelper
import com.example.nasaphotooftheday.model.PhotoOfTheDayResponse
import com.example.nasaphotooftheday.utils.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel:ViewModel() {
    private val photoOfTheDayResponse by lazy {
        MutableLiveData<PhotoOfTheDayResponse>()
    }

    fun getPhotoOfTheDay() = photoOfTheDayResponse

    fun fetchPhotoOfTheDay(date:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val response = ApiClient.retrofit.create(NasaApiHelper::class.java)
                        .getPhotoOfTheDay("M5MsS6Ja186R1lhOGhN4dM8pWAvzrdekoISSmXjo", date)
                    if (response.isSuccessful) {
                        photoOfTheDayResponse.postValue(response.body())
                    }else{

                    }
                }catch (e: Exception){
                e.printStackTrace()
            }

            }
        }
    }
}