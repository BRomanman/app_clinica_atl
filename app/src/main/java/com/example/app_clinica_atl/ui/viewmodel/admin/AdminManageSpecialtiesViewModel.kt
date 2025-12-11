package com.example.app_clinica_atl.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.domain.validation.validateRequired
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Item que se muestra en la lista de la pantalla de administración.
 *
 * isFromInitialLoad:
 *  - true  -> venía desde la BD al cargar la pantalla (especialidad "original").
 *  - false -> fue creada en esta pantalla (¡NOTA! en esta versión deshabilitamos creación).
 */
data class AdminSpecialtyItem(
    val id: Long?,
    val name: String,
    val isFromInitialLoad: Boolean
)

/**
 * Estado de UI para AdminManageSpecialtiesScreen.
 * (Sin diálogo de "Agregar", solo GET + PUT)
 */
data class AdminSpecialtiesUiState(
    val isLoading: Boolean = false,
    val specialties: List<AdminSpecialtyItem> = emptyList(),
    val errorMsg: String? = null,

    // --- Diálogo "Modificar especialidad" ---
    val isEditDialogOpen: Boolean = false,
    val editingId: Long? = null,
    val editName: String = "",
    val editNameError: String? = null
)

class AdminManageSpecialtiesViewModel(
    private val usuariosRepository: UsuariosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminSpecialtiesUiState())
    val uiState: StateFlow<AdminSpecialtiesUiState> = _uiState.asStateFlow()

    init {
        loadSpecialties()
    }

    /**
     * Reutiliza EXACTAMENTE el mismo flujo que BookAppointment:
     * UsuariosRepository.getAllSpecialties()
     */
    private fun loadSpecialties() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            val result = usuariosRepository.getAllSpecialties()
            _uiState.update { current ->
                if (result.isSuccess) {
                    val list = result.getOrNull().orEmpty()
                        .mapNotNull { dto ->
                            val cleanName = dto.nombre?.trim().orEmpty()
                            if (cleanName.isBlank()) return@mapNotNull null

                            AdminSpecialtyItem(
                                id = dto.id,
                                name = cleanName,
                                isFromInitialLoad = true
                            )
                        }
                        .distinctBy { it.id to it.name }

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

    // --------------------
    //  Diálogo: EDITAR
    // --------------------

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

        val error = validateRequired(current.editName, "Nombre")
        if (error != null) {
            _uiState.update { it.copy(editNameError = error) }
            return
        }

        if (id == null) {
            _uiState.update { it.copy(editNameError = "Error interno: ID nulo") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            val result = usuariosRepository.updateSpecialty(
                id = id,
                name = current.editName.trim()
            )

            _uiState.update { state ->
                if (result.isSuccess) {
                    val updatedList = state.specialties.map { item ->
                        if (item.id == id) item.copy(name = current.editName.trim()) else item
                    }

                    state.copy(
                        isLoading = false,
                        isEditDialogOpen = false,
                        editingId = null,
                        editNameError = null,
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
}
