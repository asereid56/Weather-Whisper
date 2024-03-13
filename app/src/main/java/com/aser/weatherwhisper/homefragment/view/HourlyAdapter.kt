package com.aser.weatherwhisper.homefragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.databinding.HourDayItemBinding
import com.aser.weatherwhisper.model.Daily
import com.aser.weatherwhisper.model.Hourly
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HourlyAdapter : ListAdapter<Hourly, HourlyAdapter.MyViewHolder>(HourlyDiffUtil()) {
    lateinit var binding: HourDayItemBinding
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

        binding.hourDayDegreeText.text = String.format("%.0f °C", currentItem.temp)


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