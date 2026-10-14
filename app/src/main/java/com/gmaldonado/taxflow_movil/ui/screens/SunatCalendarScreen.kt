package com.gmaldonado.taxflow_movil.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.gmaldonado.taxflow_movil.data.formatters.Formatters
import com.gmaldonado.taxflow_movil.data.remote.models.CalendarItemDto
import com.gmaldonado.taxflow_movil.data.remote.models.ProfileDto
import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import com.gmaldonado.taxflow_movil.ui.theme.*

@Composable
fun SunatCalendarScreen(
    currentUser: ProfileDto?
) {
    val context = LocalContext.current

    var calendarItems by remember { mutableStateOf<List<CalendarItemDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(currentUser?.id, refreshTrigger) {
        isLoading = true
        errorMessage = null
        val token = TaxflowRepository.getSessionToken()
        val result = TaxflowRepository.getCalendar(anio = 2026)

        if (token == TaxflowRepository.getSessionToken()) {
            result.onSuccess {
                calendarItems = it.items
                isLoading = false
            }.onFailure {
                errorMessage = "Error al obtener cronograma desde AWS: ${it.localizedMessage}"
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
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CRONOGRAMA SUNAT (AWS DYNAMODB)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowEmeraldDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Último dígito: ${currentUser?.ultimoDigitoRuc ?: "—"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowNavy
                    )
                }
                Text(
                    text = "Calendario Tributario",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextPrimary
                )
                Text(
                    text = "Fechas límite oficiales para rentas de 4ta categoría.",
                    fontSize = 13.sp,
                    color = TaxflowTextMuted
                )
            }
        }

        // Info Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowAmberContainer.copy(alpha = 0.4f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TaxflowAmber.copy(alpha = 0.5f)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TaxflowAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = "Cronograma Fiscal Asignado",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowNavy
                        )
                        Text(
                            text = "Obligaciones programadas para ${currentUser?.nombre ?: "Usuario Demo"} (RUC termina en ${currentUser?.ultimoDigitoRuc ?: "—"}).",
                            fontSize = 12.sp,
                            color = TaxflowTextMuted
                        )
                    }
                }
            }
        }

        if (isLoading && calendarItems.isEmpty()) {
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
        } else if (errorMessage != null && calendarItems.isEmpty()) {
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
        } else if (calendarItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No hay obligaciones programadas para este usuario", fontSize = 13.sp, color = TaxflowTextMuted)
                    }
                }
            }
        } else {
            items(calendarItems) { item ->
                val isPresented = item.estado == "PRESENTADO"
                val isActive = item.estado == "ACTIVO"
                val isPending = item.estado == "PENDIENTE"

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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.formulario,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaxflowTextMuted
                                )
                                Text(
                                    text = item.titulo,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaxflowTextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        when {
                                            isPresented -> TaxflowEmeraldContainer
                                            isActive -> TaxflowEmeraldContainer.copy(alpha = 0.5f)
                                            else -> TaxflowAmberContainer
                                        },
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = item.estado,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isPresented -> TaxflowEmeraldDark
                                        isActive -> TaxflowEmeraldDark
                                        else -> TaxflowAmberDark
                                    }
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TaxflowTextMuted, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Vencimiento: ${Formatters.formatIsoDate(item.fecha)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isPending) TaxflowAmberDark else TaxflowTextPrimary
                            )
                        }

                        Text(
                            text = item.detalle,
                            fontSize = 12.sp,
                            color = TaxflowTextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // External Link to SUNAT
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sunat.gob.pe"))
                        context.startActivity(browserIntent)
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TaxflowBorder))
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Consultar en Portal SUNAT Oficial",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TaxflowTextPrimary
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TaxflowTextMuted, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
