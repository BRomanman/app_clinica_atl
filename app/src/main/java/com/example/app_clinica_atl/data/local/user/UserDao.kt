package com.example.app_clinica_atl.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM user_table WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT COUNT(*) FROM user_table")
    suspend fun count(): Int

    @Query("SELECT * FROM user_table WHERE role = 'doctor' AND specialty = :specialty")
    suspend fun getDoctorsBySpecialty(specialty: String): List<UserEntity>

    @Query("SELECT * FROM user_table ORDER BY role, name ASC")
    suspend fun getAllUsers(): List<UserEntity>

    // --- ¡¡FUNCIÓN AÑADIDA PARA LA BÚSQUEDA!! ---
    /**
     * Busca usuarios que sean pacientes y cuyo nombre contenga el texto de búsqueda.
     * Los '%' son comodines que significan "cualquier cosa".
     * Ej: buscar "ana" encontrará "Ana Torres".
     */
    @Query("SELECT * FROM user_table WHERE role = 'paciente' AND name LIKE '%' || :query || '%'")
    suspend fun searchPatientsByName(query: String): List<UserEntity>
}