package com.aser.weatherwhisper.favouritefragment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.R
import com.aser.weatherwhisper.databinding.FragmentFavouriteBinding
import com.aser.weatherwhisper.db.CitiesLocalDataBase
import com.aser.weatherwhisper.favouritefragment.viewmodel.FavouriteCitiesViewModel
import com.aser.weatherwhisper.favouritefragment.viewmodel.FavouriteFactory
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import com.aser.weatherwhisper.utils.ApiCityState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
                        binding.favRecycleView.visibility = View.INVISIBLE
                        binding.lottie.visibility = View.VISIBLE
                        binding.lottie.setAnimation(R.raw.heart)
                        binding.lottie.playAnimation()
                    }

                    is ApiCityState.Success -> {
                        if (result.data.isNullOrEmpty()) {
                            binding.lottie.visibility = View.VISIBLE
                            binding.lottie.setAnimation(R.raw.heart)
                            binding.lottie.playAnimation()
                            binding.favRecycleView.visibility = View.INVISIBLE

                        } else {
                            binding.favRecycleView.visibility = View.VISIBLE
                            favAdapter.submitList(result.data)
                            binding.lottie.visibility = View.GONE
                        }
                    }

                    else -> {
                        Toast.makeText(
                            requireContext(),
                            "There's a problem with the server ",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.lottie.visibility = View.VISIBLE
                        binding.lottie.setAnimation(R.raw.heart)
                        binding.lottie.playAnimation()
                    }
                }
            }
        }
        favAdapter.onItemClickListener(object : FavAdapter.OnItemClickListener {
            override fun onClickItem(city: City) {
                navigateToHomeFragment(city)
            }

        })
        binding.btnAdd.setOnClickListener {
            val navController = NavHostFragment.findNavController(this@FavouriteFragment)
            navController.navigate(R.id.action_favouriteFragment_to_mapFragment)
        }
    }

    private fun navigateToHomeFragment(city: City) {
        val action = FavouriteFragmentDirections.actionFavouriteFragmentToHomeFragment(
            city.longitude.toFloat(),
            city.latitude.toFloat()
        )
        action.cityName = city.cityName
        findNavController().navigate(action)
    }

    private fun setUpRecycleView() {
        val recycleView: RecyclerView = binding.favRecycleView
        recycleView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycleView.adapter = favAdapter

    }

    override fun onDeleteClick(city: City) {
        val dialogView = layoutInflater.inflate(R.layout.cancel_delete_fav, null)

        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView).setBackground(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.dialog_background,
                    requireActivity().theme
                )
            )


        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)


        var alertDialog = alertDialogBuilder.create()
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnDelete.setOnClickListener {
            viewModel.removeFromFav(city)
            Toast.makeText(requireContext(), R.string.favDeleted, Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

}