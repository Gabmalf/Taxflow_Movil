package com.gmaldonado.taxflow_movil.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.ui.components.ReceiptData
import com.gmaldonado.taxflow_movil.ui.theme.*

data class MonthGroup(
    val monthName: String,
    val countLabel: String,
    val receipts: List<ReceiptData>
)

@Composable
fun IngresosScreen(
    onReceiptClick: (ReceiptData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Este Mes") }
    val context = LocalContext.current

    val initialData = remember {
        listOf(
            MonthGroup(
                monthName = "Septiembre 2026",
                countLabel = "2 comprobantes",
                receipts = listOf(
                    ReceiptData(
                        title = "TechCorp Solutions S.A.C.",
                        seriesNumber = "RHE E001-48",
                        date = "12 Sep 2026",
                        grossAmount = 4500.0,
                        retentionAmount = 360.0,
                        netAmount = 4140.0
                    ),
                    ReceiptData(
                        title = "Agencia Creativa Lima EIRL",
                        seriesNumber = "RHE E001-47",
                        date = "05 Sep 2026",
                        grossAmount = 1200.0,
                        retentionAmount = 0.0,
                        netAmount = 1200.0
                    )
                )
            ),
            MonthGroup(
                monthName = "Agosto 2026",
                countLabel = "2 comprobantes",
                receipts = listOf(
                    ReceiptData(
                        title = "Fintech Andes Perú",
                        seriesNumber = "RHE E001-46",
                        date = "28 Ago 2026",
                        grossAmount = 5800.0,
                        retentionAmount = 464.0,
                        netAmount = 5336.0
                    ),
                    ReceiptData(
                        title = "Universidad Digital",
                        seriesNumber = "RHE E001-45",
                        date = "15 Ago 2026",
                        grossAmount = 3000.0,
                        retentionAmount = 240.0,
                        netAmount = 2760.0
                    )
                )
            )
        )
    }

    // Filter by selected period and search query
    val filteredGroups = initialData.mapNotNull { group ->
        if (selectedFilter == "Este Mes" && !group.monthName.contains("Septiembre")) {
            null
        } else {
            val matching = group.receipts.filter { receipt ->
                searchQuery.isEmpty() ||
                        receipt.title.contains(searchQuery, ignoreCase = true) ||
                        receipt.seriesNumber.contains(searchQuery, ignoreCase = true)
            }
            if (matching.isNotEmpty()) {
                group.copy(countLabel = "${matching.size} comprobantes", receipts = matching)
            } else null
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
                placeholder = { Text("Buscar por cliente o N° de recibo...", fontSize = 14.sp) },
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
            val filters = listOf("Este Mes", "Año 2026", "Últimos 3 meses", "Personalizado")
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

        // 3. Summary Total Card
        item {
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
                    Text(
                        text = "Total Facturado 4ta Categoría",
                        fontSize = 12.sp,
                        color = TaxflowTextMuted
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "S/ 48,250.00",
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
                            text = "Neto percibido: S/ 44,390.00",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TaxflowEmeraldDark
                        )
                        Text(
                            text = "Retención 8%: S/ 3,860.00",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TaxflowTextMuted
                        )
                    }
                }
            }
        }

        // 4. Section Title
        item {
            Text(
                text = "COMPROBANTES EMITIDOS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TaxflowTextMuted,
                letterSpacing = 0.5.sp
            )
        }

        // 5. Grouped Receipts List
        if (filteredGroups.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TaxflowTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Sin comprobantes",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextPrimary
                        )
                        Text(
                            text = "No se encontraron recibos que coincidan con la búsqueda o filtro.",
                            fontSize = 12.sp,
                            color = TaxflowTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            filteredGroups.forEach { group ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = group.monthName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextPrimary
                        )
                        Text(
                            text = group.countLabel,
                            fontSize = 11.sp,
                            color = TaxflowTextMuted
                        )
                    }
                }

                items(group.receipts) { receipt ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReceiptClick(receipt) },
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = receipt.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TaxflowTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${receipt.seriesNumber} • ${receipt.date}",
                                    fontSize = 12.sp,
                                    color = TaxflowTextMuted
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format("S/ %,.2f", receipt.grossAmount),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaxflowTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = String.format("Neto: S/ %,.2f", receipt.netAmount),
                                    fontSize = 11.sp,
                                    color = TaxflowTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. SUNAT Clave SOL quick helper link
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://e-menu.sunat.gob.pe"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TaxflowTextPrimary),
                    border = BorderStroke(1.dp, TaxflowBorder)
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Emitir en SUNAT Clave SOL", fontSize = 13.sp)
                }
            }
        }
    }
}
