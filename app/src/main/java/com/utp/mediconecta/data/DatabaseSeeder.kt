package com.utp.mediconecta.data

import androidx.room.withTransaction
import com.utp.mediconecta.util.PasswordHasher

object DatabaseSeeder {
    suspend fun seedIfNeeded(db: AppDatabase) {
        if (db.userDao().count() > 0) return

        db.withTransaction {
            val userDao = db.userDao()
            val directoryDao = db.directoryDao()
            val appointmentDao = db.appointmentDao()
            val historyDao = db.historyDao()
            val medicationDao = db.medicationDao()

            fun user(nombre: String, apellido: String, cedula: String, email: String, password: String, birth: String, blood: String, phone: String, emergency: String, relation: String, emergencyPhone: String): UserEntity {
                val salt = PasswordHasher.createSalt()
                return UserEntity(
                    nombre = nombre,
                    apellido = apellido,
                    cedula = cedula,
                    correo = email,
                    passwordHash = PasswordHasher.hash(password, salt),
                    passwordSalt = salt,
                    fechaNacimiento = birth,
                    tipoSangre = blood,
                    telefono = phone,
                    contactoEmergencia = emergency,
                    relacionEmergencia = relation,
                    telefonoEmergencia = emergencyPhone
                )
            }

            val userIds = userDao.insertAll(
                listOf(
                    user("Andrés", "Soto", "8-1021-1245", "andres.demo@mediconecta.app", "Andres123*", "1998-03-14", "O+", "+507 6000-1001", "María Soto", "Madre", "+507 6000-2001"),
                    user("Roger", "Herman", "8-1016-1580", "roger.demo@mediconecta.app", "Roger123*", "1997-08-22", "A+", "+507 6000-1002", "Elena Herman", "Hermana", "+507 6000-2002"),
                    user("Alan", "Aguilar", "8-1015-2455", "alan.demo@mediconecta.app", "Alan123*", "1999-11-05", "B+", "+507 6000-1003", "Luis Aguilar", "Padre", "+507 6000-2003")
                )
            )

            val hospitalIds = directoryDao.insertHospitals(
                listOf(
                    HospitalEntity(nombre = "Hospital Santo Tomás", direccion = "Avenida Balboa, Ciudad de Panamá", telefono = "+507 560-6429", latitud = 8.970296, longitud = -79.532518),
                    HospitalEntity(nombre = "Hospital del Niño", direccion = "Avenida Balboa, Calidonia", telefono = "+507 512-9801", latitud = 8.968759, longitud = -79.532131),
                    HospitalEntity(nombre = "Hospital Nicolás A. Solano", direccion = "La Chorrera, Panamá Oeste", telefono = "+507 254-2846", latitud = 8.866288, longitud = -79.766580)
                )
            )

            val pharmacyIds = directoryDao.insertPharmacies(
                listOf(
                    PharmacyEntity(nombre = "Farmacias Arrocha - Vía España", direccion = "Vía España, Ciudad de Panamá", telefono = "+507 279-9000", latitud = 8.981864, longitud = -79.526526),
                    PharmacyEntity(nombre = "Farmacia Metro - El Dorado", direccion = "El Dorado, Ciudad de Panamá", telefono = "+507 300-7000", latitud = 9.006560, longitud = -79.533908),
                    PharmacyEntity(nombre = "Farmacias Rey - Transístmica", direccion = "Vía Transístmica, Ciudad de Panamá", telefono = "+507 270-5700", latitud = 9.009655, longitud = -79.535089)
                )
            )

            val specialtyIds = directoryDao.insertSpecialties(
                listOf(
                    SpecialtyEntity(nombre = "Cardiología", descripcion = "Diagnóstico y tratamiento cardiovascular"),
                    SpecialtyEntity(nombre = "Medicina General", descripcion = "Atención integral primaria"),
                    SpecialtyEntity(nombre = "Dermatología", descripcion = "Salud de la piel"),
                    SpecialtyEntity(nombre = "Pediatría", descripcion = "Atención infantil"),
                    SpecialtyEntity(nombre = "Gastroenterología", descripcion = "Sistema digestivo")
                )
            )

            val doctorIds = directoryDao.insertDoctors(
                listOf(
                    DoctorEntity(nombre = "Ricardo", apellido = "Medina", especialidadId = specialtyIds[0], hospitalId = hospitalIds[0], consultorio = "C-204", telefono = "+507 560-6501"),
                    DoctorEntity(nombre = "Luisa", apellido = "Ramos", especialidadId = specialtyIds[1], hospitalId = hospitalIds[1], consultorio = "A-112", telefono = "+507 512-9812"),
                    DoctorEntity(nombre = "Mario", apellido = "Fuentes", especialidadId = specialtyIds[2], hospitalId = hospitalIds[0], consultorio = "D-310", telefono = "+507 560-6510"),
                    DoctorEntity(nombre = "Ana", apellido = "Vega", especialidadId = specialtyIds[3], hospitalId = hospitalIds[1], consultorio = "P-08", telefono = "+507 512-9820"),
                    DoctorEntity(nombre = "Carlos", apellido = "Pinto", especialidadId = specialtyIds[4], hospitalId = hospitalIds[2], consultorio = "G-02", telefono = "+507 254-2855"),
                    DoctorEntity(nombre = "Jorge", apellido = "Castillo", especialidadId = specialtyIds[1], hospitalId = hospitalIds[2], consultorio = "M-05", telefono = "+507 254-2860")
                )
            )

            appointmentDao.insertAll(
                listOf(
                    AppointmentEntity(usuarioId = userIds[0], doctorId = doctorIds[0], fecha = "2026-08-22", hora = "10:30", motivo = "Control de presión arterial"),
                    AppointmentEntity(usuarioId = userIds[0], doctorId = doctorIds[1], fecha = "2026-09-05", hora = "14:00", motivo = "Chequeo general"),
                    AppointmentEntity(usuarioId = userIds[0], doctorId = doctorIds[2], fecha = "2026-04-03", hora = "09:00", motivo = "Seguimiento dermatológico", estado = "COMPLETADA"),
                    AppointmentEntity(usuarioId = userIds[1], doctorId = doctorIds[5], fecha = "2026-08-28", hora = "08:30", motivo = "Consulta general"),
                    AppointmentEntity(usuarioId = userIds[2], doctorId = doctorIds[4], fecha = "2026-09-12", hora = "11:00", motivo = "Dolor abdominal")
                )
            )

            val historyIds = historyDao.insertHistories(
                listOf(
                    MedicalHistoryEntity(usuarioId = userIds[0], doctorId = doctorIds[0], fecha = "2026-04-03", diagnostico = "Hipertensión arterial", tratamiento = "Losartán 50 mg cada 24 horas y reducción de sodio", observaciones = "Control en tres meses. Actividad física moderada."),
                    MedicalHistoryEntity(usuarioId = userIds[0], doctorId = doctorIds[2], fecha = "2026-02-15", diagnostico = "Dermatitis alérgica", tratamiento = "Crema de hidrocortisona por siete días", observaciones = "Evitar productos perfumados."),
                    MedicalHistoryEntity(usuarioId = userIds[0], doctorId = doctorIds[1], fecha = "2025-11-10", diagnostico = "Chequeo general", tratamiento = "Mantener hábitos saludables", observaciones = "Sin hallazgos relevantes."),
                    MedicalHistoryEntity(usuarioId = userIds[1], doctorId = doctorIds[5], fecha = "2026-03-20", diagnostico = "Infección respiratoria leve", tratamiento = "Hidratación y acetaminofén", observaciones = "Regresar si aparecen signos de alarma."),
                    MedicalHistoryEntity(usuarioId = userIds[2], doctorId = doctorIds[4], fecha = "2026-01-18", diagnostico = "Gastritis crónica", tratamiento = "Omeprazol 20 mg", observaciones = "Evitar alimentos irritantes.")
                )
            )

            historyDao.insertExams(
                listOf(
                    ExamEntity(historialId = historyIds[0], nombre = "Hemoglobina", fecha = "2026-04-02", resultado = "14.2 g/dL - Normal"),
                    ExamEntity(historialId = historyIds[0], nombre = "Glucosa", fecha = "2026-04-02", resultado = "98 mg/dL - Normal"),
                    ExamEntity(historialId = historyIds[0], nombre = "Colesterol total", fecha = "2026-04-02", resultado = "215 mg/dL - Elevado"),
                    ExamEntity(historialId = historyIds[1], nombre = "Prueba de alergia", fecha = "2026-02-14", resultado = "Sensibilidad a fragancias"),
                    ExamEntity(historialId = historyIds[4], nombre = "Prueba de H. pylori", fecha = "2026-01-17", resultado = "Negativo")
                )
            )

            val medicationIds = medicationDao.insertMedications(
                listOf(
                    MedicationEntity(nombre = "Losartán 50 mg", descripcion = "Antihipertensivo", presentacion = "Tabletas", requiereReceta = true),
                    MedicationEntity(nombre = "Atorvastatina 20 mg", descripcion = "Hipolipemiante", presentacion = "Tabletas", requiereReceta = true),
                    MedicationEntity(nombre = "Metformina 850 mg", descripcion = "Antidiabético", presentacion = "Tabletas", requiereReceta = true),
                    MedicationEntity(nombre = "Omeprazol 20 mg", descripcion = "Antiulceroso", presentacion = "Cápsulas", requiereReceta = false),
                    MedicationEntity(nombre = "Amoxicilina 500 mg", descripcion = "Antibiótico", presentacion = "Cápsulas", requiereReceta = true),
                    MedicationEntity(nombre = "Acetaminofén 500 mg", descripcion = "Analgésico y antipirético", presentacion = "Tabletas", requiereReceta = false),
                    MedicationEntity(nombre = "Salbutamol", descripcion = "Broncodilatador", presentacion = "Inhalador", requiereReceta = true),
                    MedicationEntity(nombre = "Loratadina 10 mg", descripcion = "Antialérgico", presentacion = "Tabletas", requiereReceta = false)
                )
            )

            val now = "2026-07-10"
            val hospitalInventory = mutableListOf<HospitalInventoryEntity>()
            hospitalIds.forEachIndexed { hospitalIndex, hospitalId ->
                medicationIds.forEachIndexed { medIndex, medId ->
                    val unavailable = (hospitalIndex == 0 && medIndex in listOf(2, 4)) || (hospitalIndex == 1 && medIndex in listOf(0, 6)) || (hospitalIndex == 2 && medIndex in listOf(1, 7))
                    val qty = if (unavailable) 0 else 12 + ((hospitalIndex + 1) * (medIndex + 3)) % 40
                    hospitalInventory += HospitalInventoryEntity(hospitalId = hospitalId, medicamentoId = medId, cantidad = qty, disponible = !unavailable, fechaActualizacion = now)
                }
            }
            medicationDao.insertHospitalInventory(hospitalInventory)

            val pharmacyInventory = mutableListOf<PharmacyInventoryEntity>()
            pharmacyIds.forEachIndexed { pharmacyIndex, pharmacyId ->
                medicationIds.forEachIndexed { medIndex, medId ->
                    val qty = 5 + ((pharmacyIndex + 2) * (medIndex + 4)) % 25
                    pharmacyInventory += PharmacyInventoryEntity(
                        farmaciaId = pharmacyId,
                        medicamentoId = medId,
                        cantidad = qty,
                        disponible = true,
                        precio = 2.50 + pharmacyIndex * 0.75 + medIndex * 1.10,
                        fechaActualizacion = now
                    )
                }
            }
            medicationDao.insertPharmacyInventory(pharmacyInventory)
        }
    }
}
