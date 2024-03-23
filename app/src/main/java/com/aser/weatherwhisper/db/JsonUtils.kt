package com.aser.weatherwhisper.db

import com.aser.weatherwhisper.model.WeatherResponse
import com.google.gson.Gson

object JsonUtils {
    private val gson = Gson()
    fun weatherResponseToJson(weatherResponse: WeatherResponse): String {
        return gson.toJson(weatherResponse)
    }

    fun jsonToWeatherResponse(json: String): WeatherResponse {
        return gson.fromJson(json, WeatherResponse::class.java)
    }
}