package com.aser.weatherwhisper.alertfragment.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.R
import com.aser.weatherwhisper.alertfragment.viewmodel.AlertFactory
import com.aser.weatherwhisper.alertfragment.viewmodel.AlertViewModel
import com.aser.weatherwhisper.databinding.FragmentAlertBinding
import com.aser.weatherwhisper.db.CitiesLocalDataBase
import com.aser.weatherwhisper.favouritefragment.view.FavAdapter
import com.aser.weatherwhisper.favouritefragment.view.FavouriteFragmentDirections
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import com.aser.weatherwhisper.utils.ApiCityState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
                        binding.alertRecycleView.visibility = View.INVISIBLE
                        binding.lottie.visibility = View.VISIBLE
                        binding.lottie.setAnimation(R.raw.alarm)
                        binding.lottie.playAnimation()
                    }

                    is ApiCityState.Success -> {
                        if (result.data.isNullOrEmpty()) {
                            binding.lottie.visibility = View.VISIBLE
                            binding.lottie.setAnimation(R.raw.alarm)
                            binding.lottie.playAnimation()
                            binding.alertRecycleView.visibility = View.INVISIBLE

                        } else {
                            binding.alertRecycleView.visibility = View.VISIBLE
                            alertAdapter.submitList(result.data)
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
                        binding.lottie.setAnimation(R.raw.alarm)
                        binding.lottie.playAnimation()

                    }
                }
            }
        }
        alertAdapter.onItemClickListener(object : AlertAdapter.OnItemClickListener {
            override fun onClickItem(city: City) {
                navigateToHomeFragment(city)
            }

        })
        binding.btnAdd.setOnClickListener {
            val navController = NavHostFragment.findNavController(this@AlertFragment)
            navController.navigate(R.id.action_alertFragment_to_mapFragment)
        }
    }

    private fun setUpRecycleView() {
        val recyclerView: RecyclerView = binding.alertRecycleView
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = alertAdapter
    }

    private fun navigateToHomeFragment(city: City) {
        val action = AlertFragmentDirections.actionAlertFragmentToHomeFragment(
            city.longitude.toFloat(),
            city.latitude.toFloat()
        )
        action.cityName = city.cityName
        findNavController().navigate(action)
    }


    override fun onDeleteClick(city: City) {
        val dialogView = layoutInflater.inflate(R.layout.cancel_delete_alert, null)

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

        val alertDialog = alertDialogBuilder.create()

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnDelete.setOnClickListener {
            viewModel.deleteFromAlertCities(city)
            viewModel.cancelWorkForAlert(city, requireContext())
            Toast.makeText(requireContext(), R.string.alertDeleted, Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }
        alertDialog.show()
    }


}
