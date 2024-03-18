package com.aser.weatherwhisper.utils

import com.aser.weatherwhisper.model.City

sealed class ApiCityState {
    class Success(val data: List<City>) : ApiCityState()
    class Failure(val msg: Throwable) : ApiCityState()
    object Loading : ApiCityState()
}