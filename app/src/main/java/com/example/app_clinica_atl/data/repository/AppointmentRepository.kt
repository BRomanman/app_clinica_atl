package com.example.app_clinica_atl.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.app_clinica_atl.ui.viewmodel.AppointmentRequest
import kotlinx.coroutines.delay
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
class AppointmentRepository(
    private val doctorRepository: DoctorRepository
) {

    private val availableTimes = listOf(
        LocalTime.of(9, 0),
        LocalTime.of(10, 0),
        LocalTime.of(11, 30),
        LocalTime.of(12, 30),
        LocalTime.of(14, 0),
        LocalTime.of(15, 30),
        LocalTime.of(17, 0)
    )

    fun getDepartments(): List<String> = doctorRepository.getSpecialties()

    fun getDoctorsByDepartment(department: String) =
        doctorRepository.getDoctorsBySpecialty(department)

    fun getAvailableTimes(): List<LocalTime> = availableTimes

    suspend fun submitAppointment(request: AppointmentRequest): Result<Boolean> {
        delay(1500)
        Log.d(
            "AppointmentRepository",
            "Reserva enviada (simulado): ${request.doctor.name} - ${request.date} ${request.time}"
        )
        return Result.success(true)
    }
}
