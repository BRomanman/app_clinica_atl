package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.remote.weather.WeatherApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Centraliza la creación de Retrofit: usa un cliente con logging + interceptor JWT opcional
// y expone instancias por microservicio (usuarios, seguros, citas, historial, clima).
object RetrofitClient {

    //Basti: vzv5wjj9
    //Diego: 3zvxm102

    private const val CODIGO = "vzv5wjj9"
    const val BASE_URL_USUARIO = "https://$CODIGO-8082.brs.devtunnels.ms/api/v1/"
    private const val BASE_URL_SEGURO = "https://$CODIGO-8084.brs.devtunnels.ms/api/v1/"
    private const val BASE_URL_CITAS = "https://$CODIGO-8080.brs.devtunnels.ms/api/v1/"
    private const val BASE_URL_HISTORIAL = "https://$CODIGO-8083.brs.devtunnels.ms/api/v1/"
    private const val BASE_URL_WEATHER = "https://api.open-meteo.com/v1/"

    private val logging = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)


    @Volatile
    private var authInterceptor: Interceptor? = null

    /*
     * Configura el interceptor JWT. Debe llamarse antes de consumir los APIs.
     */
    fun configure(userPreferences: UserPreferences) {
        authInterceptor = AuthInterceptor(userPreferences)
    }

    private fun createOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .apply { authInterceptor?.let { addInterceptor(it) } }
            .build()

    private fun createRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val retrofitUsu: Retrofit by lazy { createRetrofit(BASE_URL_USUARIO) }
    private val retrofitSeg: Retrofit by lazy { createRetrofit(BASE_URL_SEGURO) }
    private val retrofitCit: Retrofit by lazy { createRetrofit(BASE_URL_CITAS) }
    private val retrofitHis: Retrofit by lazy { createRetrofit(BASE_URL_HISTORIAL) }
    private val retrofitWea: Retrofit by lazy { createRetrofit(BASE_URL_WEATHER) }

    val usuariosApi: UsuariosApi by lazy { retrofitUsu.create(UsuariosApi::class.java) }
    val segurosApi: SegurosApi by lazy { retrofitSeg.create(SegurosApi::class.java) }
    val citasApi: CitasApi by lazy { retrofitCit.create(CitasApi::class.java) }
    val historialesApi: HistorialesApi by lazy { retrofitHis.create(HistorialesApi::class.java) }
    val weatherApi: WeatherApi by lazy { retrofitWea.create(WeatherApi::class.java) }
}
