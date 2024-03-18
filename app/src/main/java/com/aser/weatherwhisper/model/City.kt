package com.aser.weatherwhisper.model

import androidx.room.Entity
import org.jetbrains.annotations.NotNull

@Entity(tableName = "city_table", primaryKeys = ["cityName", "type"])
data class City(
    @NotNull
    val cityName: String,
    val longitude: Double,
    val latitude: Double,
    @NotNull
    var type: String
)
