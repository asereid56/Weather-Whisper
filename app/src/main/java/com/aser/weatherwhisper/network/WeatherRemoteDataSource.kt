package com.aser.weatherwhisper.network

import android.util.Log
import com.aser.weatherwhisper.model.WeatherResponse
import com.google.android.gms.common.api.internal.ApiKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Exception

class WeatherRemoteDataSource {
    private val weatherService = RetrofitHelper.service
//    suspend fun getWeatherByLatAndLong(latitude: Double, longitude: Double): WeatherResponse? {
//        try {
//            val weatherResponse = weatherService.getWeatherByLongAndLat(latitude, longitude)
//            if (weatherResponse.isSuccessful) {
//                return weatherResponse.body()
//            } else {
//                return null
//            }
//        } catch (e: Exception) {
//            return null
//        }
//    }

    suspend fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        language: String,
        units: String,
    ): Flow<WeatherResponse> = flow {
        try {
            val weatherResponse =
                weatherService.getWeatherDetails(latitude, longitude, language, units)
            if (weatherResponse.isSuccessful) {
                weatherResponse.body()?.let { emit(it) }
            } else {
                Log.e("WeatherRemoteDataSource", "Unsuccessful response: ${weatherResponse.code()}")
            }
        } catch (e: Exception) {
            Log.e("WeatherRemoteDataSource", "Exception: ${e.javaClass.simpleName}")
        }
    }

    companion object {
        val instance: WeatherRemoteDataSource by lazy { WeatherRemoteDataSource() }
    }
}