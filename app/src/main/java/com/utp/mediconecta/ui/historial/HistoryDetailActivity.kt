package com.utp.mediconecta.ui.historial

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.databinding.ActivityHistoryDetailBinding
import com.utp.mediconecta.util.DateUtils
import kotlinx.coroutines.launch

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }
        val id = intent.getLongExtra(EXTRA_HISTORY_ID, -1L)
        if (id <= 0) {
            finish()
            return
        }
        val repository = (application as MediConectaApp).repository
        lifecycleScope.launch {
            runCatching {
                val detail = repository.getHistoryDetail(id)
                val exams = repository.getExams(id)
                detail to exams
            }.onSuccess { (detail, exams) ->
                binding.progress.visibility = View.GONE
                if (detail == null) {
                    Toast.makeText(this@HistoryDetailActivity, "Registro no encontrado", Toast.LENGTH_SHORT).show()
                    finish()
                    return@onSuccess
                }
                binding.tvDate.text = "Fecha: ${DateUtils.pretty(detail.fecha)}"
                binding.tvHospital.text = "Hospital: ${detail.hospitalNombre}"
                binding.tvDoctor.text = "Médico: ${detail.doctorNombre}"
                binding.tvSpecialty.text = "Especialidad: ${detail.especialidadNombre}"
                binding.tvDiagnosis.text = detail.diagnostico
                binding.tvTreatment.text = detail.tratamiento
                binding.tvNotes.text = detail.observaciones
                binding.tvExams.text = if (exams.isEmpty()) "No hay exámenes asociados." else exams.joinToString("\n\n") { "• ${it.nombre}: ${it.resultado}" }
            }.onFailure {
                binding.progress.visibility = View.GONE
                Toast.makeText(this@HistoryDetailActivity, it.message ?: "Error al cargar", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val EXTRA_HISTORY_ID = "extra_history_id"
    }
}
