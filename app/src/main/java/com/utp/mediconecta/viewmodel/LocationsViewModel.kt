package com.utp.mediconecta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.data.LocationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocationsUiState(val loading: Boolean = true, val items: List<LocationItem> = emptyList(), val error: String? = null)

class LocationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as MediConectaApp).repository
    private val _state = MutableStateFlow(LocationsUiState())
    val state: StateFlow<LocationsUiState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        runCatching { repository.getLocations() }
            .onSuccess { _state.value = LocationsUiState(false, it) }
            .onFailure { _state.value = LocationsUiState(false, error = it.message) }
    }
}
