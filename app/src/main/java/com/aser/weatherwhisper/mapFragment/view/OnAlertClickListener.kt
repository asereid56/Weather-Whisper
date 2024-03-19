package com.aser.weatherwhisper.mapFragment.view

import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry

interface OnAlertClickListener {
    fun onCountryAlertListener(city: City)
}