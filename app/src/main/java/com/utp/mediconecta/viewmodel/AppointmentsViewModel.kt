package com.utp.mediconecta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.data.AppointmentListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppointmentsUiState(val loading: Boolean = true, val items: List<AppointmentListItem> = emptyList(), val message: String? = null)

class AppointmentsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as MediConectaApp
    private val repository = app.repository
    private val _state = MutableStateFlow(AppointmentsUiState())
    val state: StateFlow<AppointmentsUiState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        runCatching { repository.getAppointments(app.sessionManager.userId) }
            .onSuccess { _state.value = AppointmentsUiState(false, it) }
            .onFailure { _state.value = AppointmentsUiState(false, message = it.message) }
    }

    fun cancel(id: Long) = viewModelScope.launch {
        runCatching { repository.cancelAppointment(id) }
            .onSuccess { load(); _state.value = _state.value.copy(message = "Cita cancelada") }
            .onFailure { _state.value = _state.value.copy(message = it.message) }
    }

    fun consumeMessage() { _state.value = _state.value.copy(message = null) }
}
