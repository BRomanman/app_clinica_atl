package com.example.app_clinica_atl.ui.screen.patient

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app_clinica_atl.data.remote.dto.SeguroDto
import com.example.app_clinica_atl.ui.viewmodel.patient.InsuranceViewModel
import com.example.app_clinica_atl.domain.validation.validateDateDdMmYyyy
import com.example.app_clinica_atl.domain.validation.validateEmail
import com.example.app_clinica_atl.domain.validation.validatePersonName
import com.example.app_clinica_atl.domain.validation.validateRequired
import com.example.app_clinica_atl.domain.validation.validateRut
import com.example.app_clinica_atl.domain.validation.validateChileanPhoneNumber
import kotlinx.coroutines.delay
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalContext
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.app_clinica_atl.R
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

data class BeneficiarioForm(
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",
    val fechaNacimiento: String = ""
)

private data class BeneficiarioErrors(
    val nombreError: String? = null,
    val apellidoError: String? = null,
    val rutError: String? = null,
    val fechaError: String? = null
)
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)

@Composable
fun ContratarSeguroScreen(
    seguro: SeguroDto,
    viewModel: InsuranceViewModel,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    var cantidad by remember { mutableStateOf(1) }

    val beneficiarios = remember {
        mutableStateListOf(BeneficiarioForm())
    }
    val beneficiarioErrors = remember {
        mutableStateListOf(BeneficiarioErrors())
    }
    val rutFields = remember { mutableStateListOf(TextFieldValue("")) }
    val fechaFields = remember { mutableStateListOf(TextFieldValue("")) }

    val metodosPago = listOf("Débito", "Crédito", "Transferencia")
    var metodoPago by remember { mutableStateOf("") }
    var pagoExpanded by remember { mutableStateOf(false) }
    var metodoPagoError by remember { mutableStateOf<String?>(null) }
    var validationMessage by remember { mutableStateOf<String?>(null) }
    var correoContacto by remember { mutableStateOf("") }
    var telefonoContacto by remember { mutableStateOf("") }
    var correoError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }

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

        Text("Datos de contacto", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = correoContacto,
            onValueChange = {
                correoContacto = it
                correoError = validateEmail(it)
                validationMessage = null
            },
            label = { Text("Correo de contacto") },
            modifier = Modifier.fillMaxWidth(),
            isError = correoError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        correoError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = telefonoContacto,
            onValueChange = {
                telefonoContacto = it
                telefonoError = validateChileanPhoneNumber(it)
                validationMessage = null

            },
            label = { Text("Teléfono de contacto (+569xxxxxxx)") },
            modifier = Modifier.fillMaxWidth(),
            isError = telefonoError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        telefonoError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
        }

        Spacer(Modifier.height(24.dp))

        // SEGURO FAMILIAR
        if (seguro.id == 7L) {
            Text("Cantidad de beneficiarios (1 a 5)")

            Slider(
                value = cantidad.toFloat(),
                onValueChange = { raw ->
                    val nuevo = raw.toInt().coerceIn(1, 5)
                    cantidad = nuevo

                    val nuevaLista = beneficiarios.toMutableList()
                    val nuevosErrores = beneficiarioErrors.toMutableList()
                    val nuevosRut = rutFields.toMutableList()
                    val nuevasFechas = fechaFields.toMutableList()

                    if (nuevaLista.size < nuevo) {
                        val diff = nuevo - nuevaLista.size
                        repeat(diff) {
                            nuevaLista.add(BeneficiarioForm())
                            nuevosErrores.add(BeneficiarioErrors())
                            nuevosRut.add(TextFieldValue(""))
                            nuevasFechas.add(TextFieldValue(""))
                        }
                    } else if (nuevaLista.size > nuevo) {
                        val diff = nuevaLista.size - nuevo
                        repeat(diff) {
                            nuevaLista.removeLast()
                            nuevosErrores.removeLast()
                            nuevosRut.removeLast()
                            nuevasFechas.removeLast()
                        }
                    }

                    beneficiarios.clear()
                    beneficiarios.addAll(nuevaLista)
                    beneficiarioErrors.clear()
                    beneficiarioErrors.addAll(nuevosErrores)
                    rutFields.clear(); rutFields.addAll(nuevosRut)
                    fechaFields.clear(); fechaFields.addAll(nuevasFechas)
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
                rutFields.clear()
                rutFields.add(TextFieldValue(""))
                fechaFields.clear()
                fechaFields.add(TextFieldValue(""))
                beneficiarioErrors.clear()
                beneficiarioErrors.add(BeneficiarioErrors())
            }
        }


        beneficiarios.forEachIndexed { index, ben ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Beneficiario ${index + 1}", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = ben.nombre,
                        onValueChange = { newValue ->
                            val sanitized = sanitizeNameInput(newValue)
                            beneficiarios[index] = ben.copy(nombre = sanitized)
                            beneficiarioErrors[index] = beneficiarioErrors[index].copy(
                                nombreError = validatePersonName(sanitized, "Nombre")
                            )
                            validationMessage = null
                        },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = beneficiarioErrors[index].nombreError != null,
                        singleLine = true
                    )
                    beneficiarioErrors[index].nombreError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedTextField(
                        value = ben.apellido,
                        onValueChange = { newValue ->
                            val sanitized = sanitizeNameInput(newValue)
                            beneficiarios[index] = ben.copy(apellido = sanitized)
                            beneficiarioErrors[index] = beneficiarioErrors[index].copy(
                                apellidoError = validatePersonName(sanitized, "Apellido")
                            )
                            validationMessage = null
                        },
                        label = { Text("Apellido") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = beneficiarioErrors[index].apellidoError != null,
                        singleLine = true
                    )
                    beneficiarioErrors[index].apellidoError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedTextField(
                        value = rutFields.getOrElse(index) { TextFieldValue("") },
                        onValueChange = { newValue ->
                            val formatted = formatRutInput(newValue.text)
                            val tfv = TextFieldValue(formatted, selection = TextRange(formatted.length))
                            if (index < rutFields.size) rutFields[index] = tfv else rutFields.add(tfv)
                            beneficiarios[index] = ben.copy(rut = formatted)
                            beneficiarioErrors[index] = beneficiarioErrors[index].copy(
                                rutError = validateRut(formatted)
                            )
                            validationMessage = null
                        },
                        label = { Text("RUT") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = beneficiarioErrors[index].rutError != null,
                        singleLine = true
                    )
                    beneficiarioErrors[index].rutError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedTextField(
                        value = fechaFields.getOrElse(index) { TextFieldValue("") },
                        onValueChange = { newValue ->
                            val formatted = formatDateInput(newValue.text)
                            val tfv = TextFieldValue(formatted, selection = TextRange(formatted.length))
                            if (index < fechaFields.size) fechaFields[index] = tfv else fechaFields.add(tfv)
                            beneficiarios[index] = ben.copy(fechaNacimiento = formatted)
                            beneficiarioErrors[index] = beneficiarioErrors[index].copy(
                                fechaError = validateDateDdMmYyyy(formatted, "Fecha de nacimiento")
                            )
                            validationMessage = null
                        },
                        label = { Text("Fecha Nacimiento (dd-mm-yyyy)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = beneficiarioErrors[index].fechaError != null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    beneficiarioErrors[index].fechaError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }


        Text("Método de Pago", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = pagoExpanded,
            onExpandedChange = { pagoExpanded = !pagoExpanded }
        ) {
            OutlinedTextField(
                value = metodoPago,
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                label = { Text("Seleccionar método") },
                isError = metodoPagoError != null,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = pagoExpanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            metodoPagoError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            ExposedDropdownMenu(
                expanded = pagoExpanded,
                onDismissRequest = { pagoExpanded = false }
            ) {
                metodosPago.forEach { m ->
                    DropdownMenuItem(
                        text = { Text(m) },
                        onClick = {
                            metodoPago = m
                            metodoPagoError = null
                            validationMessage = null
                            pagoExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                Toast.makeText(context, "Enviando contrato...", Toast.LENGTH_SHORT).show()
                var hasError = false
                beneficiarios.forEachIndexed { idx, ben ->
                val nombreErr = validatePersonName(ben.nombre, "Nombre")
                val apellidoErr = validatePersonName(ben.apellido, "Apellido")
                val rutErr = validateRut(ben.rut)
                val fechaErr = validateDateDdMmYyyy(ben.fechaNacimiento, "Fecha de nacimiento")
                beneficiarioErrors[idx] = BeneficiarioErrors(
                    nombreError = nombreErr,
                    apellidoError = apellidoErr,
                    rutError = rutErr,
                    fechaError = fechaErr
                    )
                    if (listOf(nombreErr, apellidoErr, rutErr, fechaErr).any { it != null }) {
                        hasError = true
                    }
                }
                metodoPagoError = validateRequired(metodoPago, "Método de pago")
                correoError = validateEmail(correoContacto)
                telefonoError = validateChileanPhoneNumber(telefonoContacto)

                if (hasError || metodoPagoError != null || correoError != null || telefonoError != null) {
                    validationMessage = "Corrige los campos requeridos antes de continuar."
                    return@Button
                }
                validationMessage = null
                viewModel.contratarSeguro(
                    seguroId = seguro.id,
                    beneficiarios = beneficiarios.toList(),
                    metodoPago = metodoPago,
                    correoContacto = correoContacto,
                    telefonoContacto = telefonoContacto
                )
            }
        ) {
            Text("Confirmar Contratación")
        }

        Spacer(Modifier.height(16.dp))

        validationMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
        }

        uiState.errorMsg?.let { msg ->
            LaunchedEffect(msg) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        }

        uiState.successMsg?.let { msg ->
            Text(msg, color = Color(0xFF0A7F0A), style = MaterialTheme.typography.bodyMedium)
            LaunchedEffect(msg) {
                Toast.makeText(context, "Contrato confirmado", Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
                onBack()
            }
        }
    }
}

private fun sanitizeNameInput(input: String): String =
    input.filter { it.isLetter() || it.isWhitespace() }

private fun formatRutInput(input: String): String {
    val cleaned = input.replace(Regex("[^0-9kK]"), "").uppercase().take(9)
    if (cleaned.isEmpty()) return ""
    if (cleaned.length == 1) return cleaned
    val body = cleaned.dropLast(1)
    val dv = cleaned.last()
    val withDots = body.reversed().chunked(3).joinToString(".").reversed()
    return "$withDots-$dv"
}

private fun formatDateInput(input: String): String {
    val digits = input.filter { it.isDigit() }.take(8)
    val sb = StringBuilder()
    digits.forEachIndexed { index, c ->
        sb.append(c)
        if ((index == 1 && digits.length > 2) || (index == 3 && digits.length > 4)) {
            sb.append('-')
        }
    }
    return sb.toString()
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
private fun showInsuranceNotification(context: Context, message: String) {
    val channelId = "insurance_channel"
    val manager = NotificationManagerCompat.from(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Seguros",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
    }
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logo)
        .setContentTitle("Contrato de seguro")
        .setContentText(message)
        .setAutoCancel(true)
        .build()
    manager.notify(2003, notification)
}
