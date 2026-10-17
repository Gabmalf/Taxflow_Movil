package com.gmaldonado.taxflow_movil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.data.formatters.Formatters
import com.gmaldonado.taxflow_movil.data.remote.models.SimulationResultDto
import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import com.gmaldonado.taxflow_movil.ui.theme.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorBottomSheet(
    onDismiss: () -> Unit,
    onSimulationDone: (SimulationResultDto) -> Unit = {},
    onOperationRegistered: (String) -> Unit = {}
) {
    var amountText by remember { mutableStateOf("3500.00") }
    var applyRetention by remember { mutableStateOf(true) }
    var selectedType by remember { mutableStateOf(0) } // 0: Recibo por Honorarios (INGRESO), 1: Gasto Deducible (GASTO)
    var selectedCategory by remember { mutableStateOf("restaurantes") }
    var conceptText by remember { mutableStateOf("") }

    var isSimulating by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    var simulationResult by remember { mutableStateOf<SimulationResultDto?>(null) }
    var registeredSuccessMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentIdempotencyKey by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Regla de invalidación: ante cambios de parámetros, invalidar el cálculo previo
    fun invalidateCalculation() {
        simulationResult = null
        registeredSuccessMessage = null
        errorMessage = null
        currentIdempotencyKey = null
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = TaxflowSurfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header con textos actualizados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = TaxflowEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Simulador Tributario",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextPrimary
                        )
                        Text(
                            text = "Calcula y registra tus operaciones",
                            fontSize = 11.sp,
                            color = TaxflowTextMuted
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TaxflowTextMuted)
                }
            }

            // Selector de Tipo de Operación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TaxflowSurfaceContainerLow, RoundedCornerShape(10.dp))
                    .padding(3.dp)
            ) {
                Button(
                    onClick = {
                        if (selectedType != 0) {
                            selectedType = 0
                            invalidateCalculation()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == 0) TaxflowSurfaceContainerLowest else TaxflowSurfaceContainerLow,
                        contentColor = if (selectedType == 0) TaxflowTextPrimary else TaxflowTextMuted
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (selectedType == 0) 2.dp else 0.dp)
                ) {
                    Text("Ingreso (RHE)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        if (selectedType != 1) {
                            selectedType = 1
                            invalidateCalculation()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == 1) TaxflowSurfaceContainerLowest else TaxflowSurfaceContainerLow,
                        contentColor = if (selectedType == 1) TaxflowTextPrimary else TaxflowTextMuted
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (selectedType == 1) 2.dp else 0.dp)
                ) {
                    Text("Gasto deducible", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Input de Monto Principal
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (selectedType == 0) "Monto Bruto del Recibo (S/)" else "Total del Consumo / Gasto (S/)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxflowTextPrimary
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        invalidateCalculation()
                    },
                    prefix = { Text("S/ ", fontWeight = FontWeight.Bold, color = TaxflowTextPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TaxflowSurfaceContainerLowest,
                        unfocusedContainerColor = TaxflowSurfaceContainerLowest,
                        focusedBorderColor = TaxflowEmerald,
                        unfocusedBorderColor = TaxflowBorder
                    )
                )
            }

            // Input opcional de Concepto / Cliente / Proveedor para enriquecer el registro
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (selectedType == 0) "Cliente o Concepto (opcional)" else "Proveedor o Establecimiento (opcional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxflowTextPrimary
                )
                OutlinedTextField(
                    value = conceptText,
                    onValueChange = { conceptText = it },
                    placeholder = {
                        Text(
                            text = if (selectedType == 0) "Ej. Empresa Consultora S.A.C." else "Ej. Restaurante El Sol",
                            fontSize = 12.sp,
                            color = TaxflowTextMuted
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TaxflowSurfaceContainerLowest,
                        unfocusedContainerColor = TaxflowSurfaceContainerLowest,
                        focusedBorderColor = TaxflowEmerald,
                        unfocusedBorderColor = TaxflowBorder
                    )
                )
            }

            // Opciones específicas según el tipo
            if (selectedType == 0) {
                // Switch de Retención del 8%
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TaxflowSurfaceContainerLow, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Retención del 8% de Impuesto a la Renta", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TaxflowTextPrimary)
                        Text("Aplica si supera S/ 1,500 y no tiene suspensión", fontSize = 11.sp, color = TaxflowTextMuted)
                    }
                    Switch(
                        checked = applyRetention,
                        onCheckedChange = {
                            applyRetention = it
                            invalidateCalculation()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TaxflowEmerald
                        )
                    )
                }
            } else {
                // Selector de Categoría de Gasto Deducible
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Categoría de Gasto Deducible:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TaxflowTextPrimary)
                    val categories = listOf(
                        "restaurantes" to "Restaurantes (15%)",
                        "servicios" to "Servicios Médicos (30%)",
                        "alojamiento" to "Hoteles/Alojamiento (15%)",
                        "essalud" to "EsSalud T. Hogar (100%)"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(2).forEach { (catKey, catLabel) ->
                            FilterChip(
                                selected = selectedCategory == catKey,
                                onClick = {
                                    if (selectedCategory != catKey) {
                                        selectedCategory = catKey
                                        invalidateCalculation()
                                    }
                                },
                                label = { Text(catLabel, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.drop(2).forEach { (catKey, catLabel) ->
                            FilterChip(
                                selected = selectedCategory == catKey,
                                onClick = {
                                    if (selectedCategory != catKey) {
                                        selectedCategory = catKey
                                        invalidateCalculation()
                                    }
                                },
                                label = { Text(catLabel, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Tarjeta de Resultados (Proyección preliminar vs Cálculo confirmado)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (simulationResult != null) TaxflowEmeraldContainer.copy(alpha = 0.4f) else TaxflowSurfaceContainerLow
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (simulationResult != null) "Cálculo confirmado" else "Proyección preliminar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (simulationResult != null) TaxflowEmeraldDark else TaxflowTextMuted
                        )
                        if (simulationResult != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = TaxflowEmeraldDark, modifier = Modifier.size(14.dp))
                                Text("Pendiente de registro", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TaxflowEmeraldDark)
                            }
                        }
                    }

                    HorizontalDivider(color = TaxflowBorder, thickness = 1.dp)

                    val grossDouble = amountText.toDoubleOrNull() ?: 0.0

                    if (simulationResult != null) {
                        val res = simulationResult!!
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (res.tipo == "INGRESO") "Honorario Bruto" else "Gasto Total", fontSize = 13.sp, color = TaxflowTextPrimary)
                            Text(Formatters.formatCentimosAsPen(res.montoCentimos), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TaxflowTextPrimary)
                        }

                        if (res.tipo == "INGRESO") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Retención SUNAT (8%)", fontSize = 13.sp, color = TaxflowCoralRed)
                                Text("-${Formatters.formatCentimosAsPen(res.retencionCentimos)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TaxflowCoralRed)
                            }
                            HorizontalDivider(color = TaxflowBorder, thickness = 1.dp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Monto Neto a Cobrar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                                Text(Formatters.formatCentimosAsPen(res.netoCentimos), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TaxflowEmerald)
                            }
                        } else {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Deducción computable (3 UIT)", fontSize = 13.sp, color = TaxflowEmeraldDark)
                                Text("+${Formatters.formatCentimosAsPen(res.deducibleCentimos)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TaxflowEmeraldDark)
                            }
                            HorizontalDivider(color = TaxflowBorder, thickness = 1.dp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Gasto Neto Pagado", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                                Text(Formatters.formatCentimosAsPen(res.netoCentimos), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TaxflowEmerald)
                            }
                        }
                    } else {
                        // Proyección estimada local previa al cálculo en la nube
                        val estRetention = if (selectedType == 0 && applyRetention && grossDouble > 1500) grossDouble * 0.08 else 0.0
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (selectedType == 0) "Honorario Bruto" else "Gasto Total", fontSize = 13.sp, color = TaxflowTextPrimary)
                            Text(Formatters.formatPen(grossDouble), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TaxflowTextPrimary)
                        }
                        if (selectedType == 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Retención estimada", fontSize = 13.sp, color = TaxflowCoralRed)
                                Text("-${Formatters.formatPen(estRetention)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TaxflowCoralRed)
                            }
                        } else {
                            val rate = when (selectedCategory) {
                                "servicios" -> 0.30
                                "essalud" -> 1.00
                                else -> 0.15
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Deducción estimada", fontSize = 13.sp, color = TaxflowEmeraldDark)
                                Text("+${Formatters.formatPen(grossDouble * rate)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TaxflowEmeraldDark)
                            }
                        }
                    }
                }
            }

            // Banner de Éxito al Registrar
            if (registeredSuccessMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxflowEmeraldContainer.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TaxflowEmeraldDark, modifier = Modifier.size(24.dp))
                        Column {
                            Text("¡Operación registrada con éxito!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TaxflowEmeraldDark)
                            Text(registeredSuccessMessage ?: "", fontSize = 11.sp, color = TaxflowTextPrimary)
                            Text("Los datos se sincronizaron con DynamoDB y el dashboard se ha actualizado.", fontSize = 10.sp, color = TaxflowTextMuted)
                        }
                    }
                }
            }

            // Banner de Error
            if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxflowCoralRed.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, TaxflowCoralRed.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = TaxflowCoralRed, modifier = Modifier.size(20.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = TaxflowCoralRed,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Botón de Acción Principal (Flujo en 2 Etapas: 1. Calcular -> 2. Registrar)
            if (registeredSuccessMessage != null) {
                // Estado: Registrado -> Permitir cerrar o registrar otra operación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { invalidateCalculation() },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Registrar otra", fontSize = 13.sp)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
                    ) {
                        Text("Cerrar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else if (simulationResult == null) {
                // ETAPA 1: Calcular simulación
                Button(
                    onClick = {
                        val grossDouble = amountText.toDoubleOrNull() ?: 0.0
                        val grossCentimos = (grossDouble * 100).toLong()
                        if (grossCentimos <= 0) {
                            errorMessage = "Ingresa un monto válido mayor a 0"
                            return@Button
                        }
                        isSimulating = true
                        errorMessage = null

                        scope.launch {
                            val result = if (selectedType == 0) {
                                TaxflowRepository.simulateIncome(
                                    montoCentimos = grossCentimos,
                                    aplicarRetencion = applyRetention
                                )
                            } else {
                                TaxflowRepository.simulateExpense(
                                    montoCentimos = grossCentimos,
                                    categoria = selectedCategory
                                )
                            }

                            isSimulating = false
                            result.onSuccess {
                                simulationResult = it
                                currentIdempotencyKey = UUID.randomUUID().toString()
                                onSimulationDone(it)
                            }.onFailure {
                                errorMessage = "Error al calcular simulación: ${it.localizedMessage}"
                            }
                        }
                    },
                    enabled = !isSimulating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
                ) {
                    if (isSimulating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculando simulación...", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calcular simulación", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                // ETAPA 2: Registrar operación (Ingreso o Gasto según pestaña activa)
                val registerButtonText = if (selectedType == 0) "Registrar ingreso" else "Registrar gasto"
                val registeringButtonText = if (selectedType == 0) "Registrando ingreso..." else "Registrando gasto..."

                Button(
                    onClick = {
                        val currentSim = simulationResult
                        if (currentSim == null) {
                            errorMessage = "No existe un cálculo vigente para registrar"
                            return@Button
                        }
                        isRegistering = true
                        errorMessage = null

                        // Preservar clave de idempotencia entre reintentos
                        val idempotencyKey = currentIdempotencyKey ?: UUID.randomUUID().toString().also {
                            currentIdempotencyKey = it
                        }

                        scope.launch {
                            val registrationResult = if (selectedType == 0) {
                                val clientName = conceptText.ifBlank { "Servicios Profesionales Demo" }
                                TaxflowRepository.registerIncome(
                                    montoBrutoCentimos = currentSim.montoCentimos,
                                    aplicarRetencion = applyRetention,
                                    cliente = clientName,
                                    fecha = "2026-09-20",
                                    idempotencyKey = idempotencyKey
                                )
                            } else {
                                val providerName = conceptText.ifBlank { "Gasto Deducible Demo" }
                                TaxflowRepository.registerExpense(
                                    montoCentimos = currentSim.montoCentimos,
                                    categoria = selectedCategory,
                                    proveedor = providerName,
                                    fecha = "2026-09-20",
                                    idempotencyKey = idempotencyKey
                                )
                            }

                            isRegistering = false
                            registrationResult.onSuccess {
                                val successText = if (selectedType == 0) {
                                    "Recibo emitido con éxito por ${Formatters.formatCentimosAsPen(currentSim.montoCentimos)}"
                                } else {
                                    "Gasto deducible registrado por ${Formatters.formatCentimosAsPen(currentSim.montoCentimos)}"
                                }
                                registeredSuccessMessage = successText
                                onOperationRegistered(successText)
                            }.onFailure { err ->
                                if (err is HttpException && err.code() == 404) {
                                    errorMessage = "El endpoint de persistencia aún no está disponible en AWS (HTTP 404: Pendiente de despliegue por Agente AWS). Tu cálculo simulado permanece intacto para reintentar."
                                } else {
                                    errorMessage = "Error al registrar en AWS: ${err.localizedMessage ?: err.message}"
                                }
                            }
                        }
                    },
                    enabled = !isRegistering && !isSimulating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaxflowEmerald)
                ) {
                    if (isRegistering) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(registeringButtonText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(registerButtonText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
