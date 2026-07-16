package com.utp.mediconecta.data

data class AppointmentListItem(
    val id: Long,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val estado: String,
    val observaciones: String,
    val doctorId: Long,
    val doctorNombre: String,
    val especialidadNombre: String,
    val hospitalNombre: String
)

data class HistoryListItem(
    val id: Long,
    val fecha: String,
    val diagnostico: String,
    val tratamiento: String,
    val observaciones: String,
    val doctorNombre: String,
    val especialidadNombre: String,
    val hospitalNombre: String
)

data class MedicationAvailabilityItem(
    val medicamentoId: Long,
    val nombre: String,
    val descripcion: String,
    val presentacion: String,
    val requiereReceta: Boolean,
    val cantidad: Int,
    val disponible: Boolean
)

data class PharmacyAvailabilityItem(
    val farmaciaId: Long,
    val farmaciaNombre: String,
    val direccion: String,
    val telefono: String,
    val latitud: Double,
    val longitud: Double,
    val cantidad: Int,
    val precio: Double
)

data class MedicationCardModel(
    val medication: MedicationAvailabilityItem,
    val pharmacies: List<PharmacyAvailabilityItem>
)

data class LocationItem(
    val id: Long,
    val type: String,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val latitud: Double,
    val longitud: Double
)
