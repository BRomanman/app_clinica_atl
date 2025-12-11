package com.example.app_clinica_atl.data.repository

import android.content.Context
import android.location.Geocoder
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.weather.CurrentWeatherResponse
import com.example.app_clinica_atl.data.remote.weather.WeatherApi
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

data class WeatherInfo(
    val temperatureC: Int,
    val windKmh: Int,
    val description: String,
    val locationLabel: String,
    val placeName: String? = null,
    val emoji: String = ""
)

class WeatherRepository(
    private val api: WeatherApi = RetrofitClient.weatherApi,
    context: Context? = null
) {
    companion object {
        // Centro Santiago (plaza de armas) con más precisión
        private const val DEFAULT_LAT = -33.4372
        private const val DEFAULT_LON = -70.6506
    }

    private val geocoder: Geocoder? = context?.let { Geocoder(it, Locale.getDefault()) }

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
                val placeName = resolvePlaceName(latitude, longitude)
                toWeatherInfo(current, latitude, longitude, response.timezone, placeName)
            }
        }


    private fun toWeatherInfo(
        current: CurrentWeatherResponse,
        lat: Double,
        lon: Double,
        timezone: String?,
        placeName: String?
    ): WeatherInfo {
        val temp = current.temperature?.roundToInt() ?: 0
        val wind = current.windspeed?.roundToInt() ?: 0
        val desc = current.weathercode?.let { weatherCodeToEs(it) } ?: "Clima no disponible"
        val emoji =
            current.weathercode?.let { weatherCodeToEmoji(it) } ?: "\u2601" // nube por defecto
        val latLabel = String.format("%.4f", lat)
        val lonLabel = String.format("%.4f", lon)
        val tzLabel = timezone?.takeIf { it.isNotBlank() } ?: "coordenadas exactas"

        return WeatherInfo(
            temperatureC = temp,
            windKmh = wind,
            description = desc,
            locationLabel = "$latLabel, $lonLabel ($tzLabel)",
            placeName = placeName,
            emoji = emoji
        )
    }

    private fun resolvePlaceName(lat: Double, lon: Double): String? {
        return runCatching {
            geocoder
                ?.getFromLocation(lat, lon, 1)
                ?.firstOrNull()
                ?.let { it.locality ?: it.subAdminArea ?: it.adminArea }
        }.getOrNull()
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

    private fun weatherCodeToEmoji(code: Int): String = when (code) {
        0 -> "\u2600"                       // sol
        1 -> "\uD83C\uDF24"                 // 🌤 sol con nubes
        2 -> "\u26C5"                       // ⛅ parcialmente nublado
        3 -> "\u2601"                       // ☁ nublado
        45, 48 -> "\uD83C\uDF2B"            // 🌫 niebla
        51, 53, 55 -> "\uD83C\uDF26"        // 🌦 llovizna
        56, 57 -> "\uD83C\uDF27"            // 🌧 llovizna helada
        61, 63, 65 -> "\u2614"              // ☔ lluvia
        66, 67 -> "\u2744"                  // ❄ lluvia helada / nieve
        71, 73, 75 -> "\u2744"              // ❄ nieve
        77 -> "\uD83C\uDF28"                // 🌨 nieve granular
        80, 81, 82 -> "\uD83C\uDF27"        // 🌧 chubascos
        85, 86 -> "\u2744"                  // ❄ chubascos de nieve
        95 -> "\u26C8"                      // ⛈ tormenta
        96, 99 -> "\u26C8"                  // ⛈ tormenta con granizo
        else -> "\u2601"                    // ☁ default
    }
}
