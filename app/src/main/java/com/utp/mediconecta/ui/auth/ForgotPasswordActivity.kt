package com.utp.mediconecta.ui.auth

import android.os.Bundle
import android.view.WindowManager
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.databinding.ActivityForgotPasswordBinding
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSend.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Ingresa un correo válido"
                return@setOnClickListener
            }
            binding.tilEmail.error = null
            lifecycleScope.launch {
                val exists = (application as MediConectaApp).repository.emailExists(email)
                val message = if (exists) "Enlace de recuperación enviado (simulación)" else "No existe una cuenta con ese correo"
                Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_LONG).show()
                if (exists) finish()
            }
        }
    }
}
