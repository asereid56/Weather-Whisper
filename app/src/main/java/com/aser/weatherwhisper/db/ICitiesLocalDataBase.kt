package com.aser.weatherwhisper.db

import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface ICitiesLocalDataBase {
    suspend fun insertCityToFav(city: City)
    suspend fun insertCityToAlert(city: City)
    suspend fun deleteCityFromFav(city: City)

    suspend fun deleteCityFromAlert(city: City)
    fun getFavCities(): Flow<List<City>>
    fun getAlertCities(): Flow<List<City>>
    suspend fun insertWeatherResponse(weatherResponse: WeatherResponse)
    suspend fun deleteAllWeatherResponse()
    fun getWeatherResponseToHomeFragment(): Flow<WeatherResponse?>

}