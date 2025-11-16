package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuariosApi {

    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDto

    @POST("users")
    suspend fun createUser(@Body user: UserDto): UserDto

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body user: UserDto
    ): UserDto

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>
}
