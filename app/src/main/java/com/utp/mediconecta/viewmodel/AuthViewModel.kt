package com.utp.mediconecta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val user: UserEntity) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as MediConectaApp
    private val repository = app.repository

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthUiState.Error("Completa el correo y la contraseña")
            return
        }
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            runCatching { repository.login(email, password) }
                .onSuccess { user ->
                    if (user == null) _state.value = AuthUiState.Error("Correo o contraseña incorrectos")
                    else {
                        app.sessionManager.saveSession(user.id, "${user.nombre} ${user.apellido}", user.correo)
                        _state.value = AuthUiState.Success(user)
                    }
                }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: "No se pudo iniciar sesión") }
        }
    }

    fun register(fullName: String, cedula: String, email: String, password: String, confirm: String) {
        val parts = fullName.trim().split(Regex("\\s+"), limit = 2)
        if (parts.isEmpty() || parts.first().isBlank() || cedula.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = AuthUiState.Error("Completa todos los campos")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.value = AuthUiState.Error("Ingresa un correo válido")
            return
        }
        if (password.length < 8) {
            _state.value = AuthUiState.Error("La contraseña debe tener al menos 8 caracteres")
            return
        }
        if (password != confirm) {
            _state.value = AuthUiState.Error("Las contraseñas no coinciden")
            return
        }
        val nombre = parts.first()
        val apellido = parts.getOrElse(1) { "" }
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            repository.register(nombre, apellido, cedula, email, password)
                .onSuccess { user ->
                    app.sessionManager.saveSession(user.id, "${user.nombre} ${user.apellido}".trim(), user.correo)
                    _state.value = AuthUiState.Success(user)
                }
                .onFailure { _state.value = AuthUiState.Error(it.message ?: "No se pudo crear la cuenta") }
        }
    }

    fun clearMessage() { _state.value = AuthUiState.Idle }
}
