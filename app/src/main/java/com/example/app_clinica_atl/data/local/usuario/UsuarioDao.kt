package com.example.app_clinica_atl.data.local.usuario

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM user_table WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UsuarioEntity?

    @Query("SELECT * FROM user_table WHERE id = :id LIMIT 1")
    fun getByIdAsFlow(id: Long): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UsuarioEntity): Long

    @Update
    suspend fun update(user: UsuarioEntity)

    @Query("SELECT COUNT(*) FROM user_table")
    suspend fun count(): Int

    @Query("SELECT * FROM user_table WHERE role = 'doctor' AND specialty = :specialty")
    suspend fun getDoctorsBySpecialty(specialty: String): List<UsuarioEntity>

    @Query("SELECT * FROM user_table ORDER BY role, name ASC")
    suspend fun getAllUsers(): List<UsuarioEntity>

    @Query("SELECT * FROM user_table WHERE role = 'paciente' AND name LIKE '%' || :query || '%'")
    suspend fun searchPatientsByName(query: String): List<UsuarioEntity>

    @Query("SELECT * FROM user_table WHERE role = 'doctor' ORDER BY name ASC")
    fun getAllDoctors(): Flow<List<UserEntity>>

    // --- ¡¡FUNCIÓN AÑADIDA PARA LA CÁMARA!! ---
    /**
     * Actualiza únicamente la URL de la imagen de perfil de un usuario.
     */
    @Query("UPDATE user_table SET profileImageUrl = :imageUrl WHERE id = :userId")
    suspend fun updateProfileImageUrl(userId: Long, imageUrl: String)
}
