package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.usuario.UsuarioDao
import com.example.app_clinica_atl.data.local.usuario.UsuarioEntity
import com.example.app_clinica_atl.data.remote.RetrofitClient
import com.example.app_clinica_atl.data.remote.UsuariosApi
import com.example.app_clinica_atl.data.remote.dto.toUsuarioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repositorio alineado con la API de Usuarios.
 */
class UsuariosRepository(
    private val userDao: UsuarioDao,
    private val usuariosApi: UsuariosApi = RetrofitClient.usuariosApi
) {

    suspend fun login(email: String, pass: String): Result<UsuarioEntity> {
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

    suspend fun register(newUser: UsuarioEntity): Result<UsuarioEntity> {
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

    suspend fun getUserById(id: Long): Result<UsuarioEntity> {
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
    fun getUserByIdAsFlow(id: Long): Flow<UsuarioEntity?> {
        return userDao.getByIdAsFlow(id)
    }

    suspend fun searchPatients(query: String): Result<List<UsuarioEntity>> {
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

    fun getAllDoctors(): Flow<List<UsuarioEntity>> {
        // ... (código de getAllDoctors sin cambios)
        return userDao.getAllDoctors()
    }

    /**
     * Login contra la API de usuarios (microservicio).
     * Filtra por correo/contraseña y mapea el rol numérico al string usado por la app.
     */
    suspend fun loginViaApi(email: String, pass: String): Result<UsuarioEntity> = withContext(Dispatchers.IO) {
        try {
            val users = usuariosApi.getUsers()
            val match = users.firstOrNull { it.correo.equals(email, ignoreCase = true) && it.contrasena == pass }
            if (match != null) {
                Result.success(match.toUsuarioEntity())
            } else {
                Result.failure(Exception("Credenciales inválidas."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza solo la URL de la imagen de perfil del usuario.
     */
    suspend fun updateProfileImageUrl(userId: Long, imageUrl: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                userDao.updateProfileImageUrl(userId, imageUrl)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
