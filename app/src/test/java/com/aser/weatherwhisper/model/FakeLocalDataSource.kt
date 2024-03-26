package com.aser.weatherwhisper.model

import com.aser.weatherwhisper.db.ICitiesLocalDataBase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class FakeLocalDataSource(
    private val favCities: MutableList<City>,
    private val alertCities: MutableList<City>,
    private val weatherResponses: MutableList<WeatherResponse>
) : ICitiesLocalDataBase {


    override suspend fun insertCityToFav(city: City) {
        favCities.add(city)
    }

    override suspend fun insertCityToAlert(city: City) {
        alertCities.add(city)
    }

    override suspend fun deleteCityFromFav(city: City) {
        favCities.remove(city)
    }

    override suspend fun deleteCityFromAlert(city: City) {
        alertCities.remove(city)
    }

    override fun getFavCities(): Flow<List<City>> {
        return flowOf(favCities)
    }

    override fun getAlertCities(): Flow<List<City>> {
        return flowOf(alertCities)
    }

    override suspend fun insertWeatherResponse(weatherResponse: WeatherResponse) {
        weatherResponses.add(weatherResponse)
    }

    override suspend fun deleteAllWeatherResponse() {
        weatherResponses.clear()
    }

    override fun getWeatherResponseToHomeFragment(): Flow<WeatherResponse?> {
        return flowOf(weatherResponses.firstOrNull())
    }

}