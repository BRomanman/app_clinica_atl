package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

data class AdminViewDoctorsUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val filteredDoctors: List<UsuarioEntity> = emptyList()
)

class AdminViewDoctorsViewModel(
    userRepository: UsuariosRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _allDoctorsFlow = userRepository.getAllDoctors()

    val uiState: StateFlow<AdminViewDoctorsUiState> =
        combine(_allDoctorsFlow, _searchQuery) { doctors, query ->
            val filtered = if (query.isBlank()) {
                doctors
            } else {
                doctors.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            (it.specialty?.contains(query, ignoreCase = true) == true) ||
                            it.email.contains(query, ignoreCase = true)
                }
            }
            AdminViewDoctorsUiState(
                searchQuery = query,
                isLoading = false,
                filteredDoctors = filtered
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AdminViewDoctorsUiState(isLoading = true)
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
