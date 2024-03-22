package com.aser.weatherwhisper.alertfragment.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.utils.ApiCityState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _alertCities: MutableStateFlow<ApiCityState> =
        MutableStateFlow<ApiCityState>(ApiCityState.Loading)
    val alertCities: StateFlow<ApiCityState> = _alertCities

    init {
        getLocalAlertCities()
    }

    private fun getLocalAlertCities() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAlertCities().catch { e ->
                _alertCities.value = ApiCityState.Failure(e)
            }.collectLatest { result ->
                _alertCities.value = ApiCityState.Success(result)
            }
        }
    }

    fun deleteFromAlertCities(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCityFromAlert(city)
            getLocalAlertCities()
        }
    }

    fun cancelWorkForAlert(city: City, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.cancelWorkerForCity(city, context)
        }
    }
}