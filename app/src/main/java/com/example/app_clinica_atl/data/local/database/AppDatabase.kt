package com.example.app_clinica_atl.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class],
    version = 2, // <-- CAMBIO: versión incrementada a 2
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "ui_navegacion.db" // (Nombre de DB se mantiene del proyecto original)

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
                                val dao = getInstance(context).userDao()

                                // --- CAMBIO: Datos de prueba actualizados ---
                                val seed = listOf(

                                    //Funcional
                                    UserEntity(
                                        nombre = "Admin",
                                        apellido = "Root",
                                        fecha_nacimiento = "1990-01-01",
                                        email = "admin@duocuc.cl",
                                        phone = "+56911111111",
                                        password = "Admin123!",
                                        id_rol = 3L // Rol 3 = Admin
                                    ),

                                    //Funcional
                                    UserEntity(
                                        nombre = "Víctor",
                                        apellido = "Rosendo",
                                        fecha_nacimiento = "2000-05-10",
                                        email = "victor@duocuc.cl",
                                        phone = "+56922222222",
                                        password = "123456",
                                        id_rol = 2L // Rol 2 = Doctor
                                    ),

                                    //Funcional
                                    UserEntity(
                                        nombre = "Carlos",
                                        apellido = "Sainz",
                                        fecha_nacimiento = "1997-08-11",
                                        email = "csainz@duocuc.cl",
                                        phone = "+56933333333",
                                        password = "123456",
                                        id_rol = 1L // Rol 1 = Paciente
                                    )

                                )
                                // --- FIN DE CAMBIO ---

                                if (dao.count() == 0) {
                                    seed.forEach { dao.insert(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration() // Destruye la DB si la versión cambia
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}