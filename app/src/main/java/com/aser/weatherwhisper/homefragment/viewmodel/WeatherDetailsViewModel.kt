package com.aser.weatherwhisper.homefragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aser.weatherwhisper.utils.Constants
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.utils.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(private val repo: WeatherRepository) : ViewModel() {
    private val _weatherResponse: MutableStateFlow<ApiState> =
        MutableStateFlow<ApiState>(ApiState.Loading)
    val weatherResponse: StateFlow<ApiState> = _weatherResponse

//    fun getWeatherByLatAndLong(latitude: Double, longitude: Double) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val response = repo.getWeatherByLatAndLong(latitude, longitude)
//            _weatherResponse.postValue(response!!)
//        }
//    }

    fun getWeatherDetails(latitude: Double, longitude: Double, language: String, units: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherDetails(latitude, longitude, language, units).catch { e ->
                run {
                    _weatherResponse.value = ApiState.Failure(e)
                }
            }.collect { result ->
                _weatherResponse.value = ApiState.Success(result)

            }
        }
    }

}
