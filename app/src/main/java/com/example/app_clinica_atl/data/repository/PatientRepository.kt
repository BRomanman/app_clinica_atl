package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.model.Patient

class PatientRepository {

    // Lista fija de 3 famosos como pediste
    fun getPatients(): List<Patient> {
        return listOf(
            Patient(
                id = "p001",
                nombre = "Keanu Reeves",
                direccion = "123 Matrix St, Hollywood, CA",
                numeroContacto = "+1 555-1001",
                correo = "keanu.reeves@example.com",
                historialMedico = "Paciente presenta alergia leve al polen. Historial de fractura de muñeca (izquierda) en 2019. Todas las vacunas al día. Refiere bienestar general."
            ),
            Patient(
                id = "p002",
                nombre = "Taylor Swift",
                direccion = "456 Music Row, Nashville, TN",
                numeroContacto = "+1 555-2002",
                correo = "taylor.swift@example.com",
                historialMedico = "Revisión vocal anual, sin patologías. Reporta episodios ocasionales de migraña controlados con medicación. Sin alergias conocidas. Excelente estado de salud."
            ),
            Patient(
                id = "p003",
                nombre = "Lionel Messi",
                direccion = "789 Futbol Ave, Miami, FL",
                numeroContacto = "+1 555-3003",
                correo = "lionel.messi@example.com",
                historialMedico = "Seguimiento por distensión muscular en isquiotibial derecho (2023). Tratamiento de fisioterapia completado. Dieta y suplementación deportiva bajo supervisión. Vacunas al día."
            )
        )
    }
}