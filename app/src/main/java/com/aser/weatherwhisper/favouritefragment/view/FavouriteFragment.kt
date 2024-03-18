package com.aser.weatherwhisper.favouritefragment.view

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
import com.aser.weatherwhisper.R
import com.aser.weatherwhisper.databinding.FragmentFavouriteBinding
import com.aser.weatherwhisper.db.CitiesLocalDataBase
import com.aser.weatherwhisper.favouritefragment.viewmodel.FavouriteCitiesViewModel
import com.aser.weatherwhisper.favouritefragment.viewmodel.FavouriteFactory
import com.aser.weatherwhisper.mapFragment.viewmodel.MapViewModelFactory
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import com.aser.weatherwhisper.utils.ApiCityState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment(), OnDeleteClickListener {
    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var viewModel: FavouriteCitiesViewModel
    private lateinit var viewModelFactory: FavouriteFactory
    private lateinit var favAdapter: FavAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        viewModelFactory = FavouriteFactory(
            WeatherRepository.getInstance(
                WeatherRemoteDataSource.instance,
                CitiesLocalDataBase(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[FavouriteCitiesViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favAdapter = FavAdapter(this)
        setUpRecycleView()

        lifecycleScope.launch {
            viewModel.favCities.collectLatest { result ->
                when (result) {
                    is ApiCityState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.FavRecycleView.visibility = View.INVISIBLE
                    }

                    is ApiCityState.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.FavRecycleView.visibility = View.VISIBLE
                        favAdapter.submitList(result.data)
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
        val recycleView: RecyclerView = binding.FavRecycleView
        recycleView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycleView.adapter = favAdapter

    }

    override fun onDeleteClick(city: City) {
        viewModel.removeFromFav(city)
    }

}