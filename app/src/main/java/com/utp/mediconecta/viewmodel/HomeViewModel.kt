package com.utp.mediconecta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.data.AppointmentListItem
import com.utp.mediconecta.data.UserEntity
import com.utp.mediconecta.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

 data class HomeUiState(
    val loading: Boolean = true,
    val user: UserEntity? = null,
    val nextAppointment: AppointmentListItem? = null,
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as MediConectaApp
    private val repository = app.repository
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun load() {
        val userId = app.sessionManager.userId
        viewModelScope.launch {
            runCatching {
                val user = repository.getUser(userId)
                val next = repository.getAppointments(userId)
                    .filter { it.estado == "PROGRAMADA" && DateUtils.isUpcoming(it.fecha) }
                    .minByOrNull { "${it.fecha} ${it.hora}" }
                user to next
            }.onSuccess { (user, next) -> _state.value = HomeUiState(false, user, next) }
             .onFailure { _state.value = HomeUiState(false, error = it.message) }
        }
    }
}
