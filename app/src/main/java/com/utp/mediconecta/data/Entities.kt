package com.utp.mediconecta.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios", indices = [Index(value = ["correo"], unique = true), Index(value = ["cedula"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val cedula: String,
    val correo: String,
    val passwordHash: String,
    val passwordSalt: String,
    val fechaNacimiento: String = "",
    val tipoSangre: String = "",
    val telefono: String = "",
    val contactoEmergencia: String = "",
    val relacionEmergencia: String = "",
    val telefonoEmergencia: String = "",
    val fotoUri: String? = null
)

@Entity(tableName = "hospitales")
data class HospitalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val latitud: Double,
    val longitud: Double
)

@Entity(tableName = "farmacias")
data class PharmacyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val latitud: Double,
    val longitud: Double
)

@Entity(tableName = "especialidades", indices = [Index(value = ["nombre"], unique = true)])
data class SpecialtyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String = ""
)

@Entity(
    tableName = "doctores",
    foreignKeys = [
        ForeignKey(entity = SpecialtyEntity::class, parentColumns = ["id"], childColumns = ["especialidadId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = HospitalEntity::class, parentColumns = ["id"], childColumns = ["hospitalId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("especialidadId"), Index("hospitalId")]
)
data class DoctorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val especialidadId: Long,
    val hospitalId: Long,
    val consultorio: String,
    val telefono: String
)

@Entity(
    tableName = "citas",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = DoctorEntity::class, parentColumns = ["id"], childColumns = ["doctorId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("usuarioId"), Index("doctorId")]
)
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val doctorId: Long,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val estado: String = "PROGRAMADA",
    val observaciones: String = ""
)

@Entity(
    tableName = "historial_medico",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["usuarioId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = DoctorEntity::class, parentColumns = ["id"], childColumns = ["doctorId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("usuarioId"), Index("doctorId")]
)
data class MedicalHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val doctorId: Long,
    val fecha: String,
    val diagnostico: String,
    val tratamiento: String,
    val observaciones: String = ""
)

@Entity(
    tableName = "examenes",
    foreignKeys = [ForeignKey(entity = MedicalHistoryEntity::class, parentColumns = ["id"], childColumns = ["historialId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("historialId")]
)
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val historialId: Long,
    val nombre: String,
    val fecha: String,
    val resultado: String,
    val archivoUri: String? = null
)

@Entity(tableName = "medicamentos", indices = [Index(value = ["nombre"], unique = true)])
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val presentacion: String,
    val requiereReceta: Boolean
)

@Entity(
    tableName = "inventario_hospital",
    foreignKeys = [
        ForeignKey(entity = HospitalEntity::class, parentColumns = ["id"], childColumns = ["hospitalId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MedicationEntity::class, parentColumns = ["id"], childColumns = ["medicamentoId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("hospitalId"), Index("medicamentoId"), Index(value = ["hospitalId", "medicamentoId"], unique = true)]
)
data class HospitalInventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hospitalId: Long,
    val medicamentoId: Long,
    val cantidad: Int,
    val disponible: Boolean,
    val fechaActualizacion: String
)

@Entity(
    tableName = "inventario_farmacia",
    foreignKeys = [
        ForeignKey(entity = PharmacyEntity::class, parentColumns = ["id"], childColumns = ["farmaciaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MedicationEntity::class, parentColumns = ["id"], childColumns = ["medicamentoId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("farmaciaId"), Index("medicamentoId"), Index(value = ["farmaciaId", "medicamentoId"], unique = true)]
)
data class PharmacyInventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmaciaId: Long,
    val medicamentoId: Long,
    val cantidad: Int,
    val disponible: Boolean,
    val precio: Double,
    val fechaActualizacion: String
)
