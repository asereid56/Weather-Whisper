package com.aser.weatherwhisper.favouritefragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.utils.ApiCityState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteCitiesViewModel(private val repository: WeatherRepository) : ViewModel() {
    private var _favCities: MutableStateFlow<ApiCityState> =
        MutableStateFlow<ApiCityState>(ApiCityState.Loading)
    val favCities: StateFlow<ApiCityState> = _favCities

    init {
        getFavCities()
    }

    private fun getFavCities() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavCities().catch { e ->
                _favCities.value = ApiCityState.Failure(e)
            }.collect { storedCities ->
                _favCities.value = ApiCityState.Success(storedCities)
            }
        }
    }

    fun removeFromFav(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCityFromFav(city)
            getFavCities()
        }
    }
}