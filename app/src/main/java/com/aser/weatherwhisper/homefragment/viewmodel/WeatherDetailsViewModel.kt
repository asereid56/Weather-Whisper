package com.aser.weatherwhisper.homefragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.model.WeatherResponse
import com.aser.weatherwhisper.utils.ApiWeatherState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(private val repo: WeatherRepository) : ViewModel() {
    private val _weatherResponse: MutableStateFlow<ApiWeatherState> =
        MutableStateFlow<ApiWeatherState>(ApiWeatherState.Loading)
    val weatherResponse: StateFlow<ApiWeatherState> = _weatherResponse

    fun getWeatherDetails(latitude: Double, longitude: Double, language: String, units: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherDetails(latitude, longitude, language, units).catch { e ->
                run {
                    _weatherResponse.value = ApiWeatherState.Failure(e)
                }
            }.collect { result ->
                _weatherResponse.value = ApiWeatherState.Success(result)

            }
        }
    }
    fun insertCityToDataBase(weatherResponse: WeatherResponse){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertWeatherResponse(weatherResponse)
        }
    }
    fun deleteCityFromDataBase(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAllWeatherResponse()
        }
    }
    fun getCityFromDataBase(): Flow<WeatherResponse> {
        return repo.getWeatherResponseToHomeFragment()
    }


}
