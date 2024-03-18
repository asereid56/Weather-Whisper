package com.aser.weatherwhisper.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aser.weatherwhisper.model.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDAO {
    @Query("SELECT * FROM city_table WHERE type = 'Fav'")
    fun getAllFavCity(): Flow<List<City>>

    @Query("SELECT * FROM city_table WHERE type = 'Alert'")
    fun getALlAlertCity(): Flow<List<City>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: City):Long

    @Delete
    suspend fun deleteCity(city: City):Int


}