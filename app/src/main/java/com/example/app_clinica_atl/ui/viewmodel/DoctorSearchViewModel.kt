package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.data.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DoctorSearchUiState(
    val searchText: String = "",
    val doctors: List<DoctorInfo> = emptyList()
)

class DoctorSearchViewModel(
    private val repository: DoctorRepository
) : ViewModel() {

    private var allDoctors: List<DoctorInfo> = emptyList()

    private val _uiState = MutableStateFlow(DoctorSearchUiState())
    val uiState: StateFlow<DoctorSearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeDoctors().collect { doctors ->
                allDoctors = doctors
                _uiState.update { state ->
                    state.copy(doctors = filterDoctors(state.searchText, doctors))
                }
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _uiState.update { it.copy(searchText = text) }
        _uiState.update { it.copy(doctors = filterDoctors(text, allDoctors)) }
    }

    private fun filterDoctors(query: String, source: List<DoctorInfo>): List<DoctorInfo> {
        if (query.isBlank()) return source
        val normalized = query.trim()
        return source.filter { doctor ->
            doctor.id.contains(normalized, ignoreCase = true) ||
                doctor.name.contains(normalized, ignoreCase = true) ||
                doctor.specialty.contains(normalized, ignoreCase = true) ||
                doctor.email.contains(normalized, ignoreCase = true)
        }
    }
}
