package com.gmaldonado.taxflow_movil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.ui.components.ReceiptData
import com.gmaldonado.taxflow_movil.ui.theme.*

data class ActivityItem(
    val title: String,
    val subtitle: String,
    val amount: String,
    val tag: String,
    val isPositive: Boolean,
    val isExpense: Boolean = false,
    val fullReceipt: ReceiptData? = null
)

@Composable
fun DashboardScreen(
    onNavigateToIngresos: () -> Unit,
    onNavigateToGastos: () -> Unit,
    onNavigateToSunat: () -> Unit,
    onReceiptClick: (ReceiptData) -> Unit
) {
    val activities = listOf(
        ActivityItem(
            title = "Diseño UI/UX - TechCorp SAC",
            subtitle = "14 Sep · RHE E001-48",
            amount = "S/ 4,500.00",
            tag = "Pagado",
            isPositive = true,
            fullReceipt = ReceiptData(
                title = "TechCorp Solutions S.A.C.",
                seriesNumber = "RHE E001-48",
                date = "14 Sep 2026",
                grossAmount = 4500.0,
                retentionAmount = 360.0,
                netAmount = 4140.0
            )
        ),
        ActivityItem(
            title = "Restaurante La Mar",
            subtitle = "12 Sep · Gasto 15% deducible",
            amount = "-S/ 180.00",
            tag = "+S/ 27.00 ded.",
            isPositive = false,
            isExpense = true,
            fullReceipt = ReceiptData(
                title = "Restaurante La Mar SAC",
                seriesNumber = "B002-8912",
                date = "12 Sep 2026",
                grossAmount = 180.0,
                retentionAmount = 0.0,
                netAmount = 180.0,
                status = "Deducción 15% Aplicada",
                type = "Boleta Electrónica con DNI"
            )
        ),
        ActivityItem(
            title = "Agencia Creativa Lima EIRL",
            subtitle = "05 Sep · RHE E001-47",
            amount = "S/ 1,200.00",
            tag = "Pagado",
            isPositive = true,
            fullReceipt = ReceiptData(
                title = "Agencia Creativa Lima EIRL",
                seriesNumber = "RHE E001-47",
                date = "05 Sep 2026",
                grossAmount = 1200.0,
                retentionAmount = 0.0,
                netAmount = 1200.0
            )
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxflowBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // 1. User greeting
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "4TA CATEGORÍA · RUC 10458923014",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowTextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Hola, Carlos",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowTextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(TaxflowEmeraldContainer, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "SETIEMBRE 2026",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowEmeraldDark
                    )
                }
            }
        }

        // 2. SUNAT Expiration Alert Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSunat() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TaxflowBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(TaxflowAmber, CircleShape)
                        )
                        Text(
                            text = "Vencimiento SUNAT: 18 de Sep",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Declara Fácil 616",
                            fontSize = 12.sp,
                            color = TaxflowTextMuted
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TaxflowTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 3. Hero Impuesto Estimado Card
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column {
                        Text(
                            text = "IMPUESTO ESTIMADO ANUAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "S/ ",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TaxflowTextMuted
                                )
                                Text(
                                    text = "2,140",
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaxflowTextPrimary
                                )
                                Text(
                                    text = ".00",
                                    fontSize = 16.sp,
                                    color = TaxflowTextMuted
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(TaxflowEmeraldContainer, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Savings, contentDescription = null, tint = TaxflowEmeraldDark, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "Ahorro: S/ 1,200",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TaxflowEmeraldDark
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = TaxflowBorder, thickness = 1.dp)

                    // 2 Column stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToIngresos() }
                        ) {
                            Text("Ingresos del Mes", fontSize = 12.sp, color = TaxflowTextMuted)
                            Text("S/ 8,450.00", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(12.dp))
                                Text("+12% · 4 RHE", fontSize = 11.sp, color = TaxflowEmerald, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToGastos() }
                        ) {
                            Text("Gastos Deducibles", fontSize = 12.sp, color = TaxflowTextMuted)
                            Text("S/ 1,820.50", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                            Text("Restaurantes, Hoteles", fontSize = 11.sp, color = TaxflowTextMuted)
                        }
                    }
                }
            }
        }

        // 4. Deducción 7 UIT Progress Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Deducción 7 UIT (S/ 36,050)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowTextPrimary
                            )
                        }
                        Text(
                            text = "68%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowEmerald
                        )
                    }

                    LinearProgressIndicator(
                        progress = { 0.68f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = TaxflowEmerald,
                        trackColor = TaxflowSurfaceContainerLow
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("S/ 24,514 consumido", fontSize = 11.sp, color = TaxflowTextMuted)
                        Text("Disponible: S/ 11,536", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TaxflowTextMuted)
                    }
                }
            }
        }

        // 5. Recent Activity
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Actividad Reciente",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextPrimary
                )
                Text(
                    text = "Ver historial",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TaxflowEmerald,
                    modifier = Modifier.clickable { onNavigateToIngresos() }
                )
            }
        }

        items(activities) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        item.fullReceipt?.let { onReceiptClick(it) }
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (item.isExpense) TaxflowAmberContainer else TaxflowEmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.isExpense) Icons.Default.Restaurant else Icons.Default.Description,
                                contentDescription = null,
                                tint = if (item.isExpense) TaxflowAmber else TaxflowEmeraldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TaxflowTextPrimary
                            )
                            Text(
                                text = item.subtitle,
                                fontSize = 12.sp,
                                color = TaxflowTextMuted
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = item.amount,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextPrimary
                        )
                        Text(
                            text = item.tag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (item.isPositive) TaxflowEmeraldDark else TaxflowEmerald
                        )
                    }
                }
            }
        }
    }
}
