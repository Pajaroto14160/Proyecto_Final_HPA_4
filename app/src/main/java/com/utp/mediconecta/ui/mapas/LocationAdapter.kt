package com.utp.mediconecta.ui.mapas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.utp.mediconecta.R
import com.utp.mediconecta.data.LocationItem
import com.utp.mediconecta.databinding.ItemLocationBinding

class LocationAdapter(private val onClick: (LocationItem) -> Unit) :
    ListAdapter<LocationItem, LocationAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocationItem) = with(binding) {
            val hospital = item.type == "HOSPITAL"
            iconBox.setBackgroundResource(if (hospital) R.drawable.bg_pin_hospital else R.drawable.bg_pin_pharmacy)
            ivIcon.setImageResource(if (hospital) R.drawable.ic_hospital else R.drawable.ic_pharmacy)
            tvName.text = item.nombre
            tvAddress.text = item.direccion
            tvType.text = if (hospital) "HOSPITAL" else "FARMACIA"
            root.setOnClickListener { onClick(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<LocationItem>() {
        override fun areItemsTheSame(oldItem: LocationItem, newItem: LocationItem) = oldItem.id == newItem.id && oldItem.type == newItem.type
        override fun areContentsTheSame(oldItem: LocationItem, newItem: LocationItem) = oldItem == newItem
    }
}
