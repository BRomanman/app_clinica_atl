package com.example.app_clinica_atl.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.app_clinica_atl.data.local.appointment.AppointmentDao
import com.example.app_clinica_atl.data.local.appointment.AppointmentEntity
import com.example.app_clinica_atl.data.model.DoctorInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AppointmentRepository(
    // --- 1. INYECTAR EL DAO REAL ---
    private val appointmentDao: AppointmentDao,
    // --- 2. ¡NUEVO! INYECTAR EL REPOSITORIO DE DOCTORES REAL ---
    private val doctorRepository: DoctorRepository
) {

    // --- 3. ¡ELIMINADO! Ya no tenemos la lista falsa de doctores ---

    // --- 4. ACTUALIZADO: Obtiene doctores del DoctorRepository ---
    fun getDoctorsByDepartment(department: String): List<DoctorInfo> {
        // Llama al repositorio real de doctores
        return doctorRepository.getDoctorsBySpecialty(department)
    }

    // --- 5. ACTUALIZADO: Obtiene departamentos del DoctorRepository ---
    fun getDepartments(): List<String> {
        // Llama al repositorio real de doctores
        return doctorRepository.getSpecialties()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAvailableSlots(date: LocalDate, doctor: DoctorInfo): List<LocalTime> {
        // (La lógica de horas disponibles sigue siendo mockeada, lo cual está bien)
        return listOf(
            LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0),
            LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30),
            LocalTime.of(14, 0), LocalTime.of(14, 30), LocalTime.of(15, 0)
        )
    }

    // --- 6. ACTUALIZADO: Guardar la cita real con los nuevos campos ---
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveAppointment(
        patientId: Long,
        patientName: String, // <-- NUEVO
        doctorId: String,    // <-- NUEVO
        doctorName: String,
        department: String,
        date: LocalDate,
        time: LocalTime
    ) {
        val appointment = AppointmentEntity(
            patientId = patientId,
            patientName = patientName, // <-- NUEVO
            doctorId = doctorId,       // <-- NUEVO
            doctorName = doctorName,
            department = department,
            date = date.format(DateTimeFormatter.ISO_LOCAL_DATE), // "2025-11-20"
            time = time.format(DateTimeFormatter.ISO_LOCAL_TIME)  // "10:30:00"
        )
        appointmentDao.insertAppointment(appointment)
    }

    // (Esta sigue igual, para el paciente)
    fun getAppointmentsForUser(patientId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getAppointmentsForUser(patientId)
    }

    // --- 7. ¡NUEVO! Función para la agenda del doctor ---
    fun getAppointmentsForDoctor(doctorId: String): Flow<List<AppointmentEntity>> {
        return appointmentDao.getAppointmentsForDoctor(doctorId)
    }
    // --- FIN 7 ---

    suspend fun deleteAppointment(appointmentId: Long) {
        appointmentDao.deleteAppointmentById(appointmentId)
    }
}