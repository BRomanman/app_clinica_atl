package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity

class UserRepository(
    private val userDao: UserDao
) {

    // Login no cambia, solo usa email y password
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    // --- CAMBIO: Firma de register simplificada (Sin id_rol) ---
    suspend fun register(
        nombre: String,
        apellido: String,
        fecha_nacimiento: String,
        email: String,
        phone: String,
        password: String
        // ELIMINADO: id_rol: Long
    ): Result<Long> {
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }

        val id = userDao.insert(
            UserEntity(
                nombre = nombre,
                apellido = apellido,
                fecha_nacimiento = fecha_nacimiento,
                email = email,
                phone = phone,
                password = password,
                // *** CAMBIO CLAVE: Rol 1L (Paciente) asignado permanentemente ***
                id_rol = 1L
            )
        )
        return Result.success(id)
    }
}