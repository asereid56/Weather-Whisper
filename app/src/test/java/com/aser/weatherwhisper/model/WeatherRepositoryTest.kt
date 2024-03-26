package com.aser.weatherwhisper.model

import com.aser.weatherwhisper.model.countryname.Clouds
import com.aser.weatherwhisper.model.countryname.Coord
import com.aser.weatherwhisper.model.countryname.Main
import com.aser.weatherwhisper.model.countryname.Sys
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import com.aser.weatherwhisper.model.countryname.Wind
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {
    private val city1 = City("cairo", 0.0, 0.0, "Fav")
    private val city2 = City("Alex", 0.0, 0.0, "Alert")
    private val weatherResponse = WeatherResponse(
        Current(0, 0.0, 0, 0.0, 0, 0, 0, 0, 0.0, 0.0, 0, emptyList(), 0, 0.0, 0.0),
        emptyList(),
        emptyList(),
        0.0,
        0.0,
        emptyList(),
        "UTC",
        0
    )
    private val weatherResponseCountry = WeatherResponseCountry(
        "", Clouds(12), 12, Coord(0.0, 0.0), 1, 1,
        Main(0.0, 1, 1, 0.0, 0.0, 0.0), "",
        Sys("", 1, 1, 1, 1), 1, 1,
        emptyList(),
        Wind(1, 0.0)
    )
    private val favCitiesList = listOf<City>(city1)
    private val alertCitiesList = listOf<City>(city2)
    private val weatherResponses = listOf<WeatherResponse>(weatherResponse)

    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var repository: WeatherRepository

    @Before
    fun setUp() {
        fakeLocalDataSource =
            FakeLocalDataSource(
                favCitiesList.toMutableList(),
                alertCitiesList.toMutableList(),
                weatherResponses.toMutableList()
            )
        fakeRemoteDataSource = FakeRemoteDataSource(weatherResponse, weatherResponseCountry)
        repository = WeatherRepository(
            fakeRemoteDataSource,
            fakeLocalDataSource,
        )
    }

    @Test
    fun getWeatherDetails() = runBlocking {
        // Given , setUp function above

        //when
        val result = repository.getWeatherDetails(0.0, 0.0, "", "")

        //Then
        assertThat(result.first(), `is`(weatherResponse))
    }

    @Test
    fun getWeatherByCities() = runBlocking {
        // Given , setUp function above

        //When
        val result = repository.getWeatherByCities("", "", "")

        //Then
        assertThat(result.first(), `is`(weatherResponseCountry))
    }

    @Test
    fun getFavCities() = runBlocking {
        // Given , setUp function above

        //When
        val result = repository.getFavCities()

        //Then
        assertThat(result.first(), `is`(favCitiesList))

    }

    @Test
    fun getAlertCities() = runBlocking {
        // Given , setUp function above

        //When
        val result = repository.getAlertCities()

        //Then
        assertThat(result.first(), `is`(alertCitiesList))
    }


    @Test
    fun insertCityToFav_getTheList_returnTrueIfItExist() = runBlocking {
        // Given, setUp function above

        // When
        repository.insertCityToFav(city1)

        // Then
        val result = repository.getFavCities().first()
        assertThat(result.contains(city1), `is`(true))
    }

    @Test
    fun insertCityToAlert_getTheList_returnTrueIfItExist() = runBlocking {
        // Given, setUp function above

        // When
        repository.insertCityToAlert(city2)

        // Then
        val alertCities = repository.getAlertCities().first()
        assertThat(alertCities.contains(city2), `is`(true))
    }

    @Test
    fun deleteCityFromFav() = runBlocking {
        // Given, setUp function above

        // When
        repository.deleteCityFromFav(city1)

        // Then
        val favCities = repository.getFavCities().first()
        assertThat(favCities, `is`(emptyList()))
    }

    @Test
    fun deleteCityFromAlert() = runBlocking {
        // Given, setUp function above

        // When
        repository.deleteCityFromAlert(city2)

        // Then
        val alertCities = repository.getAlertCities().first()
        assertThat(alertCities, `is`(emptyList()))
    }

    @Test
    fun insertWeatherResponse() = runBlocking {
        // Given, setUp function above

        // When
        repository.insertWeatherResponse(weatherResponse)

        // Then
        val result = repository.getWeatherResponseToHomeFragment().first()
        assertThat(result, `is`(weatherResponse))
    }

    @Test
    fun deleteAllWeatherResponse() = runBlocking {
        // Given, setUp function above

        // When
        repository.deleteAllWeatherResponse()

        // Then: You can assert the expected behavior here if the function has side effects
        val result = repository.getWeatherResponseToHomeFragment().firstOrNull()
        assertThat(result, `is`(nullValue()))
    }

    @Test
    fun getWeatherResponseToHomeFragment() = runBlocking {
        // Given, setUp function above

        // When
        val result = repository.getWeatherResponseToHomeFragment().first()

        // Then
        assertThat(result, `is`(weatherResponse))
    }

}