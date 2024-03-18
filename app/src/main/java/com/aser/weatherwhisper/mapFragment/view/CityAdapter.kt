package com.aser.weatherwhisper.mapFragment.view


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.databinding.CountryItemBinding
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.countryname.WeatherResponseCountry
import com.aser.weatherwhisper.utils.Constants

class CityAdapter(
    private val onFavClickListener: OnFavClickListener,
    private val onAlertClickListener: OnAlertClickListener
) : ListAdapter<WeatherResponseCountry, CityAdapter.MyViewHolder>(CityDiffUtil()) {

    private lateinit var binding: CountryItemBinding
    private lateinit var listener: OnItemCLickListener

    interface OnItemCLickListener {
        fun onItemCLick(weatherResponseCountry: WeatherResponseCountry)
    }

    fun onItemClickListenerToDetails(listener: OnItemCLickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CountryItemBinding.inflate(inflater, parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)
        binding.cityName.text = currentItem.name
        binding.cityDis.text = currentItem.weather.firstOrNull()?.description ?: ""
        binding.cityTemp.text = String.format("%.0f °C", currentItem.main.temp)
        binding.btnFav.setOnClickListener {
            var city = City(currentItem.name, currentItem.coord.lon, currentItem.coord.lat, "Fav")
            onFavClickListener.onCountryFavListener(city)
        }
        binding.btnAlert.setOnClickListener {
            onAlertClickListener.onCountryAlertListener(currentItem)
        }
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemCLick(currentItem)
            }
        }

    }

    class MyViewHolder(private var binding: CountryItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class CityDiffUtil : DiffUtil.ItemCallback<WeatherResponseCountry>() {
    override fun areItemsTheSame(
        oldItem: WeatherResponseCountry,
        newItem: WeatherResponseCountry
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: WeatherResponseCountry,
        newItem: WeatherResponseCountry
    ): Boolean {
        return oldItem == newItem
    }

}

private fun formatTemperature(temp: Float, unit: String): String {
    return when (unit) {
        Constants.UNITS_CELSIUS -> String.format("%.0f °C", temp)
        Constants.UNITS_KELVIN -> String.format("%.0f K", temp)
        else -> String.format("%.0f °F", temp)
    }
}