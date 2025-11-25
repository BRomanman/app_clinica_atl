package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.weather.CurrentWeatherResponse
import com.example.app_clinica_atl.data.remote.weather.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

data class WeatherInfo(
    val temperatureC: Int,
    val windKmh: Int,
    val description: String,
    val locationLabel: String
)

class WeatherRepository(
    private val api: WeatherApi = RetrofitClient.weatherApi
) {
    // Coordenadas de Huechuraba, Santiago.
    private val latitude = -33.367
    private val longitude = -70.633
    private val locationLabel = "Huechuraba, Santiago"

    suspend fun getCurrentWeather(): Result<WeatherInfo> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.getCurrentWeather(latitude, longitude)
            val current = response.current_weather ?: error("Sin datos de clima")
            toWeatherInfo(current)
        }
    }

    private fun toWeatherInfo(current: CurrentWeatherResponse): WeatherInfo {
        val temp = current.temperature?.roundToInt() ?: 0
        val wind = current.windspeed?.roundToInt() ?: 0
        val description = current.weathercode?.let { weatherCodeToEs(it) } ?: "Clima no disponible"
        return WeatherInfo(
            temperatureC = temp,
            windKmh = wind,
            description = description,
            locationLabel = locationLabel
        )
    }

    private fun weatherCodeToEs(code: Int): String = when (code) {
        0 -> "Despejado"
        1 -> "Mayormente despejado"
        2 -> "Parcialmente nublado"
        3 -> "Nublado"
        45, 48 -> "Niebla"
        51, 53, 55 -> "Llovizna"
        56, 57 -> "Llovizna helada"
        61, 63, 65 -> "Lluvia"
        66, 67 -> "Lluvia helada"
        71, 73, 75 -> "Nieve"
        77 -> "Nieve granular"
        80, 81, 82 -> "Chubascos"
        85, 86 -> "Chubascos de nieve"
        95 -> "Tormenta"
        96, 99 -> "Tormenta con granizo"
        else -> "Clima no disponible"
    }
}
