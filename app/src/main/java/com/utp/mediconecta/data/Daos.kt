package com.utp.mediconecta.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun count(): Int

    @Query("SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(:email) LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(users: List<UserEntity>): List<Long>

    @Update
    suspend fun update(user: UserEntity)
}

@Dao
interface DirectoryDao {
    @Query("SELECT * FROM hospitales ORDER BY nombre")
    suspend fun getHospitals(): List<HospitalEntity>

    @Query("SELECT * FROM farmacias ORDER BY nombre")
    suspend fun getPharmacies(): List<PharmacyEntity>

    @Query("SELECT * FROM especialidades ORDER BY nombre")
    suspend fun getSpecialties(): List<SpecialtyEntity>

    @Query("SELECT * FROM doctores WHERE hospitalId = :hospitalId AND especialidadId = :specialtyId ORDER BY apellido, nombre")
    suspend fun getDoctors(hospitalId: Long, specialtyId: Long): List<DoctorEntity>

    @Query("SELECT * FROM doctores WHERE id = :id LIMIT 1")
    suspend fun getDoctor(id: Long): DoctorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospitals(items: List<HospitalEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPharmacies(items: List<PharmacyEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecialties(items: List<SpecialtyEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctors(items: List<DoctorEntity>): List<Long>
}

@Dao
interface AppointmentDao {
    @Query(
        """
        SELECT c.id, c.fecha, c.hora, c.motivo, c.estado, c.observaciones,
               c.doctorId AS doctorId,
               ('Dr. ' || d.nombre || ' ' || d.apellido) AS doctorNombre,
               e.nombre AS especialidadNombre,
               h.nombre AS hospitalNombre
        FROM citas c
        INNER JOIN doctores d ON c.doctorId = d.id
        INNER JOIN especialidades e ON d.especialidadId = e.id
        INNER JOIN hospitales h ON d.hospitalId = h.id
        WHERE c.usuarioId = :userId
        ORDER BY c.fecha DESC, c.hora DESC
        """
    )
    suspend fun getForUser(userId: Long): List<AppointmentListItem>

    @Query("SELECT * FROM citas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AppointmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AppointmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AppointmentEntity>)

    @Update
    suspend fun update(item: AppointmentEntity)

    @Query("UPDATE citas SET estado = 'CANCELADA' WHERE id = :id")
    suspend fun cancel(id: Long)
}

@Dao
interface HistoryDao {
    @Query(
        """
        SELECT hm.id, hm.fecha, hm.diagnostico, hm.tratamiento, hm.observaciones,
               ('Dr. ' || d.nombre || ' ' || d.apellido) AS doctorNombre,
               e.nombre AS especialidadNombre,
               h.nombre AS hospitalNombre
        FROM historial_medico hm
        INNER JOIN doctores d ON hm.doctorId = d.id
        INNER JOIN especialidades e ON d.especialidadId = e.id
        INNER JOIN hospitales h ON d.hospitalId = h.id
        WHERE hm.usuarioId = :userId
        ORDER BY hm.fecha DESC
        """
    )
    suspend fun getForUser(userId: Long): List<HistoryListItem>

    @Query(
        """
        SELECT hm.id, hm.fecha, hm.diagnostico, hm.tratamiento, hm.observaciones,
               ('Dr. ' || d.nombre || ' ' || d.apellido) AS doctorNombre,
               e.nombre AS especialidadNombre,
               h.nombre AS hospitalNombre
        FROM historial_medico hm
        INNER JOIN doctores d ON hm.doctorId = d.id
        INNER JOIN especialidades e ON d.especialidadId = e.id
        INNER JOIN hospitales h ON d.hospitalId = h.id
        WHERE hm.id = :id LIMIT 1
        """
    )
    suspend fun getDetail(id: Long): HistoryListItem?

    @Query("SELECT * FROM examenes WHERE historialId = :historyId ORDER BY fecha DESC")
    suspend fun getExams(historyId: Long): List<ExamEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(items: List<MedicalHistoryEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(items: List<ExamEntity>)
}

@Dao
interface MedicationDao {
    @Query(
        """
        SELECT m.id AS medicamentoId, m.nombre, m.descripcion, m.presentacion, m.requiereReceta,
               COALESCE(ih.cantidad, 0) AS cantidad,
               CASE WHEN COALESCE(ih.disponible, 0) = 1 AND COALESCE(ih.cantidad, 0) > 0 THEN 1 ELSE 0 END AS disponible
        FROM medicamentos m
        LEFT JOIN inventario_hospital ih
          ON ih.medicamentoId = m.id AND ih.hospitalId = :hospitalId
        WHERE LOWER(m.nombre) LIKE '%' || LOWER(:search) || '%'
        ORDER BY m.nombre
        """
    )
    suspend fun getAvailability(hospitalId: Long, search: String): List<MedicationAvailabilityItem>

    @Query(
        """
        SELECT f.id AS farmaciaId, f.nombre AS farmaciaNombre, f.direccion, f.telefono,
               f.latitud, f.longitud, inf.cantidad, inf.precio
        FROM inventario_farmacia inf
        INNER JOIN farmacias f ON inf.farmaciaId = f.id
        WHERE inf.medicamentoId = :medicationId AND inf.disponible = 1 AND inf.cantidad > 0
        ORDER BY inf.precio ASC
        """
    )
    suspend fun getPharmacyAvailability(medicationId: Long): List<PharmacyAvailabilityItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedications(items: List<MedicationEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospitalInventory(items: List<HospitalInventoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPharmacyInventory(items: List<PharmacyInventoryEntity>)
}
