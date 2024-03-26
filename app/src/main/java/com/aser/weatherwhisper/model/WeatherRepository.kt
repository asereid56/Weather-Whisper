package com.aser.weatherwhisper.model

import android.content.Context
import androidx.work.WorkManager
import com.aser.weatherwhisper.db.CitiesLocalDataSource
import com.aser.weatherwhisper.db.ICitiesLocalDataBase
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import com.aser.weatherwhisper.network.IWeatherRemoteDataSource
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepository(
    private val weatherRemoteDataSource: IWeatherRemoteDataSource,
    private val citiesLocalDataBase: ICitiesLocalDataBase
) : IWeatherRepository {
    companion object {
        @Volatile
        private var instance: WeatherRepository? = null

        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            citiesLocalDataSource: CitiesLocalDataSource
        ): WeatherRepository {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepository(
                    weatherRemoteDataSource,
                    citiesLocalDataSource
                ).also {
                    instance = it
                }
            }
        }
    }

    override suspend fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        language: String,
        units: String,
    ): Flow<WeatherResponse> {
        return weatherRemoteDataSource.getWeatherDetails(latitude, longitude, language, units)
    }

    override suspend fun getWeatherByCities(
        city: String,
        language: String,
        units: String
    ): Flow<WeatherResponseCountry> {
        return weatherRemoteDataSource.getWeatherByCity(city, language, units)
    }

    override fun getFavCities(): Flow<List<City>> {
        return citiesLocalDataBase.getFavCities()
    }

    override fun getAlertCities(): Flow<List<City>> {
        return citiesLocalDataBase.getAlertCities()
    }

    override suspend fun insertCityToFav(city: City) {
        citiesLocalDataBase.insertCityToFav(city)
    }

    override suspend fun insertCityToAlert(city: City) {
        citiesLocalDataBase.insertCityToAlert(city)
    }

    override suspend fun deleteCityFromFav(city: City) {
        citiesLocalDataBase.deleteCityFromFav(city)
    }

    override suspend fun deleteCityFromAlert(city: City) {
        citiesLocalDataBase.deleteCityFromAlert(city)
    }

    override fun cancelWorkerForCity(city: City, context: Context) {
        val uniqueKey = city.cityName + city.type
        WorkManager.getInstance(context).cancelAllWorkByTag(uniqueKey)
    }

    override suspend fun insertWeatherResponse(weatherResponse: WeatherResponse) {
        citiesLocalDataBase.insertWeatherResponse(weatherResponse)
    }

    override suspend fun deleteAllWeatherResponse() {
        citiesLocalDataBase.deleteAllWeatherResponse()
    }

    override fun getWeatherResponseToHomeFragment(): Flow<WeatherResponse?> {
        return citiesLocalDataBase.getWeatherResponseToHomeFragment()
    }
}
