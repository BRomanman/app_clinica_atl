package com.example.app_clinica_atl.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_clinica_atl.data.local.appointment.AppointmentDao
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity

// --- 1. DEFINICIÓN DE LA BASE DE DATOS ---
@Database(
    entities = [
        UserEntity::class,
        AppointmentEntity::class
    ],
    version = 4, // <-- 1. VERSIÓN SUBIDA A 4
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun appointmentDao(): AppointmentDao

}

// (Esta migración ya la tenías y está perfecta)
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `appointments` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `patient_id` INTEGER NOT NULL, 
                `doctor_name` TEXT NOT NULL, 
                `department` TEXT NOT NULL, 
                `date` TEXT NOT NULL, 
                `time` TEXT NOT NULL, 
                FOREIGN KEY(`patient_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_appointments_patient_id` ON `appointments` (`patient_id`)")
    }
}

// --- 2. ¡AQUÍ ESTÁ LA MIGRACIÓN QUE FALTABA! ---
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Añadimos las dos nuevas columnas a la tabla existente
        // Usamos DEFAULT para que las filas antiguas no rompan el NOT NULL
        db.execSQL("ALTER TABLE `appointments` ADD COLUMN `patient_name` TEXT NOT NULL DEFAULT 'Paciente'")
        db.execSQL("ALTER TABLE `appointments` ADD COLUMN `doctor_id` TEXT NOT NULL DEFAULT '000'")

        // Creamos el nuevo índice para buscar por doctor
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_appointments_doctor_id` ON `appointments` (`doctor_id`)")
    }
}