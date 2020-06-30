package com.adamian.daypicture.api

import androidx.lifecycle.LiveData
import com.adamian.daypicture.model.DayPicture
import com.adamian.daypicture.util.GenericApiResponse
import retrofit2.http.GET

interface ApiService {
    @GET("planetary/apod?api_key=wx6Jf0DxuFdcrmX9YgZBtSHy59cRXdPWiCQSZVt9")
    fun getDayPicture(): LiveData<GenericApiResponse<DayPicture>>
}