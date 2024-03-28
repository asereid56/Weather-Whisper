package com.aser.weatherwhisper.mapFragment.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aser.weatherwhisper.FakeRepository
import com.aser.weatherwhisper.favouritefragment.viewmodel.FavouriteCitiesViewModel
import com.aser.weatherwhisper.favouritefragment.viewmodel.MainCoroutineRule
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.utils.ApiCityState
import com.aser.weatherwhisper.utils.ApiSearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MainCoroutineRule(val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) :
    TestWatcher(), TestCoroutineScope by TestCoroutineScope(dispatcher) {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}


class MapViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var repo: FakeRepository
    private lateinit var viewModel: MapViewModel
    private lateinit var cityFav: City
    private lateinit var cityAlert: City


    @Before
    fun setUp() {
        repo = FakeRepository()
        viewModel = MapViewModel(repo)
        cityFav = City("Damietta", 0.0, 0.0, "")
        cityAlert = City("Cairo", 0.0, 0.0, "")
    }


    @Test
    fun getCityDetails_return1() = runBlocking {

        //Given --> i inserted a City in FakeRepo
        val cityName = ""
        val lang = ""
        val unit = ""
        //When
        viewModel.getCityDetails(cityName, lang, unit)

        //Then
        val result = viewModel.cityResponse.first() as ApiSearchState.Success
        assertThat(result.data.size, `is`(1))
    }

    @Test
    fun addToFav_returnIt_GiveTrue() = runBlocking {
        //Given
        viewModel.addToFav(cityFav)

        //When
        val result = repo.getFavCities().first()

        //Then
        assertThat(result.size, `is`(1))
        assertThat(result.contains(cityFav), `is`(true))
    }

    @Test
    fun addToAlert_returnIt_GiveTrue() = runBlocking {
        //Given
        viewModel.addToAlert(cityAlert)

        //When
        val result = repo.getAlertCities().first()

        //Then
        assertThat(result.size, `is`(1))
        assertThat(result.contains(cityAlert), `is`(true))
    }
}