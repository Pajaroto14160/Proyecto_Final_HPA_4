package com.utp.mediconecta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.mediconecta.MediConectaApp
import com.utp.mediconecta.data.HistoryListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(val loading: Boolean = true, val all: List<HistoryListItem> = emptyList(), val visible: List<HistoryListItem> = emptyList(), val filter: String = "Todos", val error: String? = null)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as MediConectaApp
    private val repository = app.repository
    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        runCatching { repository.getHistory(app.sessionManager.userId) }
            .onSuccess { _state.value = HistoryUiState(false, it, it) }
            .onFailure { _state.value = HistoryUiState(false, error = it.message) }
    }

    fun setFilter(filter: String) {
        val all = _state.value.all
        val visible = when (filter) {
            "2026" -> all.filter { it.fecha.startsWith("2026") }
            "2025" -> all.filter { it.fecha.startsWith("2025") }
            else -> all
        }
        _state.value = _state.value.copy(visible = visible, filter = filter)
    }
}
