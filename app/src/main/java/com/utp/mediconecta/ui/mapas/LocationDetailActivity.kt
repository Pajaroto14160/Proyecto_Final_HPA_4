package com.utp.mediconecta.ui.mapas

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.utp.mediconecta.R
import com.utp.mediconecta.data.LocationItem
import com.utp.mediconecta.databinding.ActivityLocationDetailBinding

class LocationDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationDetailBinding
    private lateinit var location: LocationItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        location = LocationItem(
            id = intent.getLongExtra(EXTRA_ID, 0),
            type = intent.getStringExtra(EXTRA_TYPE).orEmpty(),
            nombre = intent.getStringExtra(EXTRA_NAME).orEmpty(),
            direccion = intent.getStringExtra(EXTRA_ADDRESS).orEmpty(),
            telefono = intent.getStringExtra(EXTRA_PHONE).orEmpty(),
            latitud = intent.getDoubleExtra(EXTRA_LAT, 0.0),
            longitud = intent.getDoubleExtra(EXTRA_LNG, 0.0)
        )
        val hospital = location.type == "HOSPITAL"
        binding.iconBox.setBackgroundResource(if (hospital) R.drawable.bg_pin_hospital else R.drawable.bg_pin_pharmacy)
        binding.ivIcon.setImageResource(if (hospital) R.drawable.ic_hospital else R.drawable.ic_pharmacy)
        binding.tvName.text = location.nombre
        binding.tvType.text = if (hospital) "HOSPITAL" else "FARMACIA"
        binding.tvAddress.text = location.direccion
        binding.tvPhone.text = location.telefono
        binding.btnBack.setOnClickListener { finish() }
        binding.btnCall.setOnClickListener { dial() }
        binding.btnRoute.setOnClickListener { openMaps() }
    }

    private fun dial() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${location.telefono}"))
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
        else Toast.makeText(this, "No hay aplicación de teléfono disponible", Toast.LENGTH_SHORT).show()
    }

    private fun openMaps() {
        val encodedName = Uri.encode(location.nombre)
        val markerUri = Uri.parse(
            "geo:${location.latitud},${location.longitud}" +
                "?q=${location.latitud},${location.longitud}($encodedName)"
        )

        val googleMaps = Intent(Intent.ACTION_VIEW, markerUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (googleMaps.resolveActivity(packageManager) != null) {
            startActivity(googleMaps)
            return
        }

        val genericMap = Intent(Intent.ACTION_VIEW, markerUri)
        if (genericMap.resolveActivity(packageManager) != null) {
            startActivity(genericMap)
        } else {
            Toast.makeText(this, "No hay una aplicación de mapas instalada", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val EXTRA_ID = "location_id"
        private const val EXTRA_TYPE = "location_type"
        private const val EXTRA_NAME = "location_name"
        private const val EXTRA_ADDRESS = "location_address"
        private const val EXTRA_PHONE = "location_phone"
        private const val EXTRA_LAT = "location_lat"
        private const val EXTRA_LNG = "location_lng"

        fun createIntent(context: Context, item: LocationItem): Intent = Intent(context, LocationDetailActivity::class.java).apply {
            putExtra(EXTRA_ID, item.id)
            putExtra(EXTRA_TYPE, item.type)
            putExtra(EXTRA_NAME, item.nombre)
            putExtra(EXTRA_ADDRESS, item.direccion)
            putExtra(EXTRA_PHONE, item.telefono)
            putExtra(EXTRA_LAT, item.latitud)
            putExtra(EXTRA_LNG, item.longitud)
        }
    }
}
