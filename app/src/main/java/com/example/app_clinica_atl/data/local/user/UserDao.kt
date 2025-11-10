package com.example.app_clinica_atl.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// 1. Data Access Object (DAO) para la entidad User.
//    Define cómo interactuamos con la tabla 'users' en la BD.
@Dao
interface UserDao {

    // 2. Inserta un nuevo usuario. Si el email ya existe (conflicto), aborta la operación.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    // 3. Busca un usuario por su email (para el login).
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // 4. Obtiene el usuario que está actualmente logueado (para el 'init' del ViewModel).
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedInUser(): Flow<UserEntity?>

    // 5. Marca a un usuario como 'logueado'.
    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setLoggedIn(userId: Long)

    // 6. Desmarca a TODOS los usuarios como 'logueados' (para el logout).
    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun setAllLoggedOut()

    // --- ¡NUEVA FUNCIÓN! ---
    @Query("UPDATE users SET photoUri = :uri WHERE id = :userId")
    suspend fun updateUserPhoto(userId: Long, uri: String?)
    // --- FIN ---
}