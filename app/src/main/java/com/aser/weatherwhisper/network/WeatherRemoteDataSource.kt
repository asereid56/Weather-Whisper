package com.aser.weatherwhisper.network

import android.util.Log
import com.aser.weatherwhisper.model.WeatherResponse
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import com.google.android.gms.common.api.internal.ApiKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlin.Exception

class WeatherRemoteDataSource {
    private val weatherService = RetrofitHelper.service
    private val weatherCountryService = RetrofitHelperCity.service
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
                weatherResponse.body()?.let {
                    emit(it)
                }
                Log.i(
                    "TAG",
                    "getWeatherDetails: ${weatherResponse.body()} $longitude $latitude $units $language"
                )
            } else {
                Log.e(
                    "TAG",
                    "Unsuccessful response from getWeatherDetails from RemoteDataSource: ${weatherResponse.code()}"
                )
            }
        } catch (e: Exception) {
            Log.e(
                "TAG",
                "Exception: from getWeatherDetails from RemoteDataSource: ${e.javaClass.simpleName}  $longitude $latitude $units $language \n ${e.message}"
            )
        }
    }

    suspend fun getWeatherByCity(
        city: String,
        language: String,
        units: String
    ): Flow<WeatherResponseCountry> = flow {
        try {
            val weatherResponse = weatherCountryService.getWeatherByCity(city, language, units)
            if (weatherResponse.isSuccessful) {
                weatherResponse.body()?.let {
                    emit(it)
                }
            } else {
                Log.e(
                    "TAG",
                    "Unsuccessful response:  getWeatherByCity ${weatherResponse.code()}"
                )
            }
        } catch (e: Exception) {
            Log.e(
                "TAG",
                "Exception: getWeatherByCity ${e.javaClass.simpleName} "
            )
        }
    }


    companion object {
        val instance: WeatherRemoteDataSource by lazy { WeatherRemoteDataSource() }
    }
}