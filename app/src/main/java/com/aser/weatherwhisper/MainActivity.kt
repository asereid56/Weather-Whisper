package com.aser.weatherwhisper

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.aser.weatherwhisper.databinding.ActivityMainBinding
import com.aser.weatherwhisper.utils.Constants
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNavigationBar
        navController = Navigation.findNavController(this, R.id.mainFragment)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId
            if (itemId == R.id.homeFragment) {
                navigateToFragment(R.id.homeFragment)
            } else if (itemId == R.id.favouriteFragment) {
                navigateToFragment(R.id.favouriteFragment)
            } else if (itemId == R.id.settingFragment) {
                navigateToFragment(R.id.settingFragment)
            } else if (itemId == R.id.alertFragment) {
                navigateToFragment(R.id.alertFragment)
            }
            true
        }


    }
    private fun navigateToFragment(fragmentId: Int) {
        if (navController.currentDestination?.id != fragmentId) {
            navController.popBackStack(R.id.homeFragment, false)
            navController.navigate(fragmentId)
        }
    }

    override fun onBackPressed() {
        if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val sharedPref = newBase!!.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val language =
            sharedPref?.getString(Constants.LANG, Constants.LANG_ENGLISH) ?: Constants.LANG_ENGLISH
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