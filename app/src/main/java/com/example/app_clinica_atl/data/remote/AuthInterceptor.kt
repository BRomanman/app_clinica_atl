package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.local.storage.UserPreferences
import okhttp3.Interceptor
import okhttp3.Response

private const val LOGIN_PATHS = "/auth/login"

class AuthInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath


        // EL METODO DE LA API PARA LA AUTH
        if (request.method == "POST" && LOGIN_PATHS.contains(path)) {
            return chain.proceed(request)
        }

        // REVISA SI EL TOKEN ESTÁ GUARDADO EN LA SESIÓN, DE SER ASÍ SE ENVIA
        val token = userPreferences.currentToken()?.takeIf { it.isNotBlank() }
        if (token == null) {
            return chain.proceed(request)
        }


        /*
        * Esto genera una copia del JWT original y agrega el encabezado de autorización para
        * enviarlo como request
        *
        * chain.proceed(authenticatedRequest) le dice a OkHttp: “continúa el flujo,
        * pero usa esta versión con el token”. Así el servidor recibe la misma petición,
        * pero ahora autenticada.
        */

        val authenticatedRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authenticatedRequest)
    }
}
