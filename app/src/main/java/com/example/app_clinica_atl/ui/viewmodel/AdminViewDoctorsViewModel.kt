package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminViewDoctorsUiState(
    val isLoading: Boolean = true,
    val doctorsList: List<UsuarioDto> = emptyList(),
    val filteredList: List<UsuarioDto> = emptyList(),
    val searchQuery: String = "",
    val errorMsg: String? = null
)

class AdminViewDoctorsViewModel(
    private val repository: UsuariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminViewDoctorsUiState())
    val uiState: StateFlow<AdminViewDoctorsUiState> = _uiState.asStateFlow()

    init {
        loadDoctors()
    }

    private fun loadDoctors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Usamos el flujo getAllDoctors del repositorio con manejo de errores
            repository.getAllDoctors()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMsg = "Error cargando lista: ${e.message}")
                    }
                }
                .collect { doctors ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            doctorsList = doctors,
                            filteredList = doctors // Al principio mostramos todos
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            val filtered = if (query.isBlank()) {
                currentState.doctorsList
            } else {
                currentState.doctorsList.filter { doc ->
                    doc.name.contains(query, ignoreCase = true) ||
                            doc.email.contains(query, ignoreCase = true) ||
                            doc.specialty?.contains(query, ignoreCase = true) == true
                }
            }
            currentState.copy(searchQuery = query, filteredList = filtered)
        }
    }
}

class AdminViewDoctorsViewModelFactory(private val repo: UsuariosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewDoctorsViewModel(repo) as T
    }
}