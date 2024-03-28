package com.aser.weatherwhisper.favouritefragment.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aser.weatherwhisper.FakeRepository
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.utils.ApiCityState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
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

class FavouriteCitiesViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var repo: FakeRepository
    private lateinit var viewModel: FavouriteCitiesViewModel


    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        repo = FakeRepository()
        viewModel = FavouriteCitiesViewModel(repo)
    }

    @Test
    fun insertCity_getFavCities_returnTrue() = runTest {
        val city1 = City("cairo", 0.0, 0.0, "Fav")
        //Given
        repo.insertCityToFav(city1)

        //When
        viewModel.getFavCities()

        //Then
        val favCitiesState = viewModel.favCities.first() as ApiCityState.Success

        assertThat(favCitiesState.data.size, `is`(1))
        assertThat(favCitiesState.data.contains(city1) , `is`(true))

    }


    @Test
    fun insertCity_removeFromFav_returnEmptyList() = runTest {
        val city2 = City("Damietta", 0.0, 0.0, "Fav")
        //Given
        repo.insertCityToFav(city2)

        //When
        async {
            viewModel.removeFromFav(city2)

        }.await()


        //Then
        val result = (viewModel.favCities.value as ApiCityState.Success).data.find { city ->
            city.cityName.equals(city2.cityName) && city.type.equals(city2.type)
        }

        assertThat(result, `is`(nullValue()))
    }
}