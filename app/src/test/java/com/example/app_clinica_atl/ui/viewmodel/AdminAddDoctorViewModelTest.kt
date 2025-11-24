package com.example.app_clinica_atl.ui.viewmodel

import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.SpecialtyRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class AdminAddDoctorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val usuariosRepository: UsuariosRepository = mockk()
    private fun specialtyRepository(
        specialties: List<EspecialidadDto> = listOf(EspecialidadDto(1, "Cardiologia", 0.0)),
        failName: String? = null
    ): SpecialtyRepository = object : SpecialtyRepository {
        override fun getAllSpecialties(): Flow<List<EspecialidadDto>> = flowOf(specialties)
        override suspend fun createSpecialty(body: EspecialidadRequestDto): Result<EspecialidadDto> {
            return if (failName != null && body.nombre == failName) {
                Result.failure(IllegalStateException("Error creando especialidad: ${body.nombre}"))
            } else {
                Result.success(EspecialidadDto(99, body.nombre ?: "Nueva", 0.0))
            }
        }
    }

    @Test
    fun `registro sin datos marca errores y no avanza`() = runTest {
        val vm = AdminAddDoctorViewModel(usuariosRepository, specialtyRepository())
        advanceUntilIdle()

        vm.registerDoctor()
        val state = vm.uiState.value

        assertEquals("Nombre es requerido.", state.firstNameError)
        assertEquals("Apellido es requerido.", state.lastNameError)
        assertEquals("Fecha de nacimiento es requerida.", state.birthDateError)
        assertEquals("Email es requerido.", state.emailError)
        assertEquals(validateChileanPhoneNumber(""), state.phoneError)
        assertEquals("Salario invalido", state.salaryError)
        assertEquals("Debe seleccionar al menos una", state.specialtiesError)
        assertFalse(state.isLoading)
    }

    @Test
    fun `registro exitoso limpia formulario y expone nombre creado`() = runTest {
        coEvery { usuariosRepository.register(any()) } returns Result.success(
            UsuarioDto(id = 1, name = "Ana Perez", email = "ana@atl.cl", phone = "+56912345678", password = "x", role = "doctor")
        )
        coEvery { usuariosRepository.createDoctorForUser(any(), any(), any(), any()) } returns Result.success(10L)

        val vm = AdminAddDoctorViewModel(usuariosRepository, specialtyRepository())
        advanceUntilIdle()

        vm.onFirstNameChange("Ana")
        vm.onLastNameChange("Perez")
        vm.onBirthDateChange("1990-01-01")
        vm.onEmailChange("ana@atl.cl")
        vm.onPhoneChange("+56912345678")
        vm.onSalaryChange("1500000")
        vm.toggleSpecialty("Cardiologia")

        vm.registerDoctor()
        advanceUntilIdle()
        val state = vm.uiState.value

        assertTrue(state.registrationSuccess)
        assertEquals("Ana Perez", state.createdDoctorName)
        assertEquals("", state.firstName)
        assertEquals("", state.lastName)
        assertEquals("", state.birthDate)
        assertEquals("", state.email)
        assertEquals("", state.phone)
        assertEquals("", state.salary)
    }

    @Test
    fun `falla al crear usuario muestra error y detiene flujo`() = runTest {
        coEvery { usuariosRepository.register(any()) } returns Result.failure(Exception("correo duplicado"))

        val vm = AdminAddDoctorViewModel(usuariosRepository, specialtyRepository())
        advanceUntilIdle()

        vm.onFirstNameChange("Ana")
        vm.onLastNameChange("Perez")
        vm.onBirthDateChange("1990-01-01")
        vm.onEmailChange("ana@atl.cl")
        vm.onPhoneChange("+56912345678")
        vm.onSalaryChange("1500000")
        vm.toggleSpecialty("Cardiologia")

        vm.registerDoctor()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals("Error creando usuario: correo duplicado", state.errorMsg)
        assertFalse(state.registrationSuccess)
    }

    @Test
    fun `falla al crear doctor muestra error`() = runTest {
        coEvery { usuariosRepository.register(any()) } returns Result.success(
            UsuarioDto(id = 1, name = "Ana Perez", email = "ana@atl.cl", phone = "+56912345678", password = "x", role = "doctor")
        )
        coEvery { usuariosRepository.createDoctorForUser(any(), any(), any(), any()) } returns Result.failure(Exception("backend down"))

        val vm = AdminAddDoctorViewModel(usuariosRepository, specialtyRepository())
        advanceUntilIdle()

        vm.onFirstNameChange("Ana")
        vm.onLastNameChange("Perez")
        vm.onBirthDateChange("1990-01-01")
        vm.onEmailChange("ana@atl.cl")
        vm.onPhoneChange("+56912345678")
        vm.onSalaryChange("1500000")
        vm.toggleSpecialty("Cardiologia")

        vm.registerDoctor()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals("Error creando ficha de doctor", state.errorMsg)
        assertFalse(state.registrationSuccess)
    }

    @Test
    fun `falla al crear especialidad nueva detiene flujo`() = runTest {
        coEvery { usuariosRepository.register(any()) } returns Result.success(
            UsuarioDto(id = 1, name = "Ana Perez", email = "ana@atl.cl", phone = "+56912345678", password = "x", role = "doctor")
        )
        coEvery { usuariosRepository.createDoctorForUser(any(), any(), any(), any()) } returns Result.success(10L)

        val vm = AdminAddDoctorViewModel(usuariosRepository, specialtyRepository(failName = "Urgencias"))
        advanceUntilIdle()

        vm.onFirstNameChange("Ana")
        vm.onLastNameChange("Perez")
        vm.onBirthDateChange("1990-01-01")
        vm.onEmailChange("ana@atl.cl")
        vm.onPhoneChange("+56912345678")
        vm.onSalaryChange("1500000")
        vm.toggleSpecialty("Cardiologia")
        vm.onNewSpecialtyNameChange("Urgencias")
        vm.confirmNewSpecialty()

        vm.registerDoctor()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals("Error creando especialidad: Urgencias", state.errorMsg)
        assertFalse(state.registrationSuccess)
    }

    @Test
    fun `clearSuccess resetea flags y nombre`() = runTest {
        val vm = AdminAddDoctorViewModel(usuariosRepository, specialtyRepository())
        advanceUntilIdle()

        vm.onFirstNameChange("Ana")
        vm.onLastNameChange("Perez")
        vm.onBirthDateChange("1990-01-01")
        vm.onEmailChange("ana@atl.cl")
        vm.onPhoneChange("+56912345678")
        vm.onSalaryChange("1500000")
        vm.toggleSpecialty("Cardiologia")

        coEvery { usuariosRepository.register(any()) } returns Result.success(
            UsuarioDto(id = 1, name = "Ana Perez", email = "ana@atl.cl", phone = "+56912345678", password = "x", role = "doctor")
        )
        coEvery { usuariosRepository.createDoctorForUser(any(), any(), any(), any()) } returns Result.success(10L)

        vm.registerDoctor()
        advanceUntilIdle()
        vm.clearSuccess()

        val state = vm.uiState.value
        assertFalse(state.registrationSuccess)
        assertNull(state.createdDoctorName)
    }
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
