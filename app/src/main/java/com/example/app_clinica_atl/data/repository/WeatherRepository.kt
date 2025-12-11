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
    companion object {
        // Centro Santiago (plaza de armas) con más precisión
        private const val DEFAULT_LAT = -33.4372
        private const val DEFAULT_LON = -70.6506
    }

    suspend fun getCurrentWeather(
        lat: Double? = null,
        lon: Double? = null
    ): Result<WeatherInfo> =
        withContext(Dispatchers.IO) {
            runCatching {
                val latitude = lat ?: DEFAULT_LAT
                val longitude = lon ?: DEFAULT_LON
                val response = api.getCurrentWeather(latitude, longitude)
                val current = response.current_weather ?: error("Sin datos de clima")
                toWeatherInfo(current, latitude, longitude, response.timezone)
            }
        }


    private fun toWeatherInfo(
        current: CurrentWeatherResponse,
        lat: Double,
        lon: Double,
        timezone: String?
    ): WeatherInfo {
        val temp = current.temperature?.roundToInt() ?: 0
        val wind = current.windspeed?.roundToInt() ?: 0
        val desc = current.weathercode?.let { weatherCodeToEs(it) } ?: "Clima no disponible"
        val latLabel = String.format("%.4f", lat)
        val lonLabel = String.format("%.4f", lon)
        val tzLabel = timezone?.takeIf { it.isNotBlank() } ?: "coordenadas exactas"

        return WeatherInfo(
            temperatureC = temp,
            windKmh = wind,
            description = desc,
            locationLabel = "$latLabel, $lonLabel ($tzLabel)"
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
