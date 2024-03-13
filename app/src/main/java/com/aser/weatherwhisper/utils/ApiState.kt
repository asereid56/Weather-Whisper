package com.aser.weatherwhisper.utils

import com.aser.weatherwhisper.model.WeatherResponse

sealed class ApiState {
    class Success(val data: WeatherResponse) : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    object Loading : ApiState()
}