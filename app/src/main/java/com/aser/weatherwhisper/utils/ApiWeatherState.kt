package com.aser.weatherwhisper.utils

import com.aser.weatherwhisper.model.WeatherResponse

sealed class ApiWeatherState {
    class Success(val data: WeatherResponse) : ApiWeatherState()
    class Failure(val msg: Throwable) : ApiWeatherState()
    object Loading : ApiWeatherState()
}