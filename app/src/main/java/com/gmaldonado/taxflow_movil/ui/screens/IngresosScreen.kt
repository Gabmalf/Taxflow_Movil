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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.data.formatters.Formatters
import com.gmaldonado.taxflow_movil.data.remote.models.IncomeDto
import com.gmaldonado.taxflow_movil.data.remote.models.IncomeListResponse
import com.gmaldonado.taxflow_movil.data.remote.models.ProfileDto
import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import com.gmaldonado.taxflow_movil.ui.components.ReceiptData
import com.gmaldonado.taxflow_movil.ui.theme.*

@Composable
fun IngresosScreen(
    currentUser: ProfileDto?,
    onReceiptClick: (ReceiptData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    var incomeResponse by remember { mutableStateOf<IncomeListResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    val dataRefreshVersion by TaxflowRepository.dataRefreshTrigger.collectAsState()

    LaunchedEffect(currentUser?.id, selectedFilter, searchQuery, refreshTrigger, dataRefreshVersion) {
        isLoading = true
        errorMessage = null

        val desde = if (selectedFilter == "Este Mes") "2026-09-01" else null
        val hasta = if (selectedFilter == "Este Mes") "2026-09-30" else null
        val estado = when (selectedFilter) {
            "Pagados" -> "PAGADO"
            "Pendientes" -> "PENDIENTE"
            else -> null
        }
        val q = if (searchQuery.isNotBlank()) searchQuery.trim() else null

        val token = TaxflowRepository.getSessionToken()
        val result = TaxflowRepository.getIncomes(
            desde = desde,
            hasta = hasta,
            q = q,
            estado = estado
        )

        if (token == TaxflowRepository.getSessionToken()) {
            result.onSuccess {
                incomeResponse = it
                isLoading = false
            }.onFailure {
                errorMessage = "Error al consultar ingresos en AWS: ${it.localizedMessage}"
                isLoading = false
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxflowBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // 1. Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por cliente o serie-número...", fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TaxflowTextMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = TaxflowTextMuted)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TaxflowSurfaceContainerLowest,
                    unfocusedContainerColor = TaxflowSurfaceContainerLowest,
                    focusedBorderColor = TaxflowNavy,
                    unfocusedBorderColor = TaxflowBorder
                ),
                singleLine = true
            )
        }

        // 2. Filter Pills
        item {
            val filters = listOf("Todos", "Este Mes", "Pagados", "Pendientes")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) TaxflowNavy else TaxflowSurfaceContainerLowest)
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TaxflowTextMuted
                        )
                    }
                }
            }
        }

        // 3. Resumen Card (Live from AWS)
        item {
            val resumen = incomeResponse?.resumen
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Honorarios Brutos (Filtro Actual)",
                            fontSize = 12.sp,
                            color = TaxflowTextMuted
                        )
                        Box(
                            modifier = Modifier
                                .background(TaxflowEmeraldContainer, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${incomeResponse?.total ?: 0} RHE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowEmeraldDark
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (resumen != null) Formatters.formatCentimosAsPen(resumen.brutoCentimos) else "S/ 0.00",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PEN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextMuted
                        )
                    }

                    HorizontalDivider(color = TaxflowBorder, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Neto: " + if (resumen != null) Formatters.formatCentimosAsPen(resumen.netoCentimos) else "S/ 0.00",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TaxflowEmeraldDark
                        )
                        Text(
                            text = "Retención 8%: " + if (resumen != null) Formatters.formatCentimosAsPen(resumen.retencionCentimos) else "S/ 0.00",
                            fontSize = 12.sp,
                            color = TaxflowCoralRed
                        )
                    }
                }
            }
        }

        // 4. Loading indicator or Error or Receipts List
        if (isLoading && incomeResponse == null) {
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
        } else if (errorMessage != null && incomeResponse == null) {
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
            val items = incomeResponse?.items ?: emptyList()
            if (items.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest)
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No se encontraron recibos de ingresos con los filtros seleccionados", fontSize = 13.sp, color = TaxflowTextMuted)
                        }
                    }
                }
            } else {
                items(items) { income ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val receipt = ReceiptData(
                                    title = income.cliente,
                                    seriesNumber = "${income.serie}-${income.numero}",
                                    date = Formatters.formatIsoDate(income.fecha),
                                    grossAmount = Formatters.centimosToPen(income.montoBrutoCentimos),
                                    retentionAmount = Formatters.centimosToPen(income.retencionCentimos),
                                    netAmount = Formatters.centimosToPen(income.montoNetoCentimos),
                                    status = income.estado,
                                    type = "Recibo por Honorarios Electrónico"
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
                                        .background(TaxflowEmeraldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Receipt,
                                        contentDescription = null,
                                        tint = TaxflowEmeraldDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = income.cliente,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TaxflowTextPrimary
                                    )
                                    Text(
                                        text = "${income.serie}-${income.numero} • ${Formatters.formatIsoDate(income.fecha)}",
                                        fontSize = 11.sp,
                                        color = TaxflowTextMuted
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = Formatters.formatCentimosAsPen(income.montoBrutoCentimos),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaxflowTextPrimary
                                )
                                val isPaid = income.estado == "PAGADO"
                                val isPending = income.estado == "PENDIENTE"
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isPaid) TaxflowEmeraldContainer else if (isPending) TaxflowAmberContainer else TaxflowCoralRed.copy(alpha = 0.1f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = income.estado,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPaid) TaxflowEmeraldDark else if (isPending) TaxflowAmberDark else TaxflowCoralRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
