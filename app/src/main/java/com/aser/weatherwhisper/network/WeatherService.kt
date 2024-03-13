package com.aser.weatherwhisper.network

import com.aser.weatherwhisper.Constants
import com.aser.weatherwhisper.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
//    @GET("weather")
//    suspend fun getWeatherByLongAndLat(
//        @Query("lat") latitude: Double,
//        @Query("lon") longitude: Double,
//        @Query("appid") apiKey: String = Constants.API_KEY
//    ): Response<WeatherResponse>

    @GET("onecall")
    suspend fun getWeatherDetails(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): Response<WeatherResponse>
}