package com.aser.weatherwhisper.alertfragment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.alertfragment.viewmodel.AlertFactory
import com.aser.weatherwhisper.alertfragment.viewmodel.AlertViewModel
import com.aser.weatherwhisper.databinding.FragmentAlertBinding
import com.aser.weatherwhisper.db.CitiesLocalDataBase
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import com.aser.weatherwhisper.utils.ApiCityState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertFragment : Fragment(), OnDeleteClickListener {
    private lateinit var binding: FragmentAlertBinding
    private lateinit var viewModel: AlertViewModel
    private lateinit var alertFactory: AlertFactory
    private lateinit var alertAdapter: AlertAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        alertFactory = AlertFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.instance,
                CitiesLocalDataBase.getInstance(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, alertFactory)[AlertViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertAdapter = AlertAdapter(this)
        setUpRecycleView()
        lifecycleScope.launch {
            viewModel.alertCities.collectLatest { result ->
                when (result) {
                    is ApiCityState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.alertRecycleView.visibility = View.INVISIBLE
                    }

                    is ApiCityState.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.alertRecycleView.visibility = View.VISIBLE
                        alertAdapter.submitList(result.data)
                    }

                    else -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "There's a problem with the server ",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        }
    }

    private fun setUpRecycleView() {
        val recyclerView: RecyclerView = binding.alertRecycleView
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = alertAdapter
    }

    override fun onDeleteClick(city: City) {
        viewModel.deleteFromAlertCities(city)
    }

}
