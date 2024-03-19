package com.aser.weatherwhisper.alertfragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aser.weatherwhisper.model.WeatherRepository

class AlertFactory(private val repository: WeatherRepository) :ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(repository) as T
        }else{
            throw IllegalArgumentException("View Model Class Not Found")
        }
    }
}