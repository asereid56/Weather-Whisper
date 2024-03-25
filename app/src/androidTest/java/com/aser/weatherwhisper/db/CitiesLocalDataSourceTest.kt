package com.aser.weatherwhisper.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.Current
import com.aser.weatherwhisper.model.WeatherResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class CitiesLocalDataSourceTest {
    private lateinit var dataBase: AppDataBase
    private lateinit var localDataBase: CitiesLocalDataSource
    private lateinit var dao: CityDAO
    private lateinit var favCity: City
    private lateinit var alertCity: City
    private lateinit var weatherResponse: WeatherResponse

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dataBase = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java).build()
        dao = dataBase.getCityDao()
        localDataBase = CitiesLocalDataSource(context)
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
    fun insertCityToFav_getCityFromFav_returnTrue() = runBlocking {
        //Given
        dao.insertCity(favCity)

        //When
        val result = dao.getAllFavCity().first()

        //Then
        assertThat(result.contains(favCity), `is`(true))
        assertThat(result.size, `is`(1))
    }

    @Test
    fun insertCityToAlert_getAllAlertCities_returnTrue() = runBlocking {
        //Given
        dao.insertCity(alertCity)

        //When
        val result = dao.getALlAlertCity().first()

        //Then
        assertThat(result.contains(alertCity), `is`(true))
        assertThat(result.size, `is`(1))
    }

    @Test
    fun insertCity_deleteIt_returnTrue() = runBlocking {
        //Given
        dao.insertCity(favCity)

        //When
        dao.deleteCity(favCity)

        //Then
        val result = dao.getAllFavCity().first()
        assertThat(result.isEmpty(), `is`(true))
    }

    @Test
    fun insertWeatherResponse_getIt_theyAreEqual() = runBlocking {
        //Given
        dao.insertWeatherResponse(weatherResponse)

        //When
        val result = dao.getFirstWeatherResponse()

        //Then
        assertThat(result.first(), `is`(weatherResponse))
    }

    @Test
    fun insertWeatherResponse_deleteIt_returnNullValue() = runBlocking {
        //Given
        dao.insertWeatherResponse(weatherResponse)

        //When
        dao.deleteAllWeatherResponse()

        //Then
        val result = dao.getFirstWeatherResponse().first()
        assertThat(result, `is`(nullValue()))
    }


}