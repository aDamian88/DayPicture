package com.adamian.daypicture.api

import com.adamian.daypicture.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MyRetrofitBuilder {

    const val BASE_URL: String = "https://api.nasa.gov/"

    private val client = OkHttpClient.Builder().connectTimeout(200, java.util.concurrent.TimeUnit.MINUTES).writeTimeout(200, java.util.concurrent.TimeUnit.MINUTES).readTimeout(200, java.util.concurrent.TimeUnit.MINUTES).build()


    val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
    }


    val apiService: ApiService by lazy{
        retrofitBuilder
            .build()
            .create(ApiService::class.java)
    }
}