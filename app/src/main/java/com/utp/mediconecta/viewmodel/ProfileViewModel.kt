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

data class ProfileUiState(val loading: Boolean = true, val user: UserEntity? = null, val message: String? = null)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as MediConectaApp
    private val repository = app.repository
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        runCatching { repository.getUser(app.sessionManager.userId) }
            .onSuccess { _state.value = ProfileUiState(false, it) }
            .onFailure { _state.value = ProfileUiState(false, message = it.message) }
    }

    fun save(updated: UserEntity) = viewModelScope.launch {
        runCatching { repository.updateUser(updated) }
            .onSuccess {
                app.sessionManager.saveSession(updated.id, "${updated.nombre} ${updated.apellido}".trim(), updated.correo)
                _state.value = ProfileUiState(false, updated, "Perfil actualizado")
            }
            .onFailure { _state.value = _state.value.copy(message = it.message) }
    }

    fun consumeMessage() { _state.value = _state.value.copy(message = null) }
}
