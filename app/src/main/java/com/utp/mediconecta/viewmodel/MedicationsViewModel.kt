package com.utp.mediconecta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.data.HospitalEntity
import com.utp.mediconecta.data.MedicationCardModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MedicationsUiState(
    val loading: Boolean = true,
    val hospitals: List<HospitalEntity> = emptyList(),
    val selectedHospitalId: Long = -1,
    val search: String = "",
    val cards: List<MedicationCardModel> = emptyList(),
    val error: String? = null
)

class MedicationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as MediConectaApp).repository
    private val _state = MutableStateFlow(MedicationsUiState())
    val state: StateFlow<MedicationsUiState> = _state.asStateFlow()
    private var searchJob: Job? = null

    fun load() = viewModelScope.launch {
        runCatching { repository.getHospitals() }.onSuccess { hospitals ->
            val selected = _state.value.selectedHospitalId.takeIf { id -> hospitals.any { it.id == id } } ?: hospitals.firstOrNull()?.id ?: -1
            _state.value = _state.value.copy(hospitals = hospitals, selectedHospitalId = selected)
            refresh()
        }.onFailure { _state.value = _state.value.copy(loading = false, error = it.message) }
    }

    fun selectHospital(id: Long) {
        _state.value = _state.value.copy(selectedHospitalId = id)
        refresh()
    }

    fun search(text: String) {
        _state.value = _state.value.copy(search = text)
        searchJob?.cancel()
        searchJob = viewModelScope.launch { delay(250); refresh() }
    }

    private fun refresh() = viewModelScope.launch {
        val hospitalId = _state.value.selectedHospitalId
        if (hospitalId <= 0) return@launch
        _state.value = _state.value.copy(loading = true)
        runCatching { repository.getMedicationCards(hospitalId, _state.value.search) }
            .onSuccess { _state.value = _state.value.copy(loading = false, cards = it, error = null) }
            .onFailure { _state.value = _state.value.copy(loading = false, error = it.message) }
    }
}
