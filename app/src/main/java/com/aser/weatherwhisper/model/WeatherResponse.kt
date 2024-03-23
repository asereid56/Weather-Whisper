package com.aser.weatherwhisper.model

import androidx.room.Entity

@Entity(tableName = "home_city_table" , primaryKeys = ["lat" , "lon"])
data class WeatherResponse(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val minutely: List<Minutely>,
    val timezone: String,
    val timezone_offset: Int
)