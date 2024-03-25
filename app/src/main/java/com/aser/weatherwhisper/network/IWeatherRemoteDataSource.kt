package com.aser.weatherwhisper.network

import com.aser.weatherwhisper.model.WeatherResponse
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import kotlinx.coroutines.flow.Flow

interface IWeatherRemoteDataSource {
    suspend fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        language: String,
        units: String,
    ): Flow<WeatherResponse>

    suspend fun getWeatherByCity(
        city: String,
        language: String,
        units: String
    ): Flow<WeatherResponseCountry>
}