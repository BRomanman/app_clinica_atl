package com.example.app_clinica_atl.ui.viewmodel

import com.example.app_clinica_atl.data.remote.dto.AdministradorUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.DoctorCreateRequestDto
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadUpdateRequestDto
import com.example.app_clinica_atl.data.repository.AdminRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import com.example.app_clinica_atl.ui.viewmodel.admin.AdminAddDoctorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class AdminAddDoctorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun fakeAdminRepository(
        specialties: List<EspecialidadDto> = listOf(EspecialidadDto(1, "Cardiologia", 0.0)),
        createDoctorResult: Result<DoctorDto> = Result.success(
            DoctorDto(
                id = 1,
                nombre = "Ana",
                apellido = "Perez",
                correo = "ana@atl.cl",
                telefono = "+56912345678"
            )
        )
    ): AdminRepository = object : AdminRepository {
        override suspend fun getAdminProfile(adminId: Long) = Result.failure(IllegalStateException("not used"))
        override suspend fun updateAdminProfile(adminId: Long, request: AdministradorUpdateRequestDto) = Result.failure(IllegalStateException("not used"))
        override suspend fun uploadAdminProfilePhoto(adminId: Long, imageFile: java.io.File) = Result.failure(IllegalStateException("not used"))
        override fun buildAdminProfilePhotoUrl(adminId: Long) = ""
        override suspend fun getAllDoctors() = Result.failure(IllegalStateException("not used"))
        override suspend fun getDoctorById(doctorId: Long) = Result.failure(IllegalStateException("not used"))
        override suspend fun createDoctor(request: DoctorCreateRequestDto) = createDoctorResult
        override suspend fun updateDoctor(doctorId: Long, request: DoctorDto) = Result.failure(IllegalStateException("not used"))
        override suspend fun deactivateDoctor(doctorId: Long) = Result.success(Unit)
        override suspend fun getAllSpecialties() = Result.success(specialties)
        override suspend fun createSpecialty(request: EspecialidadRequestDto) = Result.failure(IllegalStateException("not used"))
        override suspend fun updateSpecialty(id: Long, request: EspecialidadUpdateRequestDto) = Result.failure(IllegalStateException("not used"))
        override suspend fun deleteSpecialty(id: Long) = Result.failure(IllegalStateException("not used"))
    }

    private fun buildViewModel(
        specialties: List<EspecialidadDto> = listOf(EspecialidadDto(1, "Cardiologia", 0.0)),
        createDoctorResult: Result<DoctorDto> = Result.success(
            DoctorDto(
                id = 1,
                nombre = "Ana",
                apellido = "Perez",
                correo = "ana@atl.cl",
                telefono = "+56912345678"
            )
        )
    ): AdminAddDoctorViewModel {
        val repo = fakeAdminRepository(specialties, createDoctorResult)
        return AdminAddDoctorViewModel(repo)
    }

    @Test
    fun `registro sin datos marca errores y no avanza`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.registerDoctor()
        val state = vm.uiState.value

        assertEquals("Nombre es requerido.", state.firstNameError)
        assertEquals("Apellido es requerido.", state.lastNameError)
        assertEquals("Fecha de nacimiento es requerida.", state.birthDateError)
        assertEquals("Email es requerido.", state.emailError)
        assertEquals(validateChileanPhoneNumber(""), state.phoneError)
        assertEquals("Salario invalido", state.salaryError)
        assertEquals("Debe seleccionar una especialidad", state.specialtiesError)
        assertFalse(state.isLoading)
    }

    @Test
    fun `registro exitoso limpia formulario y expone nombre creado`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onFirstNameChange("Ana")
        vm.onLastNameChange("Perez")
        vm.onBirthDateChange("1990-01-01")
        vm.onEmailChange("ana@atl.cl")
        vm.onPhoneChange("+56912345678")
        vm.onSalaryChange("1500000")
        vm.selectFirstBackendSpecialty()

        vm.registerDoctor()
        advanceUntilIdle()
        val state = vm.uiState.value

        assertTrue(state.registrationSuccess)
        assertEquals("", state.firstName)
        assertEquals("", state.lastName)
        assertEquals("", state.birthDate)
        assertEquals("", state.email)
        assertEquals("", state.phone)
        assertEquals("", state.salary)
    }

    @Test
    fun `falla al crear doctor muestra error`() = runTest {
        val vm = buildViewModel(createDoctorResult = Result.failure(Exception("backend down")))
        advanceUntilIdle()

        vm.onFirstNameChange("Ana")
        vm.onLastNameChange("Perez")
        vm.onBirthDateChange("1990-01-01")
        vm.onEmailChange("ana@atl.cl")
        vm.onPhoneChange("+56912345678")
        vm.onSalaryChange("1500000")
        vm.selectFirstBackendSpecialty()

        vm.registerDoctor()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals("backend down", state.errorMsg)
        assertFalse(state.registrationSuccess)
    }

    @Test
    fun `clearSuccess resetea flags y nombre`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onFirstNameChange("Ana")
        vm.onLastNameChange("Perez")
        vm.onBirthDateChange("1990-01-01")
        vm.onEmailChange("ana@atl.cl")
        vm.onPhoneChange("+56912345678")
        vm.onSalaryChange("1500000")
        vm.selectFirstBackendSpecialty()

        vm.registerDoctor()
        advanceUntilIdle()
        vm.clearSuccess()

        val state = vm.uiState.value
        assertFalse(state.registrationSuccess)
        assertNull(state.createdDoctorName)
    }
}

private fun AdminAddDoctorViewModel.selectFirstBackendSpecialty() {
    val specialty = uiState.value.backendSpecialties.firstOrNull() ?: return
    toggleBackendSpecialty(specialty)
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
