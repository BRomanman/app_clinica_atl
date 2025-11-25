package com.example.app_clinica_atl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.WeatherInfo
import com.example.app_clinica_atl.data.repository.WeatherRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val debugInfo: String? = null,
    val profileImageUrl: String? = null,
    val popularDoctors: List<UsuarioDto> = emptyList(),
    val weather: WeatherInfo? = null,
    val isWeatherLoading: Boolean = true,
    val weatherError: String? = null
)

class HomeViewModel(
    private val userRepository: UsuariosRepository,
    private val userPreferences: UserPreferences,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _debugUserInfo = MutableStateFlow<String?>(null)
    val debugUserInfo: StateFlow<String?> = _debugUserInfo.asStateFlow()

    private val _weatherInfo = MutableStateFlow<WeatherInfo?>(null)
    private val _weatherError = MutableStateFlow<String?>(null)
    private val _isWeatherLoading = MutableStateFlow(true)

    private val doctorsFlow = userRepository.getAllDoctors().catch { emit(emptyList<UsuarioDto>()) }

    private val weatherStateFlow = combine(
        _weatherInfo,
        _weatherError,
        _isWeatherLoading
    ) { weather, weatherError, isWeatherLoading ->
        Triple(weather, weatherError, isWeatherLoading)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentUserFlow = userPreferences.userIdFlow.flatMapLatest { userId ->
        if (userId == null) flowOf<UsuarioDto?>(null) else userRepository.getUserByIdAsFlow(userId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = combine(
        currentUserFlow,
        doctorsFlow,
        _debugUserInfo,
        weatherStateFlow
    ) { user: UsuarioDto?, doctors: List<UsuarioDto>, debug: String?, weatherTriple: Triple<WeatherInfo?, String?, Boolean> ->
        val (weather, weatherError, isWeatherLoading) = weatherTriple
        val popular = doctors
            .filter { it.role.equals("doctor", true) }
            .take(6)
        HomeUiState(
            userName = user?.name ?: "Usuario",
            debugInfo = debug,
            profileImageUrl = user?.profileImageUrl,
            popularDoctors = popular,
            weather = weather,
            isWeatherLoading = isWeatherLoading,
            weatherError = weatherError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(userName = "Cargando...")
    )

    init {
        fetchWeather()
    }

    fun fetchWeather() {
        viewModelScope.launch {
            _isWeatherLoading.value = true
            val result = weatherRepository.getCurrentWeather()
            result.onSuccess {
                _weatherInfo.value = it
                _weatherError.value = null
            }.onFailure { error ->
                _weatherError.value = error.message ?: "No pudimos obtener el clima."
            }
            _isWeatherLoading.value = false
        }
    }

    fun fetchDebugUser(userId: Long) {
        viewModelScope.launch {
            val result = userRepository.getUserById(userId)
            _debugUserInfo.value = result.fold(
                onSuccess = { user ->
                    """
                        ID: ${user.id}
                        Nombre: ${user.name}
                        Correo: ${user.email}
                        Rol: ${user.role}
                    """.trimIndent()
                },
                onFailure = { error -> "Error: ${error.message ?: "desconocido"}" }
            )
        }
    }
}
