package com.utp.mediconecta.ui.medicamentos

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.utp.mediconecta.R
import com.utp.mediconecta.data.LocationItem
import com.utp.mediconecta.data.MedicationCardModel
import com.utp.mediconecta.databinding.ItemMedicationBinding

class MedicationAdapter(private val onPharmacyClick: (LocationItem) -> Unit) :
    ListAdapter<MedicationCardModel, MedicationAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemMedicationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemMedicationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MedicationCardModel) = with(binding) {
            val med = item.medication
            tvName.text = med.nombre
            tvDescription.text = "${med.descripcion} · ${med.presentacion}${if (med.requiereReceta) " · Requiere receta" else ""}"
            tvQuantity.text = if (med.disponible) "${med.cantidad} unidades en inventario" else "Sin existencias en este hospital"
            if (med.disponible) {
                tvStatus.text = "Disponible"
                tvStatus.setBackgroundResource(R.drawable.bg_badge_green)
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.green_600))
                llPharmacies.visibility = View.GONE
            } else {
                tvStatus.text = "No disponible"
                tvStatus.setBackgroundResource(R.drawable.bg_badge_red)
                tvStatus.setTextColor(ContextCompat.getColor(root.context, R.color.red_600))
                llPharmacies.visibility = View.VISIBLE
                pharmacyContainer.removeAllViews()
                if (item.pharmacies.isEmpty()) {
                    pharmacyContainer.addView(makePharmacyText("No hay farmacias registradas con disponibilidad", null))
                } else {
                    item.pharmacies.forEach { pharmacy ->
                        val text = "${pharmacy.farmaciaNombre} - ${pharmacy.cantidad} unidades - B/. ${"%.2f".format(pharmacy.precio)}"
                        pharmacyContainer.addView(makePharmacyText(text) {
                            onPharmacyClick(
                                LocationItem(
                                    id = pharmacy.farmaciaId,
                                    type = "FARMACIA",
                                    nombre = pharmacy.farmaciaNombre,
                                    direccion = pharmacy.direccion,
                                    telefono = pharmacy.telefono,
                                    latitud = pharmacy.latitud,
                                    longitud = pharmacy.longitud
                                )
                            )
                        })
                    }
                }
            }
        }

        private fun makePharmacyText(text: String, click: (() -> Unit)?): TextView {
            val density = binding.root.resources.displayMetrics.density
            return TextView(binding.root.context).apply {
                this.text = text
                textSize = 12f
                setTextColor(ContextCompat.getColor(context, R.color.gray_700))
                setTypeface(typeface, Typeface.BOLD)
                setBackgroundResource(R.drawable.bg_quick_action)
                setPadding((10 * density).toInt(), (9 * density).toInt(), (10 * density).toInt(), (9 * density).toInt())
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    bottomMargin = (6 * density).toInt()
                }
                isClickable = click != null
                setOnClickListener { click?.invoke() }
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<MedicationCardModel>() {
        override fun areItemsTheSame(oldItem: MedicationCardModel, newItem: MedicationCardModel) = oldItem.medication.medicamentoId == newItem.medication.medicamentoId
        override fun areContentsTheSame(oldItem: MedicationCardModel, newItem: MedicationCardModel) = oldItem == newItem
    }
}
