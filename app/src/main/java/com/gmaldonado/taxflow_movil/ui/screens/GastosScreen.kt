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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import com.gmaldonado.taxflow_movil.data.formatters.Formatters
import com.gmaldonado.taxflow_movil.data.remote.models.ExpenseDto
import com.gmaldonado.taxflow_movil.data.remote.models.ExpenseListResponse
import com.gmaldonado.taxflow_movil.data.remote.models.ProfileDto
import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import com.gmaldonado.taxflow_movil.ui.components.ReceiptData
import com.gmaldonado.taxflow_movil.ui.theme.*
import kotlin.math.min

@Composable
fun GastosScreen(
    currentUser: ProfileDto?,
    onReceiptClick: (ReceiptData) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("all") }

    var expenseResponse by remember { mutableStateOf<ExpenseListResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    val dataRefreshVersion by TaxflowRepository.dataRefreshTrigger.collectAsState()

    LaunchedEffect(currentUser?.id, selectedCategory, refreshTrigger, dataRefreshVersion) {
        isLoading = true
        errorMessage = null

        val catParam = if (selectedCategory == "all") null else selectedCategory
        val token = TaxflowRepository.getSessionToken()
        val result = TaxflowRepository.getExpenses(categoria = catParam)

        if (token == TaxflowRepository.getSessionToken()) {
            result.onSuccess {
                expenseResponse = it
                isLoading = false
            }.onFailure {
                errorMessage = "Error al consultar gastos en AWS: ${it.localizedMessage}"
                isLoading = false
            }
        }
    }

    val tope3UitCentimos = 1650000L // S/ 16,500.00 en 2026

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
                        text = "SUNAT 2026 • 4TA CATEGORÍA (AWS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowEmeraldDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Tope Ley: 3 UIT (S/ 16,500)",
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
                    text = "Comprobantes electrónicos para reducir el Impuesto a la Renta anual.",
                    fontSize = 13.sp,
                    color = TaxflowTextMuted
                )
            }
        }

        // 2. Central Progress Meter Hero Card
        item {
            val deducibleCentimos = expenseResponse?.resumen?.deducibleCentimos ?: 0L
            val progress = min(1.0f, deducibleCentimos.toFloat() / tope3UitCentimos.toFloat())
            val percent = (progress * 100).toInt()

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
                                text = "Deducción acumulada (Filtro Actual)",
                                fontSize = 12.sp,
                                color = TaxflowTextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = Formatters.formatCentimosAsPen(deducibleCentimos),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaxflowTextPrimary
                                )
                                Text(
                                    text = " / S/ 16,500.00",
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
                                text = "$percent% del tope",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowEmeraldDark
                            )
                        }
                    }

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { progress },
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
                        val totalGasto = expenseResponse?.resumen?.totalCentimos ?: 0L
                        Text("Total consumido: ${Formatters.formatCentimosAsPen(totalGasto)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TaxflowTextPrimary)
                        Text("Tope 3 UIT", fontSize = 11.sp, color = TaxflowTextMuted)
                    }
                }
            }
        }

        // 3. Category Filter Chips
        item {
            val categories = listOf(
                "all" to "Todos",
                "restaurantes" to "Restaurantes (15%)",
                "servicios" to "Servicios Médicos (30%)",
                "alojamiento" to "Hoteles (15%)",
                "essalud" to "EsSalud (100%)"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (catId, catLabel) ->
                    val isSelected = selectedCategory == catId
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) TaxflowNavy else TaxflowSurfaceContainerLowest)
                            .clickable { selectedCategory = catId }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = catLabel,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TaxflowTextMuted
                        )
                    }
                }
            }
        }

        // 4. Loading indicator or Error or Expenses List
        if (isLoading && expenseResponse == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TaxflowEmerald)
                }
            }
        } else if (errorMessage != null && expenseResponse == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(errorMessage ?: "", color = TaxflowCoralRed, fontSize = 13.sp)
                        Button(onClick = { refreshTrigger++ }, colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        } else {
            val items = expenseResponse?.items ?: emptyList()
            if (items.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest)
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No hay gastos registrados en esta categoría", fontSize = 13.sp, color = TaxflowTextMuted)
                        }
                    }
                }
            } else {
                items(items) { expense ->
                    val icon: ImageVector = when (expense.categoria) {
                        "restaurantes" -> Icons.Default.Restaurant
                        "servicios" -> Icons.Default.MedicalServices
                        "alojamiento" -> Icons.Default.Hotel
                        "essalud" -> Icons.Default.VolunteerActivism
                        else -> Icons.AutoMirrored.Filled.ReceiptLong
                    }
                    val catLabel = when (expense.categoria) {
                        "restaurantes" -> "Restaurantes & Bares"
                        "servicios" -> "Servicios Médicos / Salud"
                        "alojamiento" -> "Hoteles & Hospedaje"
                        "essalud" -> "Aporte Trabajador Hogar"
                        else -> expense.categoria
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val receipt = ReceiptData(
                                    title = expense.proveedor,
                                    seriesNumber = "${expense.serie}-${expense.numero}",
                                    date = Formatters.formatIsoDate(expense.fecha),
                                    grossAmount = Formatters.centimosToPen(expense.montoCentimos),
                                    netAmount = Formatters.centimosToPen(expense.montoCentimos),
                                    isExpense = true,
                                    deductibleAmount = Formatters.centimosToPen(expense.deducibleCentimos),
                                    categoryLabel = catLabel,
                                    status = expense.estado,
                                    type = "Comprobante de Gasto Deducible"
                                )
                                onReceiptClick(receipt)
                            },
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
                                        .clip(CircleShape)
                                        .background(TaxflowAmberContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = TaxflowAmberDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = expense.proveedor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TaxflowTextPrimary
                                    )
                                    Text(
                                        text = "${Formatters.formatIsoDate(expense.fecha)} • Total ${Formatters.formatCentimosAsPen(expense.montoCentimos)}",
                                        fontSize = 11.sp,
                                        color = TaxflowTextMuted
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+ ${Formatters.formatCentimosAsPen(expense.deducibleCentimos)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaxflowEmeraldDark
                                )
                                Text(
                                    text = "${expense.porcentajeDeduciblePb / 100}% deducido",
                                    fontSize = 11.sp,
                                    color = TaxflowTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
