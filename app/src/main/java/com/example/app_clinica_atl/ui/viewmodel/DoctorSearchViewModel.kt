package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.app_clinica_atl.data.model.DoctorInfo
import com.example.app_clinica_atl.data.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DoctorSearchUiState(
    val searchText: String = "",
    val doctors: List<DoctorInfo> = emptyList()
)

class DoctorSearchViewModel(
    private val repository: DoctorRepository
) : ViewModel() {

    private val allDoctors: List<DoctorInfo> = repository.getAllDoctors()

    private val _uiState = MutableStateFlow(DoctorSearchUiState(doctors = allDoctors))
    val uiState: StateFlow<DoctorSearchUiState> = _uiState.asStateFlow()

    fun onSearchTextChange(text: String) {
        _uiState.update { it.copy(searchText = text) }

        val filtered = if (text.isBlank()) {
            allDoctors
        } else {
            val query = text.trim()
            allDoctors.filter { doctor ->
                doctor.id.contains(query, ignoreCase = true) ||
                        doctor.name.contains(query, ignoreCase = true) ||
                        doctor.specialty.contains(query, ignoreCase = true) ||
                        doctor.email.contains(query, ignoreCase = true)
            }
        }

        _uiState.update { it.copy(doctors = filtered) }
    }
}
