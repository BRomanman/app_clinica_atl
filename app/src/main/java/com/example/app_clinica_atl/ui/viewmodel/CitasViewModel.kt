package com.example.app_clinica_atl.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_clinica_atl.data.remote.dto.CitasDto
import com.example.app_clinica_atl.data.repository.CitasRepository
import kotlinx.coroutines.launch



data class CitasUiState(
    val isloading: Boolean = false,
    val citas: List<CitasDto> = emptyList(),
    val error: String? = null
)
class PostsViewModel (
    private val repository: CitasRepository = CitasRepository()
): ViewModel(){
    var uiState by mutableStateOf(CitasUiState())
        private set


    //funcion para la carga de la data
    fun loadPost(){
        //inciar la carga modificando el state
        uiState = uiState.copy(isLoading = true, error = null)


        viewModelScope.launch {
            var result = repository.fetchCitas()
            uiState = result.fold(
                onSuccess = { data -> uiState.copy(isLoading = false, post = data)},
                onFailure = { e -> uiState.copy(isLoading = false, error= e.message ?:"Error Desconocido")}
            )
        }
    }
}

