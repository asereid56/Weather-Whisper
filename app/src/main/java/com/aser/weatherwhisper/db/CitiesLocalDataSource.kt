package com.aser.weatherwhisper.db

import android.content.Context
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class CitiesLocalDataSource(context: Context) : ICitiesLocalDataBase {
    private val dao: CityDAO by lazy {
        val db: AppDataBase = AppDataBase.getInstance(context)
        db.getCityDao()
    }

    override suspend fun insertCityToFav(city: City) {
        city.type = "Fav"
        dao.insertCity(city)
    }

    override suspend fun insertCityToAlert(city: City) {
        city.type = "Alert"
        dao.insertCity(city)
    }

    override suspend fun deleteCityFromFav(city: City) {
        city.type = "Fav"
        dao.deleteCity(city)
    }

    override suspend fun deleteCityFromAlert(city: City) {
        city.type = "Alert"
        dao.deleteCity(city)
    }

    override fun getFavCities(): Flow<List<City>> {
        return dao.getAllFavCity()
    }

    override fun getAlertCities(): Flow<List<City>> {
        return dao.getALlAlertCity()
    }

    override suspend fun insertWeatherResponse(weatherResponse: WeatherResponse) {
        dao.insertWeatherResponse(weatherResponse)
    }

    override suspend fun deleteAllWeatherResponse() {
        dao.deleteAllWeatherResponse()
    }

    override fun getWeatherResponseToHomeFragment(): Flow<WeatherResponse> {
        return dao.getFirstWeatherResponse()
    }


    companion object {
        @Volatile
        private var instance: CitiesLocalDataSource? = null
        fun getInstance(context: Context): CitiesLocalDataSource {
            return instance ?: synchronized(this) {
                instance ?: CitiesLocalDataSource(context).also {
                    instance = it
                }
            }
        }

    }
}