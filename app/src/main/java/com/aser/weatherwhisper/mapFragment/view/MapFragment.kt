package com.aser.weatherwhisper.mapFragment.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.aser.weatherwhisper.databinding.FragmentMapBinding
import com.aser.weatherwhisper.db.CitiesLocalDataBase
import com.aser.weatherwhisper.mapFragment.viewmodel.MapViewModel
import com.aser.weatherwhisper.mapFragment.viewmodel.MapViewModelFactory
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import com.aser.weatherwhisper.utils.ApiSearchState
import com.aser.weatherwhisper.utils.Constants
import com.aser.weatherwhisper.utils.Constants.Companion.LANG_ENGLISH
import com.aser.weatherwhisper.utils.Constants.Companion.UNITS_CELSIUS
import com.aser.weatherwhisper.worker.AlertWorker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MapFragment : Fragment(), OnFavClickListener, OnAlertClickListener {
    lateinit var binding: FragmentMapBinding
    lateinit var viewModel: MapViewModel
    lateinit var mapFactory: MapViewModelFactory
    private lateinit var countryAdapter: CityAdapter
    private var unit: String = UNITS_CELSIUS
    private var language: String = LANG_ENGLISH
    private var selectedDate: String = ""
    private var selectedTime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        mapFactory =
            MapViewModelFactory(
                WeatherRepository.getInstance(
                    WeatherRemoteDataSource.instance,
                    CitiesLocalDataBase.getInstance(requireContext())
                )
            )
        viewModel = ViewModelProvider(this, mapFactory)[MapViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

        unit = sharedPreferences.getString(Constants.MEASUREMENT_UNIT, UNITS_CELSIUS)!!
        language = sharedPreferences.getString(Constants.LANG, LANG_ENGLISH)!!

        countryAdapter = CityAdapter(this, this)
        showCities()

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = 120
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.progressBarCity.visibility = View.VISIBLE
                val city = s.toString().trim()
                if (city.isNotEmpty()) {
                    retrieveCityDetails(city)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        observeCityDetails()

        countryAdapter.onItemClickListenerToDetails(object : CityAdapter.OnItemCLickListener {
            override fun onItemCLick(weatherResponseCountry: WeatherResponseCountry) {
                navigateToHomeFragment(weatherResponseCountry)
            }
        })
    }

    private fun navigateToHomeFragment(weatherResponseCountry: WeatherResponseCountry) {
        val cityName = weatherResponseCountry.name
        val longitude = weatherResponseCountry.coord.lon.toFloat()
        val latitude = weatherResponseCountry.coord.lat.toFloat()
        Log.i(
            "TAG",
            "navigateToHomeFragment: ${weatherResponseCountry.name}${weatherResponseCountry.coord.lon} ${weatherResponseCountry.coord.lat} "
        )
        val action =
            MapFragmentDirections.actionMapFragmentToHomeFragment(longitude, latitude)
        action.setCityName(cityName)
        findNavController().navigate(action)
    }


    private fun retrieveCityDetails(city: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getCityDetails(
                city = city,
                language = language,
                units = unit
            )
        }
    }

    private fun showCities() {
        val recyclerView: RecyclerView = binding.citiesRecycleView
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = countryAdapter
    }

    private fun observeCityDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cityResponse.collect { result ->
                when (result) {
                    is ApiSearchState.Loading -> {
                        binding.progressBarCity.visibility = View.VISIBLE
                        binding.citiesRecycleView.visibility = View.INVISIBLE
                    }

                    is ApiSearchState.Success -> {
                        binding.progressBarCity.visibility = View.INVISIBLE
                        binding.citiesRecycleView.visibility = View.VISIBLE
                        countryAdapter.submitList(result.data)
                    }

                    else -> {
                        binding.progressBarCity.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "There is a problem in the server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    override fun onCountryAlertListener(city: City) {
        showDateDialog(city)
        viewModel.addToAlert(city)

    }


    override fun onCountryFavListener(city: City) {
        viewModel.addToFav(city)
    }

    private fun showDateDialog(city: City) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Corrected date format: yyyy-MM-dd
                selectedDate =
                    String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth)
                // After selecting the date, show the time picker dialog
                showTimeDialog(city)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    private fun showTimeDialog(city: City) {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                // Format the selected time
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                // Construct the final date-time string
                val dateTime = "$selectedDate $selectedTime"

                // Convert the date-time string to milliseconds
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val selectedMillis = sdf.parse(dateTime)?.time ?: return@OnTimeSetListener

                try {
                    val latitude = city.latitude
                    val longitude = city.longitude

                    // Pass the date-time and other parameters to the WorkManager
                    val inputData = workDataOf(
                        AlertWorker.KEY_LONGITUDE to longitude,
                        AlertWorker.KEY_LATITUDE to latitude,
                        AlertWorker.KEY_LANG to language,
                        AlertWorker.KEY_UNIT to unit,
                    )

                    val alertWorkRequest = OneTimeWorkRequestBuilder<AlertWorker>()
                        .setInputData(inputData)
                        .setInitialDelay(
                            selectedMillis - System.currentTimeMillis(),
                            java.util.concurrent.TimeUnit.MILLISECONDS
                        )
                        .build()

                    // Enqueue the WorkManager task
                    WorkManager.getInstance(requireContext()).enqueue(alertWorkRequest)

                    Log.d("MapFragment", "Alert work request enqueued successfully")
                } catch (e: ParseException) {
                    e.printStackTrace()
                    Log.e("MapFragment", "Error parsing date: ${e.message}", e)
                }
            },
            0,
            0,
            false
        )
        timePickerDialog.show()
    }

}