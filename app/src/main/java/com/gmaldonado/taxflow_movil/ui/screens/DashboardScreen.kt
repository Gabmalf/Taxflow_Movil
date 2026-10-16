package com.gmaldonado.taxflow_movil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.data.formatters.Formatters
import com.gmaldonado.taxflow_movil.data.remote.models.DashboardResponse
import com.gmaldonado.taxflow_movil.data.remote.models.ProfileDto
import com.gmaldonado.taxflow_movil.data.remote.models.RecentActivityDto
import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import com.gmaldonado.taxflow_movil.ui.components.ReceiptData
import com.gmaldonado.taxflow_movil.ui.theme.*
import kotlin.math.min

@Composable
fun DashboardScreen(
    currentUser: ProfileDto?,
    onNavigateToIngresos: () -> Unit,
    onNavigateToGastos: () -> Unit,
    onNavigateToSunat: () -> Unit,
    onReceiptClick: (ReceiptData) -> Unit
) {
    var dashboardData by remember { mutableStateOf<DashboardResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    val dataRefreshVersion by TaxflowRepository.dataRefreshTrigger.collectAsState()

    LaunchedEffect(currentUser?.id, refreshTrigger, dataRefreshVersion) {
        isLoading = true
        errorMessage = null
        val token = TaxflowRepository.getSessionToken()
        val result = TaxflowRepository.getDashboard(anio = 2026, mes = 9)

        // Only commit result if session token hasn't changed (discard stale responses)
        if (token == TaxflowRepository.getSessionToken()) {
            result.onSuccess {
                dashboardData = it
                isLoading = false
            }.onFailure {
                errorMessage = "Error al conectar con AWS: ${it.localizedMessage}"
                isLoading = false
            }
        }
    }

    if (isLoading && dashboardData == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TaxflowBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(color = TaxflowEmerald)
                Text("Cargando métricas desde AWS...", fontSize = 14.sp, color = TaxflowTextMuted)
            }
        }
        return
    }

    if (errorMessage != null && dashboardData == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TaxflowBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.CloudOff, contentDescription = null, tint = TaxflowCoralRed, modifier = Modifier.size(40.dp))
                    Text("Error de Conexión AWS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                    Text(errorMessage ?: "", fontSize = 13.sp, color = TaxflowTextMuted)
                    Button(
                        onClick = { refreshTrigger++ },
                        colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
        return
    }

    val data = dashboardData ?: return

    val progress7Uit = if (data.limite7UitCentimos > 0) {
        min(1.0f, data.ingresosAnioCentimos.toFloat() / data.limite7UitCentimos.toFloat())
    } else 0f

    val progress3Uit = if (data.limite3UitCentimos > 0) {
        min(1.0f, data.deducibleAnioCentimos.toFloat() / data.limite3UitCentimos.toFloat())
    } else 0f

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
                        text = "4TA CATEGORÍA · DÍGITO RUC: ${currentUser?.ultimoDigitoRuc ?: "—"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowTextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Hola, ${currentUser?.nombre?.split(" ")?.firstOrNull() ?: "Carlos"}",
                        fontSize = 24.sp,
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
                            text = "Cronograma SUNAT · Dígito ${currentUser?.ultimoDigitoRuc ?: "—"}",
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
                            text = "Ver vencimientos",
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

        // 3. Hero Card: Ingresos Acumulados 2026 (Datos AWS)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INGRESOS ACUMULADOS 2026",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowTextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "AWS DynamoDB",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowEmeraldDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Formatters.formatCentimosAsPen(data.ingresosAnioCentimos),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowTextPrimary
                            )

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
                                        text = "Retención: ${Formatters.formatCentimosAsPen(data.retencionesAnioCentimos)}",
                                        fontSize = 11.sp,
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
                            Text("Ingresos Mes (Setiembre)", fontSize = 12.sp, color = TaxflowTextMuted)
                            Text(Formatters.formatCentimosAsPen(data.ingresosMesCentimos), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                            Text("Ver recibos emitidos →", fontSize = 11.sp, color = TaxflowEmerald, fontWeight = FontWeight.SemiBold)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToGastos() }
                        ) {
                            Text("Gastos Deducibles Año", fontSize = 12.sp, color = TaxflowTextMuted)
                            Text(Formatters.formatCentimosAsPen(data.gastosAnioCentimos), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                            Text("Deducción: ${Formatters.formatCentimosAsPen(data.deducibleAnioCentimos)}", fontSize = 11.sp, color = TaxflowTextMuted)
                        }
                    }
                }
            }
        }

        // 4. Deducción 7 UIT Progress Card (Escudo Tributario)
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
                            Icon(Icons.Default.Shield, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(16.dp))
                            Text("Escudo 7 UIT (Exoneración Legal)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                        }
                        Text("${(progress7Uit * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TaxflowEmeraldDark)
                    }

                    LinearProgressIndicator(
                        progress = { progress7Uit },
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
                        Text(
                            text = "Usado: ${Formatters.formatCentimosAsPen(data.ingresosAnioCentimos)}",
                            fontSize = 11.sp,
                            color = TaxflowTextMuted
                        )
                        Text(
                            text = "Límite: ${Formatters.formatCentimosAsPen(data.limite7UitCentimos)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TaxflowTextPrimary
                        )
                    }
                }
            }
        }

        // 5. Deducción 3 UIT Adicional Card
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
                            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = TaxflowAmber, modifier = Modifier.size(16.dp))
                            Text("Gastos Deducibles (Hasta 3 UIT)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                        }
                        Text("${(progress3Uit * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TaxflowAmberDark)
                    }

                    LinearProgressIndicator(
                        progress = { progress3Uit },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = TaxflowAmber,
                        trackColor = TaxflowSurfaceContainerLow
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Deducción: ${Formatters.formatCentimosAsPen(data.deducibleAnioCentimos)}",
                            fontSize = 11.sp,
                            color = TaxflowTextMuted
                        )
                        Text(
                            text = "Tope: ${Formatters.formatCentimosAsPen(data.limite3UitCentimos)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TaxflowTextPrimary
                        )
                    }
                }
            }
        }

        // 6. Actividad Reciente Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVIDAD RECIENTE (AWS)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextMuted,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Ver todo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TaxflowEmerald,
                    modifier = Modifier.clickable { onNavigateToIngresos() }
                )
            }
        }

        // 7. Actividad Reciente Items
        if (data.actividadReciente.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No hay actividad reciente registrada en este período", fontSize = 13.sp, color = TaxflowTextMuted)
                    }
                }
            }
        } else {
            items(data.actividadReciente) { activity ->
                val isExpense = activity.tipo == "GASTO"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val receipt = ReceiptData(
                                title = activity.titulo,
                                seriesNumber = activity.id,
                                date = Formatters.formatIsoDate(activity.fecha),
                                grossAmount = Formatters.centimosToPen(activity.montoCentimos),
                                netAmount = Formatters.centimosToPen(activity.montoCentimos),
                                isExpense = isExpense,
                                status = if (isExpense) "Gasto Registrado" else "RHE Emitido",
                                type = if (isExpense) "Comprobante de Gasto" else "Recibo por Honorarios Electrónico"
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isExpense) TaxflowAmberContainer else TaxflowEmeraldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isExpense) Icons.AutoMirrored.Filled.ReceiptLong else Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = if (isExpense) TaxflowAmberDark else TaxflowEmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = activity.titulo,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TaxflowTextPrimary
                                )
                                Text(
                                    text = "${Formatters.formatIsoDate(activity.fecha)} · ${activity.id}",
                                    fontSize = 11.sp,
                                    color = TaxflowTextMuted
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = (if (isExpense) "-" else "+") + Formatters.formatCentimosAsPen(activity.montoCentimos),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpense) TaxflowTextPrimary else TaxflowEmerald
                            )
                            Text(
                                text = if (isExpense) "Gasto" else "Ingreso",
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
