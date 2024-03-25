package com.aser.weatherwhisper.mapFragment.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.aser.weatherwhisper.R
import com.aser.weatherwhisper.databinding.FragmentMapBinding
import com.aser.weatherwhisper.db.CitiesLocalDataSource
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MapFragment : Fragment(), OnFavClickListener, OnAlertClickListener, OnMapReadyCallback,
    OnMapClickListener {
    lateinit var binding: FragmentMapBinding
    lateinit var viewModel: MapViewModel
    lateinit var mapFactory: MapViewModelFactory
    private lateinit var countryAdapter: CityAdapter
    private var unit: String = UNITS_CELSIUS
    private var language: String = LANG_ENGLISH
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var mGoogleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        mapFactory =
            MapViewModelFactory(
                WeatherRepository.getInstance(
                    WeatherRemoteDataSource.instance,
                    CitiesLocalDataSource.getInstance(requireContext())
                )
            )
        viewModel = ViewModelProvider(this, mapFactory)[MapViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDetailsOfTheUnitsAndLanguageFromSetting()

        countryAdapter = CityAdapter(unit, this, this)
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
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun navigateToHomeFragment(weatherResponseCountry: WeatherResponseCountry) {
        val cityName = weatherResponseCountry.name
        val longitude = weatherResponseCountry.coord.lon.toFloat()
        val latitude = weatherResponseCountry.coord.lat.toFloat()

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
                        binding.citiesRecycleView.visibility = View.INVISIBLE
                    }

                    is ApiSearchState.Success -> {
                        binding.progressBarCity.visibility = View.INVISIBLE
                        binding.citiesRecycleView.visibility = View.VISIBLE
                        countryAdapter.submitList(result.data)
                        val cityDetails = result.data
                        if (cityDetails.isNotEmpty()) {
                            val latitude = cityDetails[0].coord.lat
                            val longitude = cityDetails[0].coord.lon
                            addMarkerToMap(latitude, longitude, cityDetails[0].name)
                        }
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
    }


    override fun onCountryFavListener(city: City) {
        viewModel.addToFav(city)
        Toast.makeText(requireContext(), "Added to favourite", Toast.LENGTH_SHORT).show()
    }

    private fun showDateDialog(city: City) {
        val calender = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate =
                    String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth)
                showTimeDialog(city)
            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = calender.timeInMillis
        datePickerDialog.show()
    }


    private fun showTimeDialog(city: City) {
        val uniqueKey = city.cityName + city.type

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                val dateTime = "$selectedDate $selectedTime"

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val selectedMillis = sdf.parse(dateTime)?.time ?: return@OnTimeSetListener

                try {
                    val latitude = city.latitude
                    val longitude = city.longitude
                    val cityName = city.cityName

                    // Pass the parameters to the WorkManager
                    val inputData = workDataOf(
                        AlertWorker.KEY_LONGITUDE to longitude,
                        AlertWorker.KEY_LATITUDE to latitude,
                        AlertWorker.KEY_LANG to language,
                        AlertWorker.KEY_UNIT to unit,
                        AlertWorker.KEY_CITY_NAME to cityName
                    )

                    val alertWorkRequest = OneTimeWorkRequestBuilder<AlertWorker>()
                        .setInputData(inputData)
                        .setInitialDelay(
                            selectedMillis - System.currentTimeMillis(),
                            java.util.concurrent.TimeUnit.MILLISECONDS
                        )
                        .addTag(uniqueKey)
                        .build()

                    // Enqueue the WorkManager task
                    WorkManager.getInstance(requireContext()).enqueue(alertWorkRequest)

                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                Toast.makeText(requireContext(), "Added to alert", Toast.LENGTH_SHORT).show()
                viewModel.addToAlert(city)
            },
            0,
            0,
            false
        )
        timePickerDialog.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.setOnMapClickListener(this)
    }

    private fun addMarkerToMap(latitude: Double, longitude: Double, cityName: String) {
        val latLng = LatLng(latitude, longitude)
        mGoogleMap?.clear()
        val markerOptions = MarkerOptions().position(latLng).title(cityName)
        mGoogleMap?.addMarker(markerOptions)
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9f))
    }

    override fun onMapClick(latLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val cityName = addresses[0].locality
                cityName?.let {
                    mGoogleMap?.clear()
                    addMarkerToMap(latLng.latitude, latLng.longitude, cityName)
                    binding.searchText.text.clear()
                    retrieveCityDetails(cityName)
                }
            } else {
                Toast.makeText(requireContext(), "City not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Check your internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDetailsOfTheUnitsAndLanguageFromSetting() {
        val sharedPreferences =
            requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

        unit = sharedPreferences.getString(Constants.MEASUREMENT_UNIT, UNITS_CELSIUS)!!
        language = sharedPreferences.getString(Constants.LANG, LANG_ENGLISH)!!
    }

}