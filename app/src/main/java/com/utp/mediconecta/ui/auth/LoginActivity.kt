package com.utp.mediconecta.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.databinding.ActivityLoginBinding
import com.utp.mediconecta.ui.main.MainActivity
import com.utp.mediconecta.viewmodel.AuthUiState
import com.utp.mediconecta.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        val app = application as MediConectaApp
        if (app.sessionManager.isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            clearErrors()
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            if (email.isBlank()) binding.tilEmail.error = "Ingresa tu correo"
            if (password.isBlank()) binding.tilPassword.error = "Ingresa tu contraseña"
            if (email.isNotBlank() && password.isNotBlank()) viewModel.login(email, password)
        }
        binding.tvRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        binding.tvForgot.setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::render)
            }
        }
    }

    private fun render(state: AuthUiState) {
        val loading = state is AuthUiState.Loading
        binding.progressLogin.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !loading
        when (state) {
            is AuthUiState.Success -> {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            is AuthUiState.Error -> {
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
            else -> Unit
        }
    }

    private fun clearErrors() {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
    }
}
