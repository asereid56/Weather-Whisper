package com.aser.weatherwhisper.setting.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.aser.weatherwhisper.R
import com.aser.weatherwhisper.databinding.FragmentSettingBinding
import com.aser.weatherwhisper.utils.Constants
import com.aser.weatherwhisper.utils.Constants.Companion.CURRENT_LOCATION
import com.aser.weatherwhisper.utils.Constants.Companion.LANG
import com.aser.weatherwhisper.utils.Constants.Companion.LANG_ARABIC
import com.aser.weatherwhisper.utils.Constants.Companion.LANG_ENGLISH
import com.aser.weatherwhisper.utils.Constants.Companion.LOCATION
import com.aser.weatherwhisper.utils.Constants.Companion.MAP
import com.aser.weatherwhisper.utils.Constants.Companion.MEASUREMENT_UNIT
import com.aser.weatherwhisper.utils.Constants.Companion.METER
import com.aser.weatherwhisper.utils.Constants.Companion.MILE
import com.aser.weatherwhisper.utils.Constants.Companion.SPEED_UNIT
import com.aser.weatherwhisper.utils.Constants.Companion.UNITS_CELSIUS
import com.aser.weatherwhisper.utils.Constants.Companion.UNITS_FAHRENHEIT
import com.aser.weatherwhisper.utils.Constants.Companion.UNITS_KELVIN
import java.util.Locale

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var unitsSharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        unitsSharedPref = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (unitsSharedPref.getString(MEASUREMENT_UNIT, UNITS_CELSIUS)) {
            UNITS_CELSIUS -> binding.btnCelsius.isChecked = true
            UNITS_KELVIN -> binding.btnKelvin.isChecked = true
            UNITS_FAHRENHEIT -> binding.btnFahrenheit.isChecked = true
        }
        when (unitsSharedPref.getString(LANG, LANG_ENGLISH)) {
            LANG_ENGLISH -> binding.btnEn.isChecked = true
            LANG_ARABIC -> binding.btnAr.isChecked = true
        }
        when (unitsSharedPref.getString(SPEED_UNIT, METER)) {
            METER -> binding.btnMeter.isChecked = true
            MILE -> binding.btnMile.isChecked = true
        }
        when (unitsSharedPref.getString(LOCATION, CURRENT_LOCATION)) {
            CURRENT_LOCATION -> binding.btnCurrentLocation.isChecked = true
            MAP -> binding.btnMap.isChecked = true
        }


        binding.btnAr.setOnClickListener {
            unitsSharedPref.edit().putString(LANG, LANG_ARABIC).apply()
            setLocale("ar")

        }

        binding.btnEn.setOnClickListener {
            unitsSharedPref.edit().putString(LANG, LANG_ENGLISH).apply()
            setLocale("en")
        }

        binding.btnCelsius.setOnClickListener {
            unitsSharedPref.edit().putString(MEASUREMENT_UNIT, UNITS_CELSIUS).apply()
        }

        binding.btnKelvin.setOnClickListener {
            unitsSharedPref.edit().putString(MEASUREMENT_UNIT, UNITS_KELVIN).apply()
        }

        binding.btnFahrenheit.setOnClickListener {
            unitsSharedPref.edit().putString(MEASUREMENT_UNIT, UNITS_FAHRENHEIT).apply()
        }

        binding.btnMeter.setOnClickListener {
            unitsSharedPref.edit().putString(SPEED_UNIT, METER).apply()

        }

        binding.btnMile.setOnClickListener {
            unitsSharedPref.edit().putString(SPEED_UNIT, MILE).apply()

        }
        binding.btnMap.setOnClickListener {
            unitsSharedPref.edit().putString(LOCATION, MAP).apply()
            val navController = NavHostFragment.findNavController(this@SettingFragment)
            navController.navigate(R.id.action_settingFragment_to_mapFragment)

        }
        binding.btnCurrentLocation.setOnClickListener {
            unitsSharedPref.edit().putString(LOCATION, CURRENT_LOCATION).apply()
            val navController = NavHostFragment.findNavController(this@SettingFragment)
            navController.navigate(R.id.action_settingFragment_to_homeFragment)
        }


    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)

        requireActivity().recreate()
    }

}

