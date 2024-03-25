package com.aser.weatherwhisper.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherResponse

@Database(entities = [City::class , WeatherResponse::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getCityDao(): CityDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "city_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}