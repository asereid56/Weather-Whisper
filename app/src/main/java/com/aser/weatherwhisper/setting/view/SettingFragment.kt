package com.aser.weatherwhisper.setting.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aser.weatherwhisper.databinding.FragmentSettingBinding
import com.aser.weatherwhisper.utils.Constants
import com.aser.weatherwhisper.utils.Constants.Companion.MEASUREMENT_UNIT
import com.aser.weatherwhisper.utils.Constants.Companion.SPEED_UNIT
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

        binding.btnAr.setOnClickListener {
            unitsSharedPref.edit().putString(Constants.LANG, Constants.LANG_ARABIC).apply()
            setLocale("ar")
        }

        binding.btnEn.setOnClickListener {
            unitsSharedPref.edit().putString(Constants.LANG, Constants.LANG_ENGLISH).apply()
            setLocale("en")
        }

        binding.btnCelsius.setOnClickListener {
            unitsSharedPref.edit().putString(MEASUREMENT_UNIT, Constants.UNITS_CELSIUS).apply()
        }

        binding.btnKelvin.setOnClickListener {
            unitsSharedPref.edit().putString(MEASUREMENT_UNIT, Constants.UNITS_KELVIN).apply()
        }

        binding.btnFahrenheit.setOnClickListener {
            unitsSharedPref.edit().putString(MEASUREMENT_UNIT, Constants.UNITS_FAHRENHEIT).apply()
        }

        binding.btnMeter.setOnClickListener {
            unitsSharedPref.edit().putString(SPEED_UNIT, Constants.METER).apply()
        }

        binding.btnMile.setOnClickListener {
            unitsSharedPref.edit().putString(SPEED_UNIT, Constants.MILE).apply()
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

