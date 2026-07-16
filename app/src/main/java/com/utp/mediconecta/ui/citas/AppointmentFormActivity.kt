package com.utp.mediconecta.ui.citas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.data.AppointmentEntity
import com.utp.mediconecta.data.DoctorEntity
import com.utp.mediconecta.data.HospitalEntity
import com.utp.mediconecta.data.SpecialtyEntity
import com.utp.mediconecta.databinding.ActivityAppointmentFormBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class AppointmentFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppointmentFormBinding
    private val repository by lazy { (application as MediConectaApp).repository }
    private val session by lazy { (application as MediConectaApp).sessionManager }
    private var appointmentId = 0L
    private var existing: AppointmentEntity? = null
    private var hospitals: List<HospitalEntity> = emptyList()
    private var specialties: List<SpecialtyEntity> = emptyList()
    private var doctors: List<DoctorEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appointmentId = intent.getLongExtra(EXTRA_APPOINTMENT_ID, 0L)
        binding.tvTitle.text = if (appointmentId == 0L) "Agendar cita" else "Reprogramar cita"
        binding.btnBack.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }
        binding.etDate.setOnClickListener { openDatePicker() }
        binding.etTime.setOnClickListener { openTimePicker() }
        binding.btnSave.setOnClickListener { save() }

        binding.spHospital.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { loadDoctors() }
        }
        binding.spSpecialty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { loadDoctors() }
        }
        loadInitialData()
    }

    private fun loadInitialData() = lifecycleScope.launch {
        showLoading(true)
        runCatching {
            repository.ensureSeeded()
            hospitals = repository.getHospitals()
            specialties = repository.getSpecialties()
            existing = if (appointmentId > 0) repository.getAppointment(appointmentId) else null
        }.onSuccess {
            binding.spHospital.adapter = ArrayAdapter(this@AppointmentFormActivity, android.R.layout.simple_spinner_dropdown_item, hospitals.map { it.nombre })
            binding.spSpecialty.adapter = ArrayAdapter(this@AppointmentFormActivity, android.R.layout.simple_spinner_dropdown_item, specialties.map { it.nombre })
            val item = existing
            if (item != null) {
                binding.etDate.setText(item.fecha)
                binding.etTime.setText(item.hora)
                binding.etReason.setText(item.motivo)
                val doctor = repository.getDoctorsForAppointment(item.doctorId)
                doctor?.let { d ->
                    binding.spHospital.setSelection(hospitals.indexOfFirst { it.id == d.hospitalId }.coerceAtLeast(0))
                    binding.spSpecialty.setSelection(specialties.indexOfFirst { it.id == d.especialidadId }.coerceAtLeast(0))
                }
            } else {
                binding.etDate.setText(LocalDate.now().plusDays(7).toString())
                binding.etTime.setText("09:00")
            }
            loadDoctors(existing?.doctorId)
        }.onFailure {
            Toast.makeText(this@AppointmentFormActivity, it.message ?: "No se pudieron cargar los datos", Toast.LENGTH_LONG).show()
            showLoading(false)
        }
    }

    private fun loadDoctors(preferredDoctorId: Long? = null) {
        if (hospitals.isEmpty() || specialties.isEmpty()) return
        val hospital = hospitals.getOrNull(binding.spHospital.selectedItemPosition) ?: return
        val specialty = specialties.getOrNull(binding.spSpecialty.selectedItemPosition) ?: return
        lifecycleScope.launch {
            doctors = repository.getDoctors(hospital.id, specialty.id)
            binding.spDoctor.adapter = ArrayAdapter(
                this@AppointmentFormActivity,
                android.R.layout.simple_spinner_dropdown_item,
                doctors.map { "Dr. ${it.nombre} ${it.apellido}" }
            )
            preferredDoctorId?.let { id ->
                val index = doctors.indexOfFirst { it.id == id }
                if (index >= 0) binding.spDoctor.setSelection(index)
            }
            showLoading(false)
        }
    }

    private fun save() {
        val doctor = doctors.getOrNull(binding.spDoctor.selectedItemPosition)
        val date = binding.etDate.text?.toString().orEmpty()
        val time = binding.etTime.text?.toString().orEmpty()
        val reason = binding.etReason.text?.toString().orEmpty().trim()
        if (doctor == null) {
            Toast.makeText(this, "No hay médicos para la selección indicada", Toast.LENGTH_SHORT).show()
            return
        }
        if (date.isBlank() || time.isBlank() || reason.isBlank()) {
            binding.tilReason.error = if (reason.isBlank()) "Escribe el motivo" else null
            Toast.makeText(this, "Completa fecha, hora y motivo", Toast.LENGTH_SHORT).show()
            return
        }
        val parsedDate = runCatching { LocalDate.parse(date) }.getOrNull()
        if (parsedDate == null || parsedDate.isBefore(LocalDate.now())) {
            Toast.makeText(this, "La fecha no puede estar en el pasado", Toast.LENGTH_SHORT).show()
            return
        }
        showLoading(true)
        lifecycleScope.launch {
            val base = existing
            val item = AppointmentEntity(
                id = base?.id ?: 0,
                usuarioId = session.userId,
                doctorId = doctor.id,
                fecha = date,
                hora = time,
                motivo = reason,
                estado = "PROGRAMADA",
                observaciones = base?.observaciones.orEmpty()
            )
            runCatching { repository.saveAppointment(item) }
                .onSuccess {
                    Toast.makeText(this@AppointmentFormActivity, if (base == null) "Cita confirmada" else "Cita reprogramada", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                .onFailure {
                    showLoading(false)
                    Toast.makeText(this@AppointmentFormActivity, it.message ?: "No se pudo guardar", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun openDatePicker() {
        val current = runCatching { LocalDate.parse(binding.etDate.text.toString()) }.getOrDefault(LocalDate.now())
        DatePickerDialog(this, { _, year, month, day ->
            binding.etDate.setText(LocalDate.of(year, month + 1, day).toString())
        }, current.year, current.monthValue - 1, current.dayOfMonth).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }

    private fun openTimePicker() {
        val current = runCatching { LocalTime.parse(binding.etTime.text.toString()) }.getOrDefault(LocalTime.of(9, 0))
        TimePickerDialog(this, { _, hour, minute ->
            binding.etTime.setText(String.format("%02d:%02d", hour, minute))
        }, current.hour, current.minute, false).show()
    }

    private fun showLoading(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !show
    }

    companion object {
        const val EXTRA_APPOINTMENT_ID = "extra_appointment_id"
    }
}
