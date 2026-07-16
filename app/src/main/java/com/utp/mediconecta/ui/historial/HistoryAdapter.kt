package com.utp.mediconecta.ui.historial

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.utp.mediconecta.data.HistoryListItem
import com.utp.mediconecta.databinding.ItemHistoryBinding
import com.utp.mediconecta.util.DateUtils

class HistoryAdapter(private val onClick: (HistoryListItem) -> Unit) :
    ListAdapter<HistoryListItem, HistoryAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryListItem) = with(binding) {
            tvDiagnosis.text = item.diagnostico
            tvDoctor.text = "${item.doctorNombre} · ${item.especialidadNombre}"
            tvHospital.text = item.hospitalNombre
            tvDate.text = DateUtils.pretty(item.fecha)
            root.setOnClickListener { onClick(item) }
            btnDetails.setOnClickListener { onClick(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<HistoryListItem>() {
        override fun areItemsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem) = oldItem == newItem
    }
}
