package com.aser.weatherwhisper.utils

import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry

sealed class ApiSearchState {
    class Success(val data: List<WeatherResponseCountry>) : ApiSearchState()
    class Failure(val msg: Throwable) : ApiSearchState()
    object Loading : ApiSearchState()
}