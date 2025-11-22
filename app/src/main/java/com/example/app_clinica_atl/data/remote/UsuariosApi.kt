package com.example.app_clinica_atl.data.remote

import com.example.app_clinica_atl.data.remote.dto.LoginRequestDto
import com.example.app_clinica_atl.data.remote.dto.LoginResponseDto
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadResponseDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioResponseDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioUpdateRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuariosApi {

    //AuthController
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequestDto): LoginResponseDto

    // Especialidades (personal_service)
    @GET("especialidades")
    suspend fun getAllSpecialties(): Response<List<EspecialidadResponseDto>>




    //UsuarioController
    @GET("usuarios")
    suspend fun getUsers(): List<UsuarioResponseDto>

    @GET("usuarios/{id}")
    suspend fun getUserById(@Path("id") id: Long): UsuarioResponseDto

    @POST("usuarios")
    suspend fun createUser(@Body user: UsuarioUpdateRequestDto): UsuarioResponseDto

    @PUT("usuarios/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body user: UsuarioUpdateRequestDto
    ): UsuarioResponseDto

    @DELETE("usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>






    //DoctorController
    @GET("doctores")
    suspend fun getDoc(): List<DoctorDto>

    @GET("doctores/{id}")
    suspend fun getDocById(@Path("id") id: Long): DoctorDto

    @POST("doctores")
    suspend fun createDoc(@Body doc: DoctorDto): DoctorDto

    @PUT("doctores/{id}")
    suspend fun updateDoc(
        @Path("id") id: Long,
        @Body doc: DoctorDto
    ): DoctorDto

    @DELETE("doctores/{id}")
    suspend fun deleteDoc(@Path("id") id: Long): Response<Unit>

    // Especialidades por doctor (personal_service)
    @GET("doctores/{doctorId}/especialidades")
    suspend fun getDoctorSpecialties(@Path("doctorId") doctorId: Long): List<EspecialidadResponseDto>




}
