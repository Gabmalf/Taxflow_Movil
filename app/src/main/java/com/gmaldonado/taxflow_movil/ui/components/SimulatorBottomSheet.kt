package com.gmaldonado.taxflow_movil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorBottomSheet(
    onDismiss: () -> Unit,
    onSimulateSuccess: (String, Double, Double, Double) -> Unit
) {
    var amountText by remember { mutableStateOf("3500") }
    var clientName by remember { mutableStateOf("Corporación Global SAC") }
    var applyRetention by remember { mutableStateOf(true) }
    var selectedType by remember { mutableStateOf(0) } // 0: Recibo por Honorarios, 1: Gasto Deducible

    val gross = amountText.toDoubleOrNull() ?: 0.0
    val retention = if (applyRetention && gross > 1500) gross * 0.08 else 0.0
    val net = gross - retention

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
                    Text(
                        text = "Simulador Tributario 4ta",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowTextPrimary
                    )
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
                    onClick = { selectedType = 0 },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == 0) TaxflowSurfaceContainerLowest else TaxflowSurfaceContainerLow,
                        contentColor = if (selectedType == 0) TaxflowTextPrimary else TaxflowTextMuted
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = if (selectedType == 0) 2.dp else 0.dp)
                ) {
                    Text("Emitir RHE", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { selectedType = 1 },
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

            // Client or Provider Name Field
            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text(if (selectedType == 0) "Cliente / Razón Social" else "Establecimiento / Proveedor") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TaxflowNavy,
                    unfocusedBorderColor = TaxflowBorder
                )
            )

            // Amount Input Field
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text("Monto del comprobante (S/)") },
                prefix = { Text("S/ ", fontWeight = FontWeight.Bold, color = TaxflowTextPrimary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TaxflowNavy,
                    unfocusedBorderColor = TaxflowBorder
                )
            )

            // Retention Toggle (for RHE)
            if (selectedType == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Retención del 8% de SUNAT", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TaxflowTextPrimary)
                        Text("Aplica para montos mayores a S/ 1,500", fontSize = 11.sp, color = TaxflowTextMuted)
                    }
                    Switch(
                        checked = applyRetention,
                        onCheckedChange = { applyRetention = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TaxflowEmerald,
                            checkedTrackColor = TaxflowEmeraldContainer
                        )
                    )
                }
            }

            // Real-time calculation card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLow)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Cálculo Fiscal en Tiempo Real",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowTextMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Honorario Bruto", fontSize = 13.sp, color = TaxflowTextPrimary)
                        Text(String.format("S/ %,.2f", gross), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TaxflowTextPrimary)
                    }

                    if (selectedType == 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Retención SUNAT (8%)", fontSize = 13.sp, color = TaxflowCoralRed)
                            Text(String.format("-S/ %,.2f", retention), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TaxflowCoralRed)
                        }
                    } else {
                        val deducibleEst = gross * 0.15
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Deducción computable (15%)", fontSize = 13.sp, color = TaxflowEmeraldDark)
                            Text(String.format("+S/ %,.2f", deducibleEst), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TaxflowEmeraldDark)
                        }
                    }

                    HorizontalDivider(color = TaxflowBorder, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (selectedType == 0) "Monto Neto a Cobrar" else "Total Pagado", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                        Text(String.format("S/ %,.2f", net), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TaxflowEmerald)
                    }
                }
            }

            // Register Action Button
            Button(
                onClick = {
                    onSimulateSuccess(clientName, gross, retention, net)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar en el Prototipo", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
