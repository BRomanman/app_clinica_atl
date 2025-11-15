package com.example.app_clinica_atl.data.local.specialty

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecialtyDao {

    /**
     * Inserta una nueva especialidad.
     * Si el nombre ya existe (por el "índice único"), la operación fallará.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(specialty: SpecialtyEntity)

    /**
     * Elimina una especialidad.
     */
    @Delete
    suspend fun delete(specialty: SpecialtyEntity)

    /**
     * Obtiene TODAS las especialidades de la tabla, ordenadas por nombre.
     * Devuelve un Flow para que la UI se actualice automáticamente.
     */
    @Query("SELECT * FROM specialty_table ORDER BY name ASC")
    fun getAllSpecialties(): Flow<List<SpecialtyEntity>>
}