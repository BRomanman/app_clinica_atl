package com.example.app_clinica_atl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import com.example.app_clinica_atl.data.remote.dto.UsuarioDto
import com.example.app_clinica_atl.data.repository.SpecialtyRepository
import com.example.app_clinica_atl.data.repository.UsuariosRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import com.example.app_clinica_atl.ui.screen.AdminAddDoctorScreen
import com.example.app_clinica_atl.ui.viewmodel.AdminAddDoctorViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminAddDoctorScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun buildViewModel(
        specialties: List<EspecialidadDto> = listOf(EspecialidadDto(1, "Cardiologia", 0.0)),
        usersRepo: UsuariosRepository = mockk(relaxed = true),
        specialtyRepo: SpecialtyRepository = fakeSpecialtyRepository(specialties)
    ): AdminAddDoctorViewModel = AdminAddDoctorViewModel(usersRepo, specialtyRepo)

    private fun fakeSpecialtyRepository(
        specialties: List<EspecialidadDto>
    ): SpecialtyRepository = object : SpecialtyRepository {
        override fun getAllSpecialties(): Flow<List<EspecialidadDto>> = flowOf(specialties)
        override suspend fun createSpecialty(body: EspecialidadRequestDto): Result<EspecialidadDto> =
            Result.success(EspecialidadDto(99, body.nombre ?: "Nueva", 0.0))
    }

    @Test
    fun muestra_errores_al_enviar_vacio() {
        val vm = buildViewModel()
        composeRule.setContent {
            AdminAddDoctorScreen(viewModel = vm, onBackClick = {})
        }

        composeRule.onNodeWithText("Registrar Doctor").performClick()

        composeRule.onNodeWithText("Nombre es requerido.").assertIsDisplayed()
        composeRule.onNodeWithText("Apellido es requerido.").assertIsDisplayed()
        composeRule.onNodeWithText("Fecha de nacimiento es requerida.").assertIsDisplayed()
        composeRule.onNodeWithText("Email es requerido.").assertIsDisplayed()
        composeRule.onNodeWithText(validateChileanPhoneNumber("")!!).assertIsDisplayed()
        composeRule.onNodeWithText("Salario invalido").assertIsDisplayed()
        composeRule.onNodeWithText("Debe seleccionar al menos una").assertIsDisplayed()
    }

    @Test
    fun flujo_exitoso_muestra_mensaje_de_exito() {
        val usersRepo = mockk<UsuariosRepository>(relaxed = true)
        coEvery { usersRepo.register(any()) } returns Result.success(
            UsuarioDto(
                id = 1,
                name = "Ana Perez",
                email = "ana@atl.cl",
                phone = "+56912345678",
                password = "pass",
                role = "doctor"
            )
        )
        coEvery { usersRepo.createDoctorForUser(any(), any(), any(), any()) } returns Result.success(10L)

        val vm = buildViewModel(usersRepo = usersRepo)

        composeRule.setContent {
            AdminAddDoctorScreen(viewModel = vm, onBackClick = {})
        }

        composeRule.onNodeWithText("Nombre").performTextInput("Ana")
        composeRule.onNodeWithText("Apellido").performTextInput("Perez")
        composeRule.onNodeWithText("Fecha de nacimiento (aaaa-mm-dd)").performTextInput("1990-01-01")
        composeRule.onNodeWithText("Email").performTextInput("ana@atl.cl")
        composeRule.onNodeWithText("Telefono (+56912345678)").performTextInput("+56912345678")
        composeRule.onNodeWithText("Salario (Ej: 2500000)").performTextInput("1500000")
        composeRule.onNodeWithText("Cardiologia").performClick()

        composeRule.onNodeWithText("Registrar Doctor").performClick()

        composeRule.runOnIdle {
            val state = vm.uiState.value
            assertEquals("", state.firstName)
            assertEquals("", state.lastName)
            assertEquals("", state.birthDate)
            assertEquals("", state.email)
            assertEquals("", state.phone)
            assertEquals("", state.salary)
        }
    }
}
