package com.utp.mediconecta.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.data.UserEntity
import com.utp.mediconecta.databinding.ActivityProfileBinding
import com.utp.mediconecta.ui.auth.LoginActivity
import com.utp.mediconecta.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var currentUser: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener { save() }
        binding.btnLogout.setOnClickListener {
            (application as MediConectaApp).sessionManager.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.visibility = if (state.loading) View.VISIBLE else View.GONE
                    binding.btnSave.isEnabled = !state.loading
                    state.user?.let { user ->
                        if (currentUser?.id != user.id || currentUser != user) {
                            currentUser = user
                            populate(user)
                        }
                    }
                    state.message?.let {
                        Toast.makeText(this@ProfileActivity, it, Toast.LENGTH_SHORT).show()
                        viewModel.consumeMessage()
                    }
                }
            }
        }
        viewModel.load()
    }

    private fun populate(user: UserEntity) = with(binding) {
        val fullName = "${user.nombre} ${user.apellido}".trim()
        tvProfileName.text = fullName
        tvProfileCedula.text = user.cedula
        tvInitials.text = listOf(user.nombre.firstOrNull(), user.apellido.firstOrNull()).filterNotNull().joinToString("").uppercase().ifBlank { "MC" }
        etName.setText(user.nombre)
        etLastName.setText(user.apellido)
        etCedula.setText(user.cedula)
        etBirthDate.setText(user.fechaNacimiento)
        etBloodType.setText(user.tipoSangre)
        etPhone.setText(user.telefono)
        etEmergencyName.setText(user.contactoEmergencia)
        etEmergencyRelation.setText(user.relacionEmergencia)
        etEmergencyPhone.setText(user.telefonoEmergencia)
    }

    private fun save() {
        val base = currentUser ?: return
        val name = binding.etName.text?.toString().orEmpty().trim()
        val lastName = binding.etLastName.text?.toString().orEmpty().trim()
        val cedula = binding.etCedula.text?.toString().orEmpty().trim()
        if (name.isBlank() || cedula.isBlank()) {
            Toast.makeText(this, "Nombre y cédula son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.save(
            base.copy(
                nombre = name,
                apellido = lastName,
                cedula = cedula,
                fechaNacimiento = binding.etBirthDate.text?.toString().orEmpty().trim(),
                tipoSangre = binding.etBloodType.text?.toString().orEmpty().trim().uppercase(),
                telefono = binding.etPhone.text?.toString().orEmpty().trim(),
                contactoEmergencia = binding.etEmergencyName.text?.toString().orEmpty().trim(),
                relacionEmergencia = binding.etEmergencyRelation.text?.toString().orEmpty().trim(),
                telefonoEmergencia = binding.etEmergencyPhone.text?.toString().orEmpty().trim()
            )
        )
    }
}
