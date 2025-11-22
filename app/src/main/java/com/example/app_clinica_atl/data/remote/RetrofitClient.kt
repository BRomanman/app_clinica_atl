package com.example.app_clinica_atl.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // --- CONFIGURACIÓN ORIGINAL (NUBE / DEVTUNNELS) ---
    // private const val codigo = "3zvxm102"
    // private const val BASE_URL_USUARIO = "https://$codigo-8082.brs.devtunnels.ms/api/v1/"
    // private const val BASE_URL_SEGURO = "https://$codigo-8084.brs.devtunnels.ms/api/v1/"
    // private const val BASE_URL_CITAS = "https://$codigo-8080.brs.devtunnels.ms/api/v1/"
    // private const val BASE_URL_HISTORIAL = "https://$codigo-8083.brs.devtunnels.ms/api/v1/"

    // Configuración para Emulador Android (apunta al localhost de tu PC)
    private const val BASE_URL_USUARIO = "http://10.0.2.2:8082/api/v1/"
    private const val BASE_URL_SEGURO = "http://10.0.2.2:8084/api/v1/"
    private const val BASE_URL_CITAS = "http://10.0.2.2:8080/api/v1/"
    private const val BASE_URL_HISTORIAL = "http://10.0.2.2:8083/api/v1/"

    private val logging = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private fun createRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val usuariosApi: UsuariosApi by lazy { createRetrofit(BASE_URL_USUARIO).create(UsuariosApi::class.java) }
    val segurosApi: SegurosApi by lazy { createRetrofit(BASE_URL_SEGURO).create(SegurosApi::class.java) }
    val citasApi: CitasApi by lazy { createRetrofit(BASE_URL_CITAS).create(CitasApi::class.java) }
    val historialesApi: HistorialesApi by lazy { createRetrofit(BASE_URL_HISTORIAL).create(HistorialesApi::class.java) }
}