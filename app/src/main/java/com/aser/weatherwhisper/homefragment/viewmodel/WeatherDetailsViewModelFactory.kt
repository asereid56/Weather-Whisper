package com.aser.weatherwhisper.homefragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aser.weatherwhisper.model.WeatherRepository

class WeatherDetailsViewModelFactory(private val repo: WeatherRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WeatherDetailsViewModel::class.java)) {
            WeatherDetailsViewModel(repo) as T
        } else {
            throw IllegalArgumentException("View Model Class Not Found")
        }
    }
}