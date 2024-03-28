package com.aser.weatherwhisper.mapFragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.IWeatherRepository
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import com.aser.weatherwhisper.utils.ApiSearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapViewModel(private val repository: IWeatherRepository) : ViewModel() {
    private val _cityResponse: MutableStateFlow<ApiSearchState> =
        MutableStateFlow<ApiSearchState>(ApiSearchState.Loading)

    val cityResponse: StateFlow<ApiSearchState> = _cityResponse

    fun getCityDetails(city: String, language: String, units: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getWeatherByCities(city, language, units)
                val resultList = mutableListOf<WeatherResponseCountry>()
                result.collect { item ->
                    if (item is WeatherResponseCountry) {
                        resultList.add(item)
                    }
                }
                _cityResponse.value = ApiSearchState.Success(resultList)
            } catch (e: Exception) {
                _cityResponse.value = ApiSearchState.Failure(e)
            }
        }
    }

    fun addToFav(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCityToFav(city)
        }
    }

    fun addToAlert(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCityToAlert(city)
        }
    }

}