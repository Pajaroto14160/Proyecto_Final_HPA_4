package com.utp.mediconecta.data

import com.utp.mediconecta.util.PasswordHasher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AppRepository(private val db: AppDatabase) {
    private val seedMutex = Mutex()
    @Volatile private var seeded = false

    suspend fun ensureSeeded() {
        if (seeded) return
        seedMutex.withLock {
            if (!seeded) {
                DatabaseSeeder.seedIfNeeded(db)
                seeded = true
            }
        }
    }

    suspend fun login(email: String, password: String): UserEntity? {
        ensureSeeded()
        val user = db.userDao().findByEmail(email.trim()) ?: return null
        return if (PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)) user else null
    }

    suspend fun register(nombre: String, apellido: String, cedula: String, email: String, password: String): Result<UserEntity> {
        ensureSeeded()
        if (db.userDao().findByEmail(email.trim()) != null) return Result.failure(IllegalArgumentException("El correo ya está registrado"))
        return runCatching {
            val salt = PasswordHasher.createSalt()
            val user = UserEntity(
                nombre = nombre.trim(),
                apellido = apellido.trim(),
                cedula = cedula.trim(),
                correo = email.trim().lowercase(),
                passwordHash = PasswordHasher.hash(password, salt),
                passwordSalt = salt
            )
            val id = db.userDao().insert(user)
            user.copy(id = id)
        }
    }

    suspend fun emailExists(email: String): Boolean {
        ensureSeeded()
        return db.userDao().findByEmail(email.trim()) != null
    }

    suspend fun getUser(id: Long): UserEntity? { ensureSeeded(); return db.userDao().findById(id) }
    suspend fun updateUser(user: UserEntity) { db.userDao().update(user) }

    suspend fun getHospitals(): List<HospitalEntity> { ensureSeeded(); return db.directoryDao().getHospitals() }
    suspend fun getPharmacies(): List<PharmacyEntity> { ensureSeeded(); return db.directoryDao().getPharmacies() }
    suspend fun getSpecialties(): List<SpecialtyEntity> { ensureSeeded(); return db.directoryDao().getSpecialties() }
    suspend fun getDoctors(hospitalId: Long, specialtyId: Long): List<DoctorEntity> = db.directoryDao().getDoctors(hospitalId, specialtyId)

    suspend fun getDoctorsForAppointment(doctorId: Long): DoctorEntity? = db.directoryDao().getDoctor(doctorId)

    suspend fun getAppointments(userId: Long): List<AppointmentListItem> { ensureSeeded(); return db.appointmentDao().getForUser(userId) }
    suspend fun getAppointment(id: Long): AppointmentEntity? = db.appointmentDao().getById(id)
    suspend fun saveAppointment(item: AppointmentEntity): Long = if (item.id == 0L) db.appointmentDao().insert(item) else { db.appointmentDao().update(item); item.id }
    suspend fun cancelAppointment(id: Long) = db.appointmentDao().cancel(id)

    suspend fun getHistory(userId: Long): List<HistoryListItem> { ensureSeeded(); return db.historyDao().getForUser(userId) }
    suspend fun getHistoryDetail(id: Long): HistoryListItem? = db.historyDao().getDetail(id)
    suspend fun getExams(historyId: Long): List<ExamEntity> = db.historyDao().getExams(historyId)

    suspend fun getMedicationCards(hospitalId: Long, search: String): List<MedicationCardModel> {
        ensureSeeded()
        return db.medicationDao().getAvailability(hospitalId, search).map { medication ->
            val pharmacies = if (medication.disponible) emptyList() else db.medicationDao().getPharmacyAvailability(medication.medicamentoId)
            MedicationCardModel(medication, pharmacies)
        }
    }

    suspend fun getLocations(): List<LocationItem> {
        ensureSeeded()
        val hospitals = db.directoryDao().getHospitals().map {
            LocationItem(it.id, "HOSPITAL", it.nombre, it.direccion, it.telefono, it.latitud, it.longitud)
        }
        val pharmacies = db.directoryDao().getPharmacies().map {
            LocationItem(it.id, "FARMACIA", it.nombre, it.direccion, it.telefono, it.latitud, it.longitud)
        }
        return hospitals + pharmacies
    }
}
