package com.example.app_clinica_atl.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminViewDoctorsUiState(
    val isLoading: Boolean = true,
    val doctorsList: List<DoctorDto> = emptyList(),
    val filteredList: List<DoctorDto> = emptyList(),
    val searchQuery: String = "",
    val errorMsg: String? = null
)

class AdminViewDoctorsViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminViewDoctorsUiState())
    val uiState: StateFlow<AdminViewDoctorsUiState> = _uiState.asStateFlow()

    init {
        loadDoctors()
    }

    private fun loadDoctors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getAllDoctors()
            if (result.isSuccess) {
                val doctors = result.getOrNull().orEmpty()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        doctorsList = doctors,
                        filteredList = doctors
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error cargando lista"
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
                    val fullName = listOfNotNull(doc.nombre, doc.apellido).joinToString(" ").trim()
                    val email = doc.usuario?.correo ?: doc.correo.orEmpty()
                    val specialty = doc.especialidad ?: ""
                    fullName.contains(query, ignoreCase = true) ||
                            email.contains(query, ignoreCase = true) ||
                            specialty.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(searchQuery = query, filteredList = filtered)
        }
    }
}

class AdminViewDoctorsViewModelFactory(private val repo: AdminRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewDoctorsViewModel(repo) as T
    }
}
