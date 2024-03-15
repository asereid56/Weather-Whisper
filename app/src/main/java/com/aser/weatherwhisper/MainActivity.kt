package com.aser.weatherwhisper

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.aser.weatherwhisper.databinding.ActivityMainBinding
import com.aser.weatherwhisper.utils.Constants
import org.intellij.lang.annotations.Language
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationBar
        val navController = Navigation.findNavController(this, R.id.mainFragment)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

    }

    override fun attachBaseContext(newBase: Context?) {
        val sharedPref = newBase!!.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val language = sharedPref?.getString(Constants.LANG, Constants.LANG_ENGLISH)?: Constants.LANG_ENGLISH
        val update = updateBaseContextLocale(newBase, language)
        super.attachBaseContext(update)
    }

    private fun updateBaseContextLocale(context: Context, language: String): Context {
        val local = Locale(language)
        Locale.setDefault(local)

        val config = Configuration(context.resources.configuration)
        config.setLocale(local)
        return context.createConfigurationContext(config)
    }
}