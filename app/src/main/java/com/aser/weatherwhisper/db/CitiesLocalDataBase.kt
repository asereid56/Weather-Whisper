package com.aser.weatherwhisper.db

import android.content.Context
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class CitiesLocalDataBase(context: Context) : ICitiesLocalDataBase {
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

//    override suspend fun insertCityToHomeFragment(weatherResponse: WeatherResponse) {
//        val json = JsonUtils.weatherResponseToJson(weatherResponse)
//        dao.insertCityToHome(json)
//    }
//
//    override suspend fun deleteAllCitiesFromHomeFragment() {
//        dao.deleteAllCitiesFromHome()
//    }
//
//    override fun getCityToHomeFragment(): Flow<WeatherResponse?> {
//        return dao.getCityToHomeFragment().map { json ->
//            json?.let { JsonUtils.jsonToWeatherResponse(it) }
//        }
//    }

    companion object {
        @Volatile
        private var instance: CitiesLocalDataBase? = null
        fun getInstance(context: Context): CitiesLocalDataBase {
            return instance ?: synchronized(this) {
                instance ?: CitiesLocalDataBase(context).also {
                    instance = it
                }
            }
        }

    }
}