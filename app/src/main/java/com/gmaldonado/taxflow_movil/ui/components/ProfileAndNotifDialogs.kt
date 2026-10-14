package com.gmaldonado.taxflow_movil.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gmaldonado.taxflow_movil.R
import com.gmaldonado.taxflow_movil.data.formatters.Formatters
import com.gmaldonado.taxflow_movil.data.remote.models.NotificationDto
import com.gmaldonado.taxflow_movil.data.remote.models.ProfileDto
import com.gmaldonado.taxflow_movil.data.repository.DemoCredential
import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import com.gmaldonado.taxflow_movil.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileDialog(
    currentUser: ProfileDto?,
    onSwitchDemoAccount: (DemoCredential) -> Unit,
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mi Perfil Taxflow",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TaxflowTextMuted)
                    }
                }

                // Avatar
                Image(
                    painter = painterResource(id = R.drawable.avatar_carlos),
                    contentDescription = currentUser?.nombre ?: "Usuario",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(2.dp, TaxflowEmerald, CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentUser?.nombre ?: "Usuario",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextPrimary
                )

                Text(
                    text = currentUser?.profesion ?: "Profesional Independiente",
                    fontSize = 13.sp,
                    color = TaxflowEmeraldDark,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = currentUser?.email ?: "",
                    fontSize = 12.sp,
                    color = TaxflowTextMuted
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(TaxflowEmeraldContainer, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "ID: ${currentUser?.id ?: "—"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowEmeraldDark
                        )
                    }
                    Text(
                        text = if (currentUser?.ultimoDigitoRuc != null)
                            "· Dígito RUC: ${currentUser.ultimoDigitoRuc}"
                        else
                            "· RUC: No registrado",
                        fontSize = 12.sp,
                        color = TaxflowTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Cuentas Demo para alternar rápido en pruebas
                Text(
                    text = "CAMBIAR A CUENTA DEMO:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextMuted,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaxflowRepository.demoAccounts.forEach { acc ->
                        val isSelected = acc.id == currentUser?.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSwitchDemoAccount(acc) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) TaxflowEmeraldContainer.copy(alpha = 0.6f) else TaxflowSurfaceContainerLow
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, TaxflowEmerald) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${acc.nombre} (${acc.id})",
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = TaxflowTextPrimary
                                    )
                                    Text(
                                        text = "${acc.profesion} • RUC: ...${acc.ultimoDigitoRuc}",
                                        fontSize = 11.sp,
                                        color = TaxflowTextMuted
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Seleccionado",
                                        tint = TaxflowEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout / Salir
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TaxflowCoralRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TaxflowCoralRed.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cerrar Sesión", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun NotificationsDialog(
    onDismiss: () -> Unit
) {
    var notifications by remember { mutableStateOf<List<NotificationDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null
        val result = TaxflowRepository.getNotifications()
        result.onSuccess {
            notifications = it.items
            isLoading = false
        }.onFailure {
            errorMessage = "No se pudieron cargar las notificaciones: ${it.localizedMessage}"
            isLoading = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Avisos Fiscales (AWS)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextPrimary
                        )
                        Text(
                            text = "Bandeja de Notificaciones",
                            fontSize = 11.sp,
                            color = TaxflowTextMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TaxflowTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TaxflowEmerald)
                    }
                } else if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = TaxflowCoralRed,
                            fontSize = 13.sp
                        )
                    }
                } else if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes avisos pendientes",
                            color = TaxflowTextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 340.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notifications) { notif ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (notif.esUrgente) TaxflowAmberContainer.copy(alpha = 0.5f) else TaxflowSurfaceContainerLow
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = notif.titulo,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (notif.esUrgente) TaxflowNavy else TaxflowTextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = Formatters.formatIsoDateTime(notif.createdAt),
                                            fontSize = 10.sp,
                                            color = TaxflowTextMuted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notif.mensaje,
                                        fontSize = 12.sp,
                                        color = TaxflowTextMuted,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
                ) {
                    Text("Cerrar", fontSize = 13.sp)
                }
            }
        }
    }
}
