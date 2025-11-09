package com.example.app_clinica_atl.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_clinica_atl.data.repository.AppointmentRepository
import com.example.app_clinica_atl.data.repository.UserRepository
// --- 1. IMPORTAR EL REPOSITORIO QUE FALTA ---
import com.example.app_clinica_atl.data.repository.DoctorRepository

class BookAppointmentViewModelFactory(
    private val repository: AppointmentRepository,
    private val userRepository: UserRepository,
    // --- 2. AÑADIR DOCTORREPOSITORY AL CONSTRUCTOR ---
    private val doctorRepository: DoctorRepository
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookAppointmentViewModel::class.java)) {
            // --- 3. PASAR LOS TRES REPOSITORIOS AL VIEWMODEL ---
            return BookAppointmentViewModel(repository, userRepository, doctorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}