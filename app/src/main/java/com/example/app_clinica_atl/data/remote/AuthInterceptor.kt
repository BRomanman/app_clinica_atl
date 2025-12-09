package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.local.storage.UserPreferences
import okhttp3.Interceptor
import okhttp3.Response

private val LOGIN_PATHS = setOf("/api/v1/auth/login", "/auth/login")

class AuthInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        if (request.method == "POST" && LOGIN_PATHS.contains(path)) {
            return chain.proceed(request)
        }

        val token = userPreferences.currentToken()?.takeIf { it.isNotBlank() }
        if (token == null) {
            return chain.proceed(request)
        }

        val shouldAttach = request.url.host.contains("10.0.2.2")
        if (!shouldAttach) {
            return chain.proceed(request)
        }

        val authenticatedRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authenticatedRequest)
    }
}
