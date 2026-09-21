package com.gmaldonado.taxflow_movil.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.gmaldonado.taxflow_movil.ui.theme.*

@Composable
fun ProfileDialog(
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
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TaxflowTextMuted)
                    }
                }

                // Avatar
                Image(
                    painter = painterResource(id = R.drawable.avatar_carlos),
                    contentDescription = "Carlos Mendoza",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, TaxflowEmerald, CircleShape)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Carlos Mendoza Ramos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextPrimary
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
                        Text("Activo / Habido", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TaxflowEmeraldDark)
                    }
                    Text("· RUC 10458923014", fontSize = 12.sp, color = TaxflowTextMuted)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Info Rows
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileInfoRow("Régimen Fiscal", "Rentas de 4ta Categoría")
                        ProfileInfoRow("Profesión", "Diseñador & Consultor UX")
                        ProfileInfoRow("Dígito RUC", "Último dígito: 4")
                        ProfileInfoRow("Clave SOL", "Sincronizada y Cifrada 256-bit")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TaxflowCoralRed)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cerrar Sesión / Volver al Inicio", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TaxflowTextMuted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TaxflowTextPrimary)
    }
}

data class NotificationItem(
    val title: String,
    val description: String,
    val time: String,
    val isUrgent: Boolean = false
)

@Composable
fun NotificationsDialog(
    onDismiss: () -> Unit
) {
    val items = listOf(
        NotificationItem(
            title = "Próximo Vencimiento SUNAT",
            description = "Tu declaración jurada mensual vence el 18 de Septiembre (Dígito 4).",
            time = "Hace 2 horas",
            isUrgent = true
        ),
        NotificationItem(
            title = "Comprobante Validado",
            description = "El RHE E001-48 por S/ 4,500.00 fue registrado con éxito en SUNAT.",
            time = "Ayer",
            isUrgent = false
        ),
        NotificationItem(
            title = "Ahorro Tributario Detectado",
            description = "Nuevo consumo en Restaurante Central agregó +S/ 48.00 a tu bolsa deducible de 3 UIT.",
            time = "Hace 3 días",
            isUrgent = false
        )
    )

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
                    Text(
                        text = "Notificaciones Fiscales",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TaxflowTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items) { notif ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (notif.isUrgent) TaxflowAmberContainer.copy(alpha = 0.5f) else TaxflowSurfaceContainerLow
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
                                        text = notif.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (notif.isUrgent) TaxflowNavy else TaxflowTextPrimary
                                    )
                                    Text(
                                        text = notif.time,
                                        fontSize = 10.sp,
                                        color = TaxflowTextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.description,
                                    fontSize = 12.sp,
                                    color = TaxflowTextMuted,
                                    lineHeight = 16.sp
                                )
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
                    Text("Marcar todas como leídas", fontSize = 13.sp)
                }
            }
        }
    }
}
