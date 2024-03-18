package com.aser.weatherwhisper.homefragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.databinding.HourDayItemBinding
import com.aser.weatherwhisper.model.Daily
import com.aser.weatherwhisper.utils.Constants
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyAdapter(private val unit: String) :
    ListAdapter<Daily, DailyAdapter.MyViewHolder>(DailyDiffUtil()) {
    private lateinit var binding: HourDayItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourDayItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem: Daily = getItem(position)

        val iconCode = currentItem.weather.firstOrNull()?.icon ?: ""
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode.png"
        Glide.with(holder.binding.icon.context).load(iconUrl).into(holder.binding.icon)

        val timestamp = (currentItem.dt * 1000).toLong()
        val date = Date(timestamp)
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val formattedDay = dayFormat.format(date)
        holder.binding.hourDayText.text = formattedDay

        binding.hourDayDegreeText.text = formatTemperature(currentItem.temp.day.toFloat() , unit)


    }

    class MyViewHolder(var binding: HourDayItemBinding) : RecyclerView.ViewHolder(binding.root)
}

private fun formatTemperature(temp: Float, unit: String): String {
    return when (unit) {
        Constants.UNITS_CELSIUS -> String.format("%.0f °C", temp)
        Constants.UNITS_KELVIN -> String.format("%.0f K", temp)
        else -> String.format("%.0f F" ,temp)
    }
}

class DailyDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.weather[0].id == newItem.weather[0].id
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }

}