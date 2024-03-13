package com.aser.weatherwhisper.model

import android.util.Log
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import com.google.android.gms.common.api.internal.ApiKey
import kotlinx.coroutines.flow.Flow

class WeatherRepository private constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource
) {
    companion object {
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(
                    weatherRemoteDataSource
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
}
