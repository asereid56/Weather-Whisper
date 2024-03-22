package com.aser.weatherwhisper.model

import android.content.Context
import android.util.Log
import androidx.work.WorkManager
import androidx.work.Worker
import com.aser.weatherwhisper.db.CitiesLocalDataBase
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import com.google.android.gms.common.api.internal.ApiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class WeatherRepository private constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val citiesLocalDataBase: CitiesLocalDataBase
) {
    companion object {
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            citiesLocalDataBase: CitiesLocalDataBase
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(
                    weatherRemoteDataSource,
                    citiesLocalDataBase
                ).also {
                    instance = it
                }
            }
        }
    }

    suspend fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        language: String,
        units: String,
    ): Flow<WeatherResponse> {
        return weatherRemoteDataSource.getWeatherDetails(latitude, longitude, language, units)
    }

    suspend fun getWeatherByCities(
        city: String,
        language: String,
        units: String
    ): Flow<WeatherResponseCountry> {
        return weatherRemoteDataSource.getWeatherByCity(city, language, units)
    }

    suspend fun getFavCities(): Flow<List<City>> {
        return citiesLocalDataBase.getFavCities()
    }

    suspend fun getAlertCities(): Flow<List<City>> {
        return citiesLocalDataBase.getAlertCities()
    }

    suspend fun insertCityToFav(city: City) {
        citiesLocalDataBase.insertCityToFav(city)
    }

    suspend fun insertCityToAlert(city: City) {
        citiesLocalDataBase.insertCityToAlert(city)
    }

    suspend fun deleteCityFromFav(city: City) {
        citiesLocalDataBase.deleteCityFromFav(city)
    }

    suspend fun deleteCityFromAlert(city: City) {
        citiesLocalDataBase.deleteCityFromAlert(city)
    }

    suspend fun cancelWorkerForCity(city: City, context: Context) {
        val uniqueKey = city.cityName + city.type
        WorkManager.getInstance(context).cancelAllWorkByTag(uniqueKey)
    }
}
