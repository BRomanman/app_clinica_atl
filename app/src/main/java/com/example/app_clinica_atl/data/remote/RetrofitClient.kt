package com.example.app_clinica_atl.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ÚNICA baseUrl compartida por todos los endpoints REST.
    // Si mueves el backend, cambia solo esta constante (o léela de BuildConfig).
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuariosApi: UsuariosApi by lazy { retrofit.create(UsuariosApi::class.java) }

    val citasApi: CitasApi by lazy { retrofit.create(CitasApi::class.java) }

    val historialesApi: HistorialesApi by lazy { retrofit.create(HistorialesApi::class.java) }

    val segurosApi: SegurosApi by lazy { retrofit.create(SegurosApi::class.java) }
}
