package com.example.app_clinica_atl.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // TODO: Reemplazar con la URL base de la API real
    private const val BASE_URL = "https://api.clinic-atl.com/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuariosApi: UsuariosApi by lazy {
        instance.create(UsuariosApi::class.java)
    }

    val citasApi: CitasApi by lazy {
        instance.create(CitasApi::class.java)
    }

    val historialesApi: HistorialesApi by lazy {
        instance.create(HistorialesApi::class.java)
    }

    val segurosApi: SegurosApi by lazy {
        instance.create(SegurosApi::class.java)
    }
}
