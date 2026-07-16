package com.utp.mediconecta.ui.citas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.utp.mediconecta.R
import com.utp.mediconecta.data.AppointmentListItem
import com.utp.mediconecta.databinding.ItemAppointmentBinding
import com.utp.mediconecta.util.DateUtils

class AppointmentAdapter(
    private val onEdit: (AppointmentListItem) -> Unit,
    private val onCancel: (AppointmentListItem) -> Unit
) : ListAdapter<AppointmentListItem, AppointmentAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemAppointmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppointmentListItem) = with(binding) {
            tvDay.text = DateUtils.day(item.fecha)
            tvMonth.text = DateUtils.month(item.fecha)
            tvDoctor.text = item.doctorNombre
            tvSpecialty.text = "${item.especialidadNombre} · ${item.hospitalNombre}"
            tvTime.text = "⏰ ${item.hora}"
            tvStatus.text = item.estado.lowercase().replaceFirstChar { it.uppercase() }

            val active = item.estado == "PROGRAMADA"
            actions.visibility = if (active) View.VISIBLE else View.GONE
            if (active) {
                tvStatus.setBackgroundResource(R.drawable.bg_badge_teal)
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.teal_700))
            } else if (item.estado == "CANCELADA") {
                tvStatus.setBackgroundResource(R.drawable.bg_badge_red)
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.red_600))
            } else {
                tvStatus.setBackgroundResource(R.drawable.bg_badge_gray)
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.gray_500))
            }
            btnEdit.setOnClickListener { onEdit(item) }
            btnCancel.setOnClickListener { onCancel(item) }
            root.setOnClickListener { if (active) onEdit(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<AppointmentListItem>() {
        override fun areItemsTheSame(oldItem: AppointmentListItem, newItem: AppointmentListItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AppointmentListItem, newItem: AppointmentListItem) = oldItem == newItem
    }
}
