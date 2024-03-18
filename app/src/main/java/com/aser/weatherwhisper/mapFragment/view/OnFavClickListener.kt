package com.aser.weatherwhisper.mapFragment.view

import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry

interface OnFavClickListener {
    fun onCountryFavListener(city: City)

}