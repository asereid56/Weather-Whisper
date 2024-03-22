package com.aser.weatherwhisper.alertfragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.databinding.FavAlertItemBinding
import com.aser.weatherwhisper.model.City

class AlertAdapter(private val onDeleteClickListener: OnDeleteClickListener) :
    ListAdapter<City, AlertAdapter.MyViewModel>(AlertDiffUtil()) {
    lateinit var binding: FavAlertItemBinding
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onClickItem(city: City)
    }

    fun onItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertAdapter.MyViewModel {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavAlertItemBinding.inflate(inflater, parent, false)
        return MyViewModel(binding)
    }


    override fun onBindViewHolder(holder: MyViewModel, position: Int) {
        val currentItem = getItem(position)
        binding.cityName.text = currentItem.cityName
        binding.btnDelete.setOnClickListener {
            onDeleteClickListener.onDeleteClick(currentItem)
        }
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onClickItem(currentItem)
            }
        }
    }

    class MyViewModel(private val binding: FavAlertItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class AlertDiffUtil : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem.cityName == newItem.cityName
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }

}