// HourlyAdapter.kt
package com.aser.weatherwhisper.homefragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.databinding.HourDayItemBinding
import com.aser.weatherwhisper.model.Hourly
import com.aser.weatherwhisper.utils.Constants
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HourlyAdapter(private val unit: String) : ListAdapter<Hourly, HourlyAdapter.MyViewHolder>(HourlyDiffUtil()) {
    private lateinit var binding: HourDayItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourDayItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem: Hourly = getItem(position)

        val iconCode = currentItem.weather.firstOrNull()?.icon ?: ""
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode.png"
        Glide.with(holder.binding.icon.context).load(iconUrl).into(holder.binding.icon)

        val timestamp = (currentItem.dt * 1000).toLong()
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("hh a", Locale.getDefault())
        val formattedTime = dateFormat.format(date)
        holder.binding.hourDayText.text = formattedTime

        holder.binding.hourDayDegreeText.text = formatTemperature(currentItem.temp.toFloat(), unit)
    }

    private fun formatTemperature(temp: Float, unit: String): String {
        return when (unit) {
            Constants.UNITS_CELSIUS -> String.format("%.0f °C", temp)
            Constants.UNITS_KELVIN -> String.format("%.0f K", temp)
            else -> String.format("%.0f °F", temp)
        }
    }

    class MyViewHolder(var binding: HourDayItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class HourlyDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}
