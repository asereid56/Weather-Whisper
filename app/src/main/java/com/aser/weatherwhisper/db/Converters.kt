package com.aser.weatherwhisper.db

import androidx.room.TypeConverter
import com.aser.weatherwhisper.model.Current
import com.aser.weatherwhisper.model.Daily
import com.aser.weatherwhisper.model.Hourly
import com.aser.weatherwhisper.model.Minutely
import com.aser.weatherwhisper.model.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    @TypeConverter
    fun fromCurrent(current: Current): String {
        return Gson().toJson(current)
    }

    @TypeConverter
    fun toCurrent(json: String): Current {
        return Gson().fromJson(json, Current::class.java)
    }

    @TypeConverter
    fun fromDailyList(daily: List<Daily>): String {
        return Gson().toJson(daily)
    }

    @TypeConverter
    fun toDailyList(json: String): List<Daily> {
        val type = object : TypeToken<List<Daily>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun fromHourlyList(hourly: List<Hourly>): String {
        return Gson().toJson(hourly)
    }

    @TypeConverter
    fun toHourlyList(json: String): List<Hourly> {
        val type = object : TypeToken<List<Hourly>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun fromMinutelyList(minutely: List<Minutely>?): String {
        return Gson().toJson(minutely ?: emptyList<Minutely>())
    }


    @TypeConverter
    fun toMinutelyList(json: String): List<Minutely> {
        val type = object : TypeToken<List<Minutely>>() {}.type
        return Gson().fromJson(json, type)
    }

}