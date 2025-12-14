package com.example.app_clinica_atl.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.remote.dto.EspecialidadUpdateRequestDto
import com.example.app_clinica_atl.data.repository.AdminRepository
import com.example.app_clinica_atl.domain.validation.validateRequired
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminSpecialtyItem(
    val id: Long?,
    val name: String
)

data class AdminSpecialtiesUiState(
    val isLoading: Boolean = false,
    val specialties: List<AdminSpecialtyItem> = emptyList(),
    val errorMsg: String? = null,
    val isEditDialogOpen: Boolean = false,
    val editingId: Long? = null,
    val editName: String = "",
    val editNameError: String? = null,
    val successMessage: String? = null
)

class AdminManageSpecialtiesViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminSpecialtiesUiState())
    val uiState: StateFlow<AdminSpecialtiesUiState> = _uiState.asStateFlow()

    init {
        loadSpecialties()
    }

    private fun loadSpecialties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            val result = adminRepository.getAllSpecialties()
            _uiState.update { current ->
                if (result.isSuccess) {
                    val list = result.getOrNull().orEmpty()
                        .mapNotNull { dto ->
                            val cleanName = dto.name?.trim().orEmpty()
                            if (cleanName.isBlank()) return@mapNotNull null
                            AdminSpecialtyItem(
                                id = dto.id,
                                name = cleanName
                            )
                        }
                        .distinctBy { Pair(it.id, it.name) }

                    current.copy(
                        isLoading = false,
                        specialties = list,
                        errorMsg = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al cargar especialidades"
                    )
                }
            }
        }
    }

    fun openEditDialog(item: AdminSpecialtyItem) {
        _uiState.update {
            it.copy(
                isEditDialogOpen = true,
                editingId = item.id,
                editName = item.name,
                editNameError = null
            )
        }
    }

    fun dismissEditDialog() {
        _uiState.update {
            it.copy(
                isEditDialogOpen = false,
                editingId = null,
                editName = "",
                editNameError = null
            )
        }
    }

    fun onEditNameChange(newName: String) {
        _uiState.update { it.copy(editName = newName, editNameError = null) }
    }

    fun confirmEditSpecialty() {
        val current = _uiState.value
        val id = current.editingId
        val trimmedName = current.editName.trim()

        val nameError = validateRequired(trimmedName, "Nombre")
        if (nameError != null) {
            _uiState.update { it.copy(editNameError = nameError) }
            return
        }

        if (id == null) {
            _uiState.update { it.copy(editNameError = "Error interno: ID nulo") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            val editRequest = EspecialidadUpdateRequestDto(
                nombre = trimmedName
            )
            val result = adminRepository.updateSpecialty(
                id = id,
                request = editRequest
            )

            _uiState.update { state ->
                if (result.isSuccess) {
                    val updatedList = state.specialties.map { item ->
                        if (item.id == id) item.copy(name = trimmedName) else item
                    }

                    state.copy(
                        isLoading = false,
                        isEditDialogOpen = false,
                        editingId = null,
                        editName = "",
                        editNameError = null,
                        successMessage = "Especialidad modificada con éxito",
                        specialties = updatedList
                    )
                } else {
                    state.copy(
                        isLoading = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al actualizar especialidad"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMsg = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
