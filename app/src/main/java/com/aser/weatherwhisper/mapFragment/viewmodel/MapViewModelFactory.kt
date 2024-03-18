package com.aser.weatherwhisper.mapFragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aser.weatherwhisper.model.WeatherRepository

class MapViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(repository) as T
        } else {
            throw IllegalArgumentException("View Model Class Not Found")
        }
    }
}