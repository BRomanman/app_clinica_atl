package com.example.app_clinica_atl.data.model

/**
 * Representa la información de un doctor tal como se recoge en el flujo
 * de creación/gestión del menú de administrador.
 */
data class DoctorInfo(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val email: String = "",
    val contactNumber: String = "",
    val password: String = "",
    val consultationRate: String = "",
    val salary: String = "",
    val bonus: String = "",
    val specialtyId: String = "",
    val specialty: String = "",
    val availability: String = "",
    val address: String = "",
    val since: String = "",
    val photoUri: String? = null
) {
    val name: String
        get() = buildString {
            if (firstName.isNotBlank()) append(firstName.trim())
            if (lastName.isNotBlank()) {
                if (isNotEmpty()) append(' ')
                append(lastName.trim())
            }
        }.ifBlank { firstName.ifBlank { lastName.ifBlank { email } } }
}
