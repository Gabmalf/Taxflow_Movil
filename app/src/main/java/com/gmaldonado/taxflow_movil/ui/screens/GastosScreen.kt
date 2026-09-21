package com.gmaldonado.taxflow_movil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.ui.components.ReceiptData
import com.gmaldonado.taxflow_movil.ui.theme.*

data class ExpenseItem(
    val title: String,
    val dateAndTotal: String,
    val deductibleAmount: String,
    val percentageLabel: String,
    val category: String,
    val icon: ImageVector,
    val fullReceipt: ReceiptData
)

@Composable
fun GastosScreen(
    onReceiptClick: (ReceiptData) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("all") }

    val expenses = remember {
        listOf(
            ExpenseItem(
                title = "Restaurante Central",
                dateAndTotal = "14 Sep 2026 • Total S/ 320.00",
                deductibleAmount = "+ S/ 48.00",
                percentageLabel = "15% deducido",
                category = "restaurantes",
                icon = Icons.Default.Restaurant,
                fullReceipt = ReceiptData(
                    title = "Restaurante Central SAC",
                    seriesNumber = "B001-44910",
                    date = "14 Sep 2026",
                    grossAmount = 320.0,
                    retentionAmount = 0.0,
                    netAmount = 320.0,
                    status = "Validado SUNAT (15%)",
                    type = "Boleta Electrónica con DNI"
                )
            ),
            ExpenseItem(
                title = "Dr. Mario Benavides",
                dateAndTotal = "10 Sep 2026 • Total S/ 300.00",
                deductibleAmount = "+ S/ 90.00",
                percentageLabel = "30% deducido",
                category = "servicios",
                icon = Icons.Default.MedicalServices,
                fullReceipt = ReceiptData(
                    title = "Dr. Mario Benavides (Médico Cirujano)",
                    seriesNumber = "RHE E001-12",
                    date = "10 Sep 2026",
                    grossAmount = 300.0,
                    retentionAmount = 0.0,
                    netAmount = 300.0,
                    status = "Validado SUNAT (30%)",
                    type = "Recibo por Honorarios Profesional de Salud"
                )
            ),
            ExpenseItem(
                title = "Hotel Valle Sagrado",
                dateAndTotal = "22 Ago 2026 • Total S/ 850.00",
                deductibleAmount = "+ S/ 127.50",
                percentageLabel = "15% deducido",
                category = "alojamiento",
                icon = Icons.Default.Hotel,
                fullReceipt = ReceiptData(
                    title = "Inversiones Turísticas Cusco SAC",
                    seriesNumber = "F003-12093",
                    date = "22 Ago 2026",
                    grossAmount = 850.0,
                    retentionAmount = 0.0,
                    netAmount = 850.0,
                    status = "Validado SUNAT (15%)",
                    type = "Factura de Alojamiento y Hospedaje"
                )
            ),
            ExpenseItem(
                title = "Formulario 1676 EsSalud",
                dateAndTotal = "15 Ago 2026 • Total S/ 178.00",
                deductibleAmount = "+ S/ 178.00",
                percentageLabel = "100% deducido",
                category = "essalud",
                icon = Icons.Default.VolunteerActivism,
                fullReceipt = ReceiptData(
                    title = "SUNAT / EsSalud Trabajador del Hogar",
                    seriesNumber = "F1676-9481",
                    date = "15 Ago 2026",
                    grossAmount = 178.0,
                    retentionAmount = 0.0,
                    netAmount = 178.0,
                    status = "Aporte 100% Deducible",
                    type = "Guía Pago Fácil Trabajador del Hogar"
                )
            )
        )
    }

    val filteredExpenses = expenses.filter {
        selectedCategory == "all" || it.category == selectedCategory
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxflowBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // 1. Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SUNAT 2026 • 4TA CATEGORÍA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowEmeraldDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Tope Ley: 3 UIT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxflowTextMuted
                    )
                }
                Text(
                    text = "Gastos Deducibles",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextPrimary
                )
                Text(
                    text = "Beneficio tributario para reducir el Impuesto a la Renta anual.",
                    fontSize = 13.sp,
                    color = TaxflowTextMuted
                )
            }
        }

        // 2. Central Progress Meter Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total deducible acumulado",
                                fontSize = 12.sp,
                                color = TaxflowTextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "S/ 1,820.50",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaxflowTextPrimary
                                )
                                Text(
                                    text = " / S/ 15,450.00",
                                    fontSize = 13.sp,
                                    color = TaxflowTextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(TaxflowEmeraldContainer, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "11.8% del tope",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowEmeraldDark
                            )
                        }
                    }

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { 0.118f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = TaxflowEmerald,
                        trackColor = TaxflowSurfaceContainerLow
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("S/ 0", fontSize = 11.sp, color = TaxflowTextMuted)
                        Text("Disponible: S/ 13,629.50", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TaxflowEmeraldDark)
                        Text("Tope: S/ 15,450", fontSize = 11.sp, color = TaxflowTextMuted)
                    }

                    HorizontalDivider(color = TaxflowBorder, thickness = 1.dp)

                    // Deduction Tags
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DeductionTag("🍽️ Restaurantes 15%")
                        DeductionTag("🩺 Médicos 30%")
                        DeductionTag("🛡️ EsSalud 100%")
                        DeductionTag("🏨 Hoteles 15%")
                    }
                }
            }
        }

        // 3. Category Filter Pills
        item {
            val categories = listOf(
                Pair("all", "Todos (18)"),
                Pair("restaurantes", "Restaurantes"),
                Pair("servicios", "Servicios Médicos"),
                Pair("alojamiento", "Alojamiento"),
                Pair("essalud", "EsSalud")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) TaxflowNavy else TaxflowSurfaceContainerLowest)
                            .clickable { selectedCategory = catKey }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = catLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TaxflowTextMuted
                        )
                    }
                }
            }
        }

        // 4. Section title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMPROBANTES VALIDADOS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextMuted,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Sincronizado vía SUNAT",
                    fontSize = 11.sp,
                    color = TaxflowTextMuted
                )
            }
        }

        // 5. Expense Items
        items(filteredExpenses) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReceiptClick(item.fullReceipt) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TaxflowSurfaceContainerLow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = TaxflowTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TaxflowTextPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TaxflowEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = item.dateAndTotal,
                                fontSize = 12.sp,
                                color = TaxflowTextMuted
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = item.deductibleAmount,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowEmeraldDark
                        )
                        Text(
                            text = item.percentageLabel,
                            fontSize = 11.sp,
                            color = TaxflowTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeductionTag(text: String) {
    Box(
        modifier = Modifier
            .background(TaxflowSurfaceContainerLow, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = TaxflowTextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}
