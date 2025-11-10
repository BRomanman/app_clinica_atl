package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

// 1. Repositorio: Es el ÚNICO punto de acceso a los datos del usuario.
//    Abstrae la lógica de "dónde" vienen los datos (BD, Prefs, API, etc.).
class UserRepository(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) {

    // 2. Obtiene el usuario que está logueado (observa cambios en la BD).
    fun getLoggedInUser(): Flow<UserEntity?> = userDao.getLoggedInUser()

    // 3. Lógica de Login
    suspend fun login(email: String, pass: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                // 3.1. Busca al usuario por email.
                val user = userDao.getUserByEmail(email)
                when {
                    // 3.2. Si no existe, falla.
                    user == null -> {
                        Result.failure(Exception("El usuario no existe."))
                    }
                    // 3.3. Si la contraseña no coincide, falla.
                    user.password != pass -> {
                        Result.failure(Exception("La contraseña es incorrecta."))
                    }
                    // 3.4. ¡Éxito!
                    else -> {
                        // 3.5. Limpia cualquier sesión antigua y marca al nuevo usuario.
                        userDao.setAllLoggedOut()
                        userDao.setLoggedIn(user.id)
                        // 3.6. Guarda el email en las SharedPreferences.
                        userPreferences.setLoggedIn(true)
                        userPreferences.setLoggedInUserEmail(user.email) // Guardamos el email
                        Result.success(user)
                    }
                }
            } catch (e: Exception) {
                // 3.7. Captura cualquier error de BD.
                Result.failure(e)
            }
        }
    }

    // 4. Lógica de Registro
    suspend fun register(
        nombre: String,
        apellido: String,
        fecha_nacimiento: String,
        email: String,
        phone: String,
        password: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // 4.1. Crea la entidad del nuevo usuario (Paciente por defecto).
                val newUser = UserEntity(
                    nombre = nombre,
                    apellido = apellido,
                    fecha_nacimiento = fecha_nacimiento,
                    email = email,
                    phone = phone,
                    password = password,
                    id_rol = 1L, // 1 = Paciente
                    photoUri = null // <-- ¡NUEVO!
                )
                // 4.2. Intenta insertarlo.
                userDao.insertUser(newUser)
                Result.success(Unit)
            } catch (e: Exception) {
                // 4.3. Si falla (ej: email duplicado), devuelve el error.
                Result.failure(Exception("El correo '$email' ya está registrado."))
            }
        }
    }

    // --- ¡NUEVA FUNCIÓN! ---
    suspend fun updateUserPhoto(userId: Long, uri: String?) {
        withContext(Dispatchers.IO) {
            userDao.updateUserPhoto(userId, uri)
        }
    }
    // --- FIN ---
}