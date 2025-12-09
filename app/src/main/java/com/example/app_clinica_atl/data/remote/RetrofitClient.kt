package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.citas.CitasApiService
import com.example.app_clinica_atl.data.remote.weather.WeatherApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL_USUARIOS = "http://10.0.2.2:8082/api/v1/"
    private const val BASE_URL_CITAS = "http://10.0.2.2:8080/"
    private const val BASE_URL_WEATHER = "https://api.open-meteo.com/v1/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private var initialized = false
    private lateinit var usuariosApiInstance: UsuariosApi
    private lateinit var segurosApiInstance: SegurosApi
    private lateinit var citasApiInstance: CitasApi
    private lateinit var citasApiServiceInstance: CitasApiService
    private lateinit var historialesApiInstance: HistorialesApi
    private lateinit var weatherApiInstance: WeatherApi

    fun initialize(userPreferences: UserPreferences) {
        if (initialized) return

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(userPreferences))
            .addInterceptor(logging)
            .build()

        val usuariosRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_USUARIOS)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val citasRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_CITAS)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        usuariosApiInstance = usuariosRetrofit.create(UsuariosApi::class.java)
        segurosApiInstance = usuariosRetrofit.create(SegurosApi::class.java)
        historialesApiInstance = usuariosRetrofit.create(HistorialesApi::class.java)
        citasApiInstance = citasRetrofit.create(CitasApi::class.java)
        citasApiServiceInstance = citasRetrofit.create(CitasApiService::class.java)

        val weatherRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_WEATHER)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApiInstance = weatherRetrofit.create(WeatherApi::class.java)
        initialized = true
    }

    val usuariosApi: UsuariosApi
        get() {
            check(initialized) { "RetrofitClient must be initialized before use." }
            return usuariosApiInstance
        }

    val segurosApi: SegurosApi
        get() {
            check(initialized) { "RetrofitClient must be initialized before use." }
            return segurosApiInstance
        }

    val citasApi: CitasApi
        get() {
            check(initialized) { "RetrofitClient must be initialized before use." }
            return citasApiInstance
        }

    fun createCitasApiService(): CitasApiService {
        check(initialized) { "RetrofitClient must be initialized before use." }
        return citasApiServiceInstance
    }

    val historialesApi: HistorialesApi
        get() {
            check(initialized) { "RetrofitClient must be initialized before use." }
            return historialesApiInstance
        }

    val weatherApi: WeatherApi
        get() {
            check(initialized) { "RetrofitClient must be initialized before use." }
            return weatherApiInstance
        }
}
