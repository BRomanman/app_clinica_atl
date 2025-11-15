package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = ""
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserName()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            try {
                val userId = userPreferences.userIdFlow.first()
                if (userId != null) {
                    val result = userRepository.getUserById(userId)
                    if (result.isSuccess) {
                        val name = result.getOrNull()?.name ?: "Usuario"
                        _uiState.update { it.copy(userName = name) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(userName = "Usuario") }
            }
        }
    }
}