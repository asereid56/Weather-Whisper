package com.aser.weatherwhisper.model

import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import com.aser.weatherwhisper.network.IWeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class FakeRemoteDataSource(
    private val weatherResponses: WeatherResponse,
    private val weatherResponseCountry: WeatherResponseCountry
) : IWeatherRemoteDataSource {

    override suspend fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        language: String,
        units: String
    ): Flow<WeatherResponse> {
        return flowOf(weatherResponses)
    }

    override suspend fun getWeatherByCity(
        city: String,
        language: String,
        units: String
    ): Flow<WeatherResponseCountry> {
        return flowOf(weatherResponseCountry)
    }

}