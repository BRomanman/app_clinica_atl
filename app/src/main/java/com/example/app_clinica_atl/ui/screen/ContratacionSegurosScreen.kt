package com.example.app_clinica_atl.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.ui.viewmodel.InsuranceViewModel
import kotlinx.coroutines.delay

data class BeneficiarioForm(
    var nombre: String = "",
    var apellido: String = "",
    var rut: String = "",
    var fechaNacimiento: String = ""
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ContratarSeguroScreen(
    seguro: SeguroDto,
    viewModel: InsuranceViewModel,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    var cantidad by remember { mutableStateOf(1) }

    val beneficiarios = remember {
        mutableStateListOf(BeneficiarioForm())
    }

    val metodosPago = listOf("Débito", "Crédito", "Transferencia")
    var metodoPago by remember { mutableStateOf("") }
    var pagoExpanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Contratar Seguro", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        Text(seguro.name, style = MaterialTheme.typography.titleMedium)
        Text(seguro.description)
        Text("Precio: $${seguro.price}")

        Spacer(Modifier.height(24.dp))

        // SEGURO FAMILIAR
        if (seguro.id == 7L) {
            Text("Cantidad de beneficiarios (1 a 5)")

            Slider(
                value = cantidad.toFloat(),
                onValueChange = {
                    val nuevo = it.toInt().coerceIn(1, 5)
                    cantidad = nuevo

                    if (beneficiarios.size < nuevo) {
                        repeat(nuevo - beneficiarios.size) {
                            beneficiarios.add(BeneficiarioForm())
                        }
                    } else if (beneficiarios.size > nuevo) {
                        repeat(beneficiarios.size - nuevo) {
                            beneficiarios.removeLast()
                        }
                    }
                },
                valueRange = 1f..5f,
                steps = 3
            )

            Text("Beneficiarios: $cantidad")
            Spacer(Modifier.height(16.dp))
        } else {
            if (beneficiarios.size > 1) {
                beneficiarios.clear()
                beneficiarios.add(BeneficiarioForm())
            }
        }

        // FORMULARIOS DINÁMICOS
        beneficiarios.forEachIndexed { index, ben ->
            Text("Beneficiario ${index + 1}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ben.nombre,
                onValueChange = { ben.nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ben.apellido,
                onValueChange = { ben.apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ben.rut,
                onValueChange = { ben.rut = it },
                label = { Text("RUT") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = ben.fechaNacimiento,
                onValueChange = { ben.fechaNacimiento = it },
                label = { Text("Fecha Nacimiento (dd-mm-yyyy)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }

        // MÉTODO DE PAGO
        Text("Método de Pago", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box {
            OutlinedTextField(
                value = metodoPago,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Seleccionar método") },
                trailingIcon = {
                    IconButton(onClick = { pagoExpanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            )

            DropdownMenu(
                expanded = pagoExpanded,
                onDismissRequest = { pagoExpanded = false }
            ) {
                metodosPago.forEach { m ->
                    DropdownMenuItem(
                        text = { Text(m) },
                        onClick = {
                            metodoPago = m
                            pagoExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.contratarSeguro(
                    seguroId = seguro.id,
                    beneficiarios = beneficiarios.toList(),
                    metodoPago = metodoPago
                )
            }
        ) {
            Text("Confirmar Contratación")
        }

        Spacer(Modifier.height(16.dp))

        uiState.errorMsg?.let {
            Text(it, color = Color.Red)
            LaunchedEffect(it) {
                delay(2000)
                viewModel.clearMessages()
            }
        }

        uiState.successMsg?.let {
            Text(it, color = Color(0xFF0A7F0A))
            LaunchedEffect(it) {
                delay(2000)
                viewModel.clearMessages()
                onBack()
            }
        }
    }
}
