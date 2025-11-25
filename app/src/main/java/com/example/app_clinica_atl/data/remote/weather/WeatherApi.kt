package com.example.app_clinica_atl.data.remote.weather

import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val current_weather: CurrentWeatherResponse?,
    val timezone: String?
)

data class CurrentWeatherResponse(
    val temperature: Double?,
    val windspeed: Double?,
    val weathercode: Int?,
    val time: String?
)

interface WeatherApi {
    @GET("forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
