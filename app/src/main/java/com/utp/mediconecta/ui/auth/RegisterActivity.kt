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
import com.utp.mediconecta.databinding.ActivityRegisterBinding
import com.utp.mediconecta.ui.main.MainActivity
import com.utp.mediconecta.viewmodel.AuthUiState
import com.utp.mediconecta.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnCreateAccount.setOnClickListener {
            viewModel.register(
                binding.etFullName.text?.toString().orEmpty(),
                binding.etCedula.text?.toString().orEmpty(),
                binding.etEmail.text?.toString().orEmpty(),
                binding.etPassword.text?.toString().orEmpty(),
                binding.etConfirm.text?.toString().orEmpty()
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    val loading = state is AuthUiState.Loading
                    binding.progressRegister.visibility = if (loading) View.VISIBLE else View.GONE
                    binding.btnCreateAccount.isEnabled = !loading
                    when (state) {
                        is AuthUiState.Success -> {
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            finishAffinity()
                        }
                        is AuthUiState.Error -> {
                            Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_SHORT).show()
                            viewModel.clearMessage()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
