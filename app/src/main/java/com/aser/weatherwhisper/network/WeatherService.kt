package com.aser.weatherwhisper.network

import com.aser.weatherwhisper.utils.Constants
import com.aser.weatherwhisper.model.WeatherResponse
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("onecall")
    suspend fun getWeatherDetails(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): Response<WeatherResponse>

    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("lang") language: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): Response<WeatherResponseCountry>
}