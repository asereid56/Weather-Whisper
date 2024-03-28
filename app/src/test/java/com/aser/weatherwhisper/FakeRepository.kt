package com.aser.weatherwhisper

import android.content.Context
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.IWeatherRepository
import com.aser.weatherwhisper.model.WeatherResponse
import com.aser.weatherwhisper.model.countryname.Clouds
import com.aser.weatherwhisper.model.countryname.Coord
import com.aser.weatherwhisper.model.countryname.Main
import com.aser.weatherwhisper.model.countryname.Sys
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import com.aser.weatherwhisper.model.countryname.Wind
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeRepository : IWeatherRepository {
    private val flowCities = MutableStateFlow<List<City>>(emptyList())

    private val weatherResponseCountry by lazy {
        WeatherResponseCountry(
            "", Clouds(12), 12, Coord(0.0, 0.0), 1, 1,
            Main(0.0, 1, 1, 0.0, 0.0, 0.0), "",
            Sys("", 1, 1, 1, 1), 1, 1,
            emptyList(),
            Wind(1, 0.0)
        )
    }

    override suspend fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        language: String,
        units: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherByCities(
        city: String,
        language: String,
        units: String
    ): Flow<WeatherResponseCountry> {
        return flowOf(weatherResponseCountry)
    }

    override fun getFavCities(): Flow<List<City>> {
        return flowCities
    }

    override fun getAlertCities(): Flow<List<City>> {
        return flowCities
    }

    override suspend fun insertCityToFav(city: City) {
        flowCities.value += city
    }

    override suspend fun insertCityToAlert(city: City) {
        flowCities.value += city
    }

    override suspend fun deleteCityFromFav(city: City) {
        flowCities.value -= city
    }

    override suspend fun deleteCityFromAlert(city: City) {
        TODO("Not yet implemented")
    }

    override fun cancelWorkerForCity(city: City, context: Context) {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeatherResponse(weatherResponse: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllWeatherResponse() {
        TODO("Not yet implemented")
    }

    override fun getWeatherResponseToHomeFragment(): Flow<WeatherResponse?> {
        TODO("Not yet implemented")
    }
}