package com.gmaldonado.taxflow_movil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Info
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorBottomSheet(
    userId: String,
    onDismiss: () -> Unit,
    onSimulationDone: (SimulationResultDto) -> Unit = {}
) {
    var amountText by remember { mutableStateOf("3500.00") }
    var applyRetention by remember { mutableStateOf(true) }
    var selectedType by remember { mutableStateOf(0) } // 0: Recibo por Honorarios (INGRESO), 1: Gasto Deducible (GASTO)
    var selectedCategory by remember { mutableStateOf("restaurantes") }

    var isSimulating by remember { mutableStateOf(false) }
    var simulationResult by remember { mutableStateOf<SimulationResultDto?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = TaxflowSurfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
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
                            text = "Simulador Tributario (AWS Lambda)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextPrimary
                        )
                        Text(
                            text = "Cálculo en la nube sin persistencia",
                            fontSize = 11.sp,
                            color = TaxflowTextMuted
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TaxflowTextMuted)
                }
            }

            // Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TaxflowSurfaceContainerLow, RoundedCornerShape(10.dp))
                    .padding(3.dp)
            ) {
                Button(
                    onClick = {
                        selectedType = 0
                        simulationResult = null
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
                        selectedType = 1
                        simulationResult = null
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == 1) TaxflowSurfaceContainerLowest else TaxflowSurfaceContainerLow,
                        contentColor = if (selectedType == 1) TaxflowTextPrimary else TaxflowTextMuted
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (selectedType == 1) 2.dp else 0.dp)
                ) {
                    Text("Gasto Deducible", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Amount Input
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
                        simulationResult = null
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

            // Type-specific options
            if (selectedType == 0) {
                // Retention switch for Income
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
                            simulationResult = null
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TaxflowEmerald
                        )
                    )
                }
            } else {
                // Category selector for Expense
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
                                    selectedCategory = catKey
                                    simulationResult = null
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
                                    selectedCategory = catKey
                                    simulationResult = null
                                },
                                label = { Text(catLabel, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Results Card (either from local estimation or AWS Lambda result)
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
                            text = if (simulationResult != null) "Cálculo confirmado por AWS Lambda" else "Proyección preliminar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (simulationResult != null) TaxflowEmeraldDark else TaxflowTextMuted
                        )
                        if (simulationResult != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = TaxflowEmeraldDark, modifier = Modifier.size(14.dp))
                                Text("v1 (no persistido)", fontSize = 10.sp, color = TaxflowEmeraldDark)
                            }
                        }
                    }

                    HorizontalDivider(color = TaxflowBorder, thickness = 1.dp)

                    val grossDouble = amountText.toDoubleOrNull() ?: 0.0
                    val grossCentimos = (grossDouble * 100).toLong()

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
                        // Estimated local preview before hitting AWS
                        val estRetention = if (selectedType == 0 && applyRetention && grossDouble > 1500) grossDouble * 0.08 else 0.0
                        val estNet = grossDouble - estRetention
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

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = TaxflowCoralRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Simulate Action Button (Calls AWS Lambda via API Gateway)
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
                                userId = userId,
                                montoCentimos = grossCentimos,
                                aplicarRetencion = applyRetention
                            )
                        } else {
                            TaxflowRepository.simulateExpense(
                                userId = userId,
                                montoCentimos = grossCentimos,
                                categoria = selectedCategory
                            )
                        }

                        isSimulating = false
                        result.onSuccess {
                            simulationResult = it
                            onSimulationDone(it)
                        }.onFailure {
                            errorMessage = "Error en simulación AWS: ${it.localizedMessage}"
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
                    Text("Calculando en AWS...", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (simulationResult != null) "Recalcular en AWS" else "Calcular Simulación en AWS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
