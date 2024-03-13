package com.aser.weatherwhisper.network

import com.aser.weatherwhisper.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private val retrofitInstance = Retrofit.Builder()
        .baseUrl(Constants.WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: WeatherService by lazy {
        retrofitInstance.create(WeatherService::class.java)
    }
}