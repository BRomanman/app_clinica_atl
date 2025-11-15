package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun login(email: String, pass: String): Result<UserEntity> {
        // ... (código de login sin cambios)
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getByEmail(email)
                when {
                    user == null -> Result.failure(Exception("El usuario no existe."))
                    user.password != pass -> Result.failure(Exception("La contraseña es incorrecta."))
                    else -> Result.success(user)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun register(newUser: UserEntity): Result<UserEntity> {
        // ... (código de register sin cambios)
        return withContext(Dispatchers.IO) {
            try {
                val existingUser = userDao.getByEmail(newUser.email)
                if (existingUser != null) {
                    throw IllegalStateException("El correo '${newUser.email}' ya está registrado.")
                }

                val newId = userDao.insert(newUser)
                val insertedUser = newUser.copy(id = newId)
                Result.success(insertedUser)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUserById(id: Long): Result<UserEntity> {
        // ... (código de getUserById sin cambios)
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getById(id)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(NoSuchElementException("Usuario no encontrado con ID $id"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // --- ¡¡FUNCIÓN AÑADIDA!! ---
    /**
     * Obtiene un usuario por su ID como un Flow (observable).
     */
    fun getUserByIdAsFlow(id: Long): Flow<UserEntity?> {
        return userDao.getByIdAsFlow(id)
    }
    // --- FIN ---

    suspend fun searchPatients(query: String): Result<List<UserEntity>> {
        // ... (código de searchPatients sin cambios)
        return withContext(Dispatchers.IO) {
            try {
                val patients = userDao.searchPatientsByName(query)
                Result.success(patients)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun getAllDoctors(): Flow<List<UserEntity>> {
        // ... (código de getAllDoctors sin cambios)
        return userDao.getAllDoctors()
    }
}