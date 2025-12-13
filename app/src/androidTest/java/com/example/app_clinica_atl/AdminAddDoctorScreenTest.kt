package com.example.app_clinica_atl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_clinica_atl.data.remote.dto.AdministradorUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.DoctorCreateRequestDto
import com.example.app_clinica_atl.data.remote.dto.DoctorDto
import com.example.app_clinica_atl.data.remote.dto.DoctorUpdateRequestDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadRequestDto
import com.example.app_clinica_atl.data.remote.dto.EspecialidadUpdateRequestDto
import com.example.app_clinica_atl.data.repository.AdminRepository
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import com.example.app_clinica_atl.ui.screen.admin.AdminAddDoctorScreen
import com.example.app_clinica_atl.ui.viewmodel.admin.AdminAddDoctorViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminAddDoctorScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun buildViewModel(
        specialties: List<EspecialidadDto> = listOf(EspecialidadDto(1, "Cardiologia", 0.0))
    ): AdminAddDoctorViewModel {
        val repo = fakeAdminRepository(specialties)
        return AdminAddDoctorViewModel(repo)
    }

    private fun fakeAdminRepository(
        specialties: List<EspecialidadDto>,
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
        override suspend fun updateDoctor(doctorId: Long, request: DoctorUpdateRequestDto) = Result.failure(IllegalStateException("not used"))
        override suspend fun deactivateDoctor(doctorId: Long) = Result.success(Unit)

        override suspend fun getAllSpecialties() = Result.success(specialties)
        override suspend fun createSpecialty(request: EspecialidadRequestDto) = Result.failure(IllegalStateException("not used"))
        override suspend fun updateSpecialty(id: Long, request: EspecialidadUpdateRequestDto) = Result.failure(IllegalStateException("not used"))
        override suspend fun deleteSpecialty(id: Long) = Result.failure(IllegalStateException("not used"))
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
        composeRule.onNodeWithText("Debe seleccionar una especialidad").assertIsDisplayed()
    }

    @Test
    fun flujo_exitoso_muestra_mensaje_de_exito() {
        val vm = buildViewModel()
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
