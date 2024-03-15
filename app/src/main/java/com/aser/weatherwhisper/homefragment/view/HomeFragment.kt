package com.aser.weatherwhisper.homefragment.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.R
import com.aser.weatherwhisper.utils.Constants
import com.aser.weatherwhisper.databinding.FragmentHomeBinding
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import com.aser.weatherwhisper.homefragment.viewmodel.WeatherDetailsViewModel
import com.aser.weatherwhisper.homefragment.viewmodel.WeatherDetailsViewModelFactory
import com.aser.weatherwhisper.model.WeatherResponse
import com.aser.weatherwhisper.utils.ApiState
import com.aser.weatherwhisper.utils.Constants.Companion.LANG
import com.aser.weatherwhisper.utils.Constants.Companion.LANG_ENGLISH
import com.aser.weatherwhisper.utils.Constants.Companion.MEASUREMENT_UNIT
import com.aser.weatherwhisper.utils.Constants.Companion.METER
import com.aser.weatherwhisper.utils.Constants.Companion.MILE
import com.aser.weatherwhisper.utils.Constants.Companion.REQUEST_LOCATION_CODE
import com.aser.weatherwhisper.utils.Constants.Companion.SPEED_UNIT
import com.aser.weatherwhisper.utils.Constants.Companion.UNITS_CELSIUS
import com.aser.weatherwhisper.utils.Constants.Companion.UNITS_FAHRENHEIT
import com.aser.weatherwhisper.utils.Constants.Companion.UNITS_KELVIN
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: WeatherDetailsViewModel
    private lateinit var weatherDetailsFactory: WeatherDetailsViewModelFactory
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private var unit: String = Constants.UNITS_CELSIUS
    private var language: String = Constants.LANG_ENGLISH
    private var speed: String = Constants.METER

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        weatherDetailsFactory =
            WeatherDetailsViewModelFactory(WeatherRepository.getInstance(WeatherRemoteDataSource.instance))
        viewModel =
            ViewModelProvider(this, weatherDetailsFactory)[WeatherDetailsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences =
            requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

        unit = sharedPreferences.getString(MEASUREMENT_UNIT, UNITS_CELSIUS)!!

        language = sharedPreferences.getString(LANG, LANG_ENGLISH)!!

        speed = sharedPreferences.getString(SPEED_UNIT, METER)!!

        dailyAdapter = DailyAdapter(unit)
        hourlyAdapter = HourlyAdapter(unit)


        if (checkPermission()) {
            if (isLocationEnable()) {
                getFreshLocation()
            } else {
                enableLocationService()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_CODE
            )
        }

        binding.hourlyForecast.setOnClickListener {
            showHourlyForecast()
        }
        binding.weeklyForecast.setOnClickListener {
            showWeeklyForecast()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.weatherResponse.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar2.visibility = View.VISIBLE
                        binding.recycleViewForecast.visibility = View.INVISIBLE
                        binding.detailsLinear.visibility = View.INVISIBLE
                    }

                    is ApiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.progressBar2.visibility = View.GONE
                        binding.recycleViewForecast.visibility = View.VISIBLE
                        binding.detailsLinear.visibility = View.VISIBLE
                        updateUI(result.data)
                    }

                    else -> {
                        binding.recycleViewForecast.visibility = View.INVISIBLE
                        binding.detailsLinear.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "There is a problem with the server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun updateUI(weatherResponse: WeatherResponse) {
        val timezoneParts = weatherResponse.timezone.split("/")
        val cityName = timezoneParts.last()
        binding.countryText.text = cityName

        val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(weatherResponse.current.dt * 1000L))
        binding.dateText.text = formattedDate

        binding.degreeText.text = formatTemperature(
            weatherResponse.current.temp.toFloat(),
            unit
        )

        val iconCode = weatherResponse.current.weather.firstOrNull()?.icon ?: ""
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode.png"
        Glide.with(requireContext()).load(iconUrl).into(binding.weatherIcon)

        binding.weatherDescriptionText.text =
            weatherResponse.current.weather.firstOrNull()?.description ?: ""
        binding.pressureDis.text =
            String.format("%.0f hpa", weatherResponse.current.pressure.toFloat())
        binding.humidityDis.text =
            String.format("%.0f %%", weatherResponse.current.humidity.toFloat())
        binding.windDis.text =
            formatSpeed(speed, unit, weatherResponse.current.wind_speed.toFloat())
        binding.cloudDis.text =
            String.format("%.0f %%", weatherResponse.current.clouds.toFloat())
        binding.visibilityDis.text =
            String.format("%.1f m", weatherResponse.current.visibility.toFloat())

        dailyAdapter.submitList(weatherResponse.daily)
        hourlyAdapter.submitList(weatherResponse.hourly)
        showHourlyForecast()
    }

    private fun showHourlyForecast() {
        val recyclerView: RecyclerView = binding.recycleViewForecast
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = hourlyAdapter
        binding.weeklyForecast.setTextColor(Color.GRAY)
        binding.hourlyForecast.setTextColor(Color.WHITE)
    }

    private fun showWeeklyForecast() {
        val recyclerView: RecyclerView = binding.recycleViewForecast
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = dailyAdapter
        binding.weeklyForecast.setTextColor(Color.WHITE)
        binding.hourlyForecast.setTextColor(Color.GRAY)
    }

    private fun formatTemperature(temp: Float, unit: String): String {
        return when (unit) {
            UNITS_CELSIUS -> String.format("%.0f °C", temp)
            UNITS_KELVIN -> String.format("%.0f K", temp)
            else -> String.format("%.0f °F", temp)
        }
    }

    private fun formatSpeed(speedUnit: String, measurementUnit: String, speed: Float): String {

        return when {
            (measurementUnit == UNITS_KELVIN && speedUnit == METER) || (measurementUnit == UNITS_CELSIUS && speedUnit == METER) -> {
                String.format("%.0f m/s", speed)
            }

            (measurementUnit == UNITS_KELVIN && speedUnit == MILE) || (measurementUnit == UNITS_CELSIUS && speedUnit == MILE) -> {
                val speedInMilesPerHour = speed * 3600 / 1609.344
                String.format("%.0f mile/h", speedInMilesPerHour)
            }

            (measurementUnit == UNITS_FAHRENHEIT && speedUnit == MILE) -> {
                String.format("%.0f mile/h", speed)
            }

            else -> {
                val speedInMeterPerSec = speed * 1609.344 / 3600
                String.format("%.0f m/s", speedInMeterPerSec)
            }
        }
    }


    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun enableLocationService() {
        Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
        startActivity(intent)
    }

    private fun getFreshLocation() {
        if (checkPermission()) {
            if (isLocationEnable()) {
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(requireContext())
                try {
                    fusedLocationProviderClient.requestLocationUpdates(
                        //location request
                        LocationRequest.Builder(0).apply {

                            setIntervalMillis(30000)
                            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

                        }.build(),

                        //location CallBack
                        object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                val location = locationResult.lastLocation
                                if (location != null) {
                                    Log.i(
                                        "TAG",
                                        "onLocationResult: ${location.latitude} , ${location.longitude}"
                                    )
                                    viewModel.getWeatherDetails(
                                        location.latitude,
                                        location.longitude,
                                        language = language,
                                        units = unit
                                    )
                                }
                            }
                        },
                        Looper.myLooper()
                    )
                } catch (securityException: SecurityException) {
                    Toast.makeText(
                        requireContext(),
                        "Location permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                enableLocationService()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_CODE
            )
        }
    }
}