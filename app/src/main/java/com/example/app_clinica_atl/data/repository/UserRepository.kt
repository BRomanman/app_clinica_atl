package com.example.app_clinica_atl.data.repository

// --- 1. IMPORTAR LO NECESARIO ---
import com.example.app_clinica_atl.data.local.storage.UserPreferences
import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository(
    private val userDao: UserDao,
    // --- 2. INYECTAR EL DATASTORE ---
    private val userPreferences: UserPreferences
) {

    // Login no cambia, solo usa email y password
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            // --- 3. GUARDAR EL USUARIO LOGUEADO EN DATASTORE ---
            // (Guardamos el email, que es único, para saber quién es)
            userPreferences.setLoggedInUserEmail(user.email)
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    // --- 4. LA NUEVA FUNCIÓN QUE FALTABA ---
    /**
     * Obtiene la entidad del usuario que tiene la sesión activa.
     * Lee el email guardado en DataStore y lo busca en Room.
     */
    suspend fun getLoggedInUser(): Flow<UserEntity?> {
        // Obtenemos el email guardado en DataStore (ej: "csainz@duoc.cl")
        val loggedInEmail = userPreferences.loggedInUserEmail.first()
        if (loggedInEmail == null) {
            // Si no hay nadie logueado, devolvemos un Flow nulo
            return kotlinx.coroutines.flow.flowOf(null)
        }
        // Buscamos a ese usuario en Room y devolvemos el Flow
        return userDao.getByEmailFlow(loggedInEmail)
    }
    // --- FIN 4 ---

    // --- CAMBIO: Firma de register simplificada (Sin id_rol) ---
    suspend fun register(
        nombre: String,
        apellido: String,
        fecha_nacimiento: String,
        email: String,
        phone: String,
        password: String
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