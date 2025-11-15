package com.example.app_clinica_atl.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_clinica_atl.data.local.appointment.AppointmentDao
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
// --- IMPORTS AÑADIDOS ---
import com.example.app_clinica_atl.data.local.insurance.InsuranceDao
import com.example.app_clinica_atl.data.local.insurance.InsuranceEntity
import com.example.app_clinica_atl.data.local.insurance.UserInsuranceEntity
// --- FIN IMPORTS ---
import com.example.app_clinica_atl.data.local.specialty.SpecialtyDao
import com.example.app_clinica_atl.data.local.specialty.SpecialtyEntity
import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        AppointmentEntity::class,
        SpecialtyEntity::class,
        InsuranceEntity::class,     // <-- ¡TABLA AÑADIDA!
        UserInsuranceEntity::class  // <-- ¡TABLA AÑADIDA!
    ],
    version = 8, // <-- ¡VERSIÓN INCREMENTADA A 8!
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun specialtyDao(): SpecialtyDao
    abstract fun insuranceDao(): InsuranceDao // <-- ¡DAO AÑADIDO!

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "app_clinica_atl.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                prepopulateDatabase(getInstance(context))
                            }
                        }
                    })
                    // Al migrar de v7 a v8, destruimos la data anterior
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * "Siembra" la base de datos con datos de prueba reales.
         */
        suspend fun prepopulateDatabase(db: AppDatabase) {
            val userDao = db.userDao()
            if (userDao.count() == 0) {
                // (Inserta usuarios, igual que antes)
                val users = listOf(
                    UserEntity(
                        name = "Admin User", email = "admin@clinica.cl", phone = "+56911111111",
                        password = "Admin123!", role = "admin"
                    ),
                    UserEntity(
                        name = "Bruno Roman", email = "bruno@paciente.cl", phone = "+56922222222",
                        password = "123", role = "paciente"
                    ),
                    UserEntity(
                        name = "Ana Torres", email = "ana@paciente.cl", phone = "+56933333333",
                        password = "123", role = "paciente"
                    ),
                    UserEntity(
                        name = "Dr. Juan Pérez", email = "jperez@clinica.cl", phone = "+56944444444",
                        password = "123", role = "doctor", specialty = "Cardiología",
                        salary = 2500000.0
                    ),
                    UserEntity(
                        name = "Dra. Ana Gómez", email = "agomez@clinica.cl", phone = "+56955555555",
                        password = "123", role = "doctor", specialty = "Dermatología",
                        salary = 2200000.0
                    ),
                    UserEntity(
                        name = "Dr. Carlos Smith", email = "csmith@clinica.cl", phone = "+56966666666",
                        password = "123", role = "doctor", specialty = "Medicina General",
                        salary = 2000000.0
                    ),
                    UserEntity(
                        name = "Dra. María López", email = "mlopez@clinica.cl", phone = "+56977777777",
                        password = "123", role = "doctor", specialty = "Pediatría",
                        salary = 2300000.0
                    ),
                    UserEntity(
                        name = "Dra. Sofía Martin", email = "smartin@clinica.cl", phone = "+56988888888",
                        password = "123", role = "doctor", specialty = "Psicología",
                        salary = 2100000.0
                    )
                )
                users.forEach { userDao.insert(it) }
            }

            // --- ¡¡LÓGICA AÑADIDA PARA SEGUROS!! ---
            // "Sembramos" los tipos de seguro
            val insuranceDao = db.insuranceDao()
            val insurances = listOf(
                InsuranceEntity(
                    id = 1, name = "Plan Básico",
                    description = "Cobertura esencial para consultas generales.",
                    price = 15000.0
                ),
                InsuranceEntity(
                    id = 2, name = "Plan Familiar",
                    description = "Cobertura completa para ti y tu familia.",
                    price = 45000.0
                ),
                InsuranceEntity(
                    id = 3, name = "Plan Premium",
                    description = "Acceso total a todas las especialidades y exámenes.",
                    price = 75000.0
                )
            )
            insurances.forEach { insuranceDao.insertInsurance(it) }
        }
    }
}