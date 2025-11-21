package com.example.app_clinica_atl.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ÚNICA baseUrl compartida por todos los endpoints REST.
    // Si mueves el backend, cambia solo esta constante (o léela de BuildConfig).
    private const val BASE_URL_USUARIO = "https://3zvxm102-8082.brs.devtunnels.ms/api/v1/"
    private const val BASE_URL_SEGURO = "https://3zvxm102-8084.brs.devtunnels.ms/api/v1/"
    private const val BASE_URL_CITAS = "https://3zvxm102-8080.brs.devtunnels.ms/api/v1/"
    private const val BASE_URL_HISTORIAL = "https://3zvxm102-8083.brs.devtunnels.ms/api/v1/"

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

    // Utilizar la función para crear todas las instancias
    private val retrofit_usu: Retrofit by lazy { createRetrofit(BASE_URL_USUARIO) }
    private val retrofit_seg: Retrofit by lazy { createRetrofit(BASE_URL_SEGURO) }
    private val retrofit_cit: Retrofit by lazy { createRetrofit(BASE_URL_CITAS) }
    private val retrofit_his: Retrofit by lazy { createRetrofit(BASE_URL_HISTORIAL) }

    val usuariosApi: UsuariosApi by lazy { retrofit_usu.create(UsuariosApi::class.java) }
    val segurosApi: SegurosApi by lazy { retrofit_seg.create(SegurosApi::class.java) }
    val citasApi: CitasApi by lazy { retrofit_cit.create(CitasApi::class.java) }
    val historialesApi: HistorialesApi by lazy { retrofit_his.create(HistorialesApi::class.java) }
}
