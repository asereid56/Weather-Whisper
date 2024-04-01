package com.aser.weatherwhisper.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.Current
import com.aser.weatherwhisper.model.Daily
import com.aser.weatherwhisper.model.Hourly
import com.aser.weatherwhisper.model.WeatherResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class CityDAOTest {
    private lateinit var dataBase: AppDataBase
    private lateinit var dao: CityDAO
    private lateinit var favCity: City
    private lateinit var alertCity: City
    private lateinit var weatherResponse: WeatherResponse

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dataBase = Room.inMemoryDatabaseBuilder(
            context, AppDataBase::class.java
        ).build()
        dao = dataBase.getCityDao()

        // initialize test data

        favCity = City("Damietta", 12.23, 12.23, "Fav")
        alertCity = City("New Damietta", 12.23, 12.23, "Alert")
        weatherResponse = WeatherResponse(
            Current(0, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0, emptyList(), 0, 0.0, 0.0),
            emptyList(),
            emptyList(),
            0.0,
            0.0,
            emptyList(),
            "UTC",
            0
        )
    }

    @After
    fun tearDown() {
        dataBase.close()
    }

    @Test
    fun insertCity_getFavCity_returnTrue() = runBlocking {
        //Given
        dao.insertCity(favCity)

        //When
        val result = dao.getAllFavCity().first()

        //Then
        assertThat(result, not(nullValue()))
        assertThat(result.size , `is`(1))
        assertThat(result.contains(favCity), `is`(true))
    }

    @Test
    fun insertCity_getAlertCity_returnTrue() = runBlocking {
        //Given
        dao.insertCity(alertCity)

        //When
        val result = dao.getALlAlertCity().first()

        //Then

        assertThat(result, not(nullValue()))
        assertThat(result.size , `is`(1))
        assertThat(result.contains(alertCity), `is`(true))
    }

    @Test
    fun insertCity_deleteIt_returnEmptyList() = runBlocking {
        //Given
        dao.insertCity(favCity)

        //When
        dao.deleteCity(favCity)

        //Then
        val result = dao.getAllFavCity().first()

        assertThat(result, `is`(emptyList()))

    }

    @Test
    fun insertWeatherResponse_getWeatherResponse() = runBlocking {
        //Given
        dao.insertWeatherResponse(weatherResponse)

        //When
        val result = dao.getFirstWeatherResponse().first()

        //Then
        assert(result == weatherResponse)

    }

    @Test
    fun insertWeatherResponse_deleteIt_ReturnNull() = runBlocking {
        //Given
        dao.insertWeatherResponse(weatherResponse)

        //When
        dao.deleteAllWeatherResponse()

        //Then
        val result = dao.getFirstWeatherResponse().first()
        assertThat(result, `is`(nullValue()))
    }
}