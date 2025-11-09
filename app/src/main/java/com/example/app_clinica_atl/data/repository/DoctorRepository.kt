package com.example.app_clinica_atl.data.repository

import com.example.app_clinica_atl.data.model.DoctorInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DoctorRepository {

    private val initialDoctors = listOf(
        // --- ¡AQUÍ ESTÁ LA SOLUCIÓN! ---
        // Añadimos al doctor "Víctor Rosendo" a esta lista "falsa" (mock)
        // para que coincida con el usuario que SÍ existe en la base de datos (Room).
        DoctorInfo(
            id = "000", // ID de doctor (puede ser diferente al ID de usuario de Room)
            firstName = "Víctor",
            lastName = "Rosendo",
            birthDate = "2000-05-10",
            email = "victor@duoc.cl", // <-- Este email AHORA SÍ COINCIDE
            contactNumber = "+56922222222",
            password = "123456",
            consultationRate = "40000",
            salary = "2000000",
            bonus = "150000",
            specialtyId = "MED",
            specialty = "Medicina General",
            availability = "Martes y Jueves, 10:00 - 18:00",
            address = "Av. Clínica Duoc 555, Santiago",
            since = "2020"
        ),
        // --- FIN DE LA SOLUCIÓN ---

        DoctorInfo(
            id = "001",
            firstName = "Ana",
            lastName = "Perez",
            birthDate = "1980-05-14",
            email = "ana.perez@atlclinic.cl",
            contactNumber = "+56 2 2100 1001",
            password = "temporal123",
            consultationRate = "45000",
            salary = "2200000",
            bonus = "180000",
            specialtyId = "MED",
            specialty = "Medicina General",
            availability = "Lunes a Viernes, 08:00 - 16:00",
            address = "Av. Salud 123, Santiago",
            since = "2010"
        ),
        DoctorInfo(
            id = "002",
            firstName = "Juana",
            lastName = "Perez",
            birthDate = "1982-09-03",
            email = "juana.perez@atlclinic.cl",
            contactNumber = "+56 2 2100 1002",
            password = "temporal123",
            consultationRate = "43000",
            salary = "2100000",
            bonus = "175000",
            specialtyId = "MED",
            specialty = "Medicina General",
            availability = "Martes a Sabado, 09:00 - 17:00",
            address = "Av. Salud 456, Santiago",
            since = "2011"
        ),
        DoctorInfo(
            id = "003",
            firstName = "Sofia",
            lastName = "Morales",
            birthDate = "1983-01-22",
            email = "sofia.morales@atlclinic.cl",
            contactNumber = "+56 2 2100 2001",
            password = "temporal123",
            consultationRate = "48000",
            salary = "2300000",
            bonus = "190000",
            specialtyId = "CARD",
            specialty = "Cardiologia",
            availability = "Lunes a Jueves, 09:00 - 16:00",
            address = "Av. Cardio 120, Santiago",
            since = "2012"
        ),
        DoctorInfo(
            id = "004",
            firstName = "Juan",
            lastName = "Torres",
            birthDate = "1978-11-10",
            email = "juan.torres@atlclinic.cl",
            contactNumber = "+56 2 2100 2002",
            password = "temporal123",
            consultationRate = "50000",
            salary = "2350000",
            bonus = "200000",
            specialtyId = "CARD",
            specialty = "Cardiologia",
            availability = "Martes a Viernes, 10:00 - 18:00",
            address = "Av. Cardio 98, Santiago",
            since = "2014"
        ),
        DoctorInfo(
            id = "005",
            firstName = "Nicolas",
            lastName = "Diaz",
            birthDate = "1985-07-18",
            email = "nicolas.diaz@atlclinic.cl",
            contactNumber = "+56 2 2100 3001",
            password = "temporal123",
            consultationRate = "42000",
            salary = "2050000",
            bonus = "160000",
            specialtyId = "DERM",
            specialty = "Dermatologia",
            availability = "Lunes a Viernes, 09:30 - 17:30",
            address = "Av. Dermis 45, Santiago",
            since = "2013"
        ),
        DoctorInfo(
            id = "006",
            firstName = "Isabel",
            lastName = "Soto",
            birthDate = "1987-02-05",
            email = "isabel.soto@atlclinic.cl",
            contactNumber = "+56 2 2100 3002",
            password = "temporal123",
            consultationRate = "41000",
            salary = "1980000",
            bonus = "155000",
            specialtyId = "DERM",
            specialty = "Dermatologia",
            availability = "Martes a Sabado, 08:30 - 15:30",
            address = "Av. Dermis 87, Santiago",
            since = "2015"
        ),
        DoctorInfo(
            id = "007",
            firstName = "Gabriel",
            lastName = "Molina",
            birthDate = "1979-03-21",
            email = "gabriel.molina@atlclinic.cl",
            contactNumber = "+56 2 2100 4001",
            password = "temporal123",
            consultationRate = "40000",
            salary = "1950000",
            bonus = "150000",
            specialtyId = "PED",
            specialty = "Pediatria",
            availability = "Lunes a Viernes, 08:00 - 15:00",
            address = "Av. Infantil 12, Santiago",
            since = "2010"
        ),
        DoctorInfo(
            id = "008",
            firstName = "Fernanda",
            lastName = "Morales",
            birthDate = "1984-09-09",
            email = "fernanda.morales@atlclinic.cl",
            contactNumber = "+56 2 2100 4002",
            password = "temporal123",
            consultationRate = "40500",
            salary = "1980000",
            bonus = "152000",
            specialtyId = "PED",
            specialty = "Pediatria",
            availability = "Lunes a Viernes, 09:00 - 17:00",
            address = "Av. Infantil 34, Santiago",
            since = "2011"
        ),
        DoctorInfo(
            id = "009",
            firstName = "Sebastian",
            lastName = "Flores",
            birthDate = "1981-12-16",
            email = "sebastian.flores@atlclinic.cl",
            contactNumber = "+56 2 2100 5001",
            password = "temporal123",
            consultationRate = "46000",
            salary = "2100000",
            bonus = "170000",
            specialtyId = "PSI",
            specialty = "Psicologia",
            availability = "Lunes a Viernes, 10:00 - 19:00",
            address = "Av. Apoyo 55, Santiago",
            since = "2013"
        ),
        DoctorInfo(
            id = "010",
            firstName = "Catalina",
            lastName = "Reyes",
            birthDate = "1986-04-02",
            email = "catalina.reyes@atlclinic.cl",
            contactNumber = "+56 2 2100 5002",
            password = "temporal123",
            consultationRate = "45500",
            salary = "2080000",
            bonus = "168000",
            specialtyId = "PSI",
            specialty = "Psicologia",
            availability = "Martes a Sabado, 09:00 - 17:00",
            address = "Av. Apoyo 59, Santiago",
            since = "2014"
        ),
        DoctorInfo(
            id = "011",
            firstName = "Veronica",
            lastName = "Contreras",
            birthDate = "1977-08-27",
            email = "veronica.contreras@atlclinic.cl",
            contactNumber = "+56 2 2100 6001",
            password = "temporal123",
            consultationRate = "38000",
            salary = "1900000",
            bonus = "140000",
            specialtyId = "NUT",
            specialty = "Nutricion",
            availability = "Lunes a Viernes, 08:30 - 16:30",
            address = "Av. Bienestar 70, Santiago",
            since = "2011"
        ),
        DoctorInfo(
            id = "012",
            firstName = "Felipe",
            lastName = "Lagos",
            birthDate = "1983-06-30",
            email = "felipe.lagos@atlclinic.cl",
            contactNumber = "+56 2 2100 6002",
            password = "temporal123",
            consultationRate = "38500",
            salary = "1920000",
            bonus = "142000",
            specialtyId = "NUT",
            specialty = "Nutricion",
            availability = "Lunes a Viernes, 11:00 - 19:00",
            address = "Av. Bienestar 74, Santiago",
            since = "2012"
        )
    )

    private val doctors = MutableStateFlow(initialDoctors)

    fun observeDoctors(): StateFlow<List<DoctorInfo>> = doctors.asStateFlow()

    fun getAllDoctors(): List<DoctorInfo> = doctors.value

    fun getDoctorById(id: String): DoctorInfo? =
        doctors.value.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }

    fun getDoctorByEmail(email: String): DoctorInfo? =
        doctors.value.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }

    fun updateDoctor(updated: DoctorInfo): Boolean {
        val sanitized = updated.sanitized()
        var success = false
        doctors.update { current ->
            val index = current.indexOfFirst { it.id.equals(sanitized.id, ignoreCase = true) }
            if (index >= 0) {
                success = true
                current.toMutableList().apply { this[index] = sanitized }
            } else {
                current
            }
        }
        return success
    }

    fun deleteDoctorById(id: String): Boolean {
        val targetId = id.trim()
        if (targetId.isEmpty()) return false

        var success = false
        doctors.update { current ->
            val index = current.indexOfFirst { it.id.equals(targetId, ignoreCase = true) }
            if (index >= 0) {
                success = true
                current.toMutableList().apply { removeAt(index) }
            } else {
                current
            }
        }
        return success
    }

    fun getSpecialties(): List<String> = doctors.value.map { it.specialty }.distinct()

    fun getDoctorsBySpecialty(specialty: String): List<DoctorInfo> =
        doctors.value.filter { it.specialty.equals(specialty, ignoreCase = true) }

    private fun DoctorInfo.sanitized(): DoctorInfo = copy(
        id = id.trim(),
        firstName = firstName.trim(),
        lastName = lastName.trim(),
        birthDate = birthDate.trim(),
        email = email.trim(),
        contactNumber = contactNumber.trim(),
        password = password.trim(),
        consultationRate = consultationRate.trim(),
        salary = salary.trim(),
        bonus = bonus.trim(),
        specialtyId = specialtyId.trim(),
        specialty = specialty.trim(),
        availability = availability.trim(),
        address = address.trim(),
        since = since.trim()
    )
}