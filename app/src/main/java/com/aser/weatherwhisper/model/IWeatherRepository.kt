package com.aser.weatherwhisper.model

import android.content.Context
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        language: String,
        units: String,
    ): Flow<WeatherResponse>

    suspend fun getWeatherByCities(
        city: String,
        language: String,
        units: String
    ): Flow<WeatherResponseCountry>

    fun getFavCities(): Flow<List<City>>
    fun getAlertCities(): Flow<List<City>>

    suspend fun insertCityToFav(city: City)

    suspend fun insertCityToAlert(city: City)

    suspend fun deleteCityFromFav(city: City)

    suspend fun deleteCityFromAlert(city: City)
    fun cancelWorkerForCity(city: City, context: Context)

    suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)

    suspend fun deleteAllWeatherResponse()
    fun getWeatherResponseToHomeFragment(): Flow<WeatherResponse>
}