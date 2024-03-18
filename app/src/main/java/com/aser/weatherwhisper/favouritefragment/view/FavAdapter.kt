package com.aser.weatherwhisper.favouritefragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aser.weatherwhisper.databinding.FavAlertItemBinding
import com.aser.weatherwhisper.model.City

class FavAdapter(private val onDeleteClickListener: OnDeleteClickListener) :
    ListAdapter<City, FavAdapter.MyViewHolder>(FavDiffUtil()) {

    lateinit var binding: FavAlertItemBinding
    lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onClickItem(city: City)
    }

    fun onItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavAlertItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = getItem(position)
        binding.cityName.text = current.cityName
        binding.btnDelete.setOnClickListener {
            onDeleteClickListener.onDeleteClick(current)
        }
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onClickItem(current)
            }
        }
    }

    class MyViewHolder(private var binding: FavAlertItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class FavDiffUtil : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem.type == newItem.type
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }
}