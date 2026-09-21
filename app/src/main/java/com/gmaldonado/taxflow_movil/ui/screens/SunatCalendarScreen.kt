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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.ui.theme.*

data class SunatObligation(
    val title: String,
    val dueDate: String,
    val formCode: String,
    val statusText: String,
    val isUrgent: Boolean = false,
    val isCompleted: Boolean = false,
    val details: String
)

@Composable
fun SunatCalendarScreen() {
    val context = LocalContext.current

    val obligations = listOf(
        SunatObligation(
            title = "Declara Fácil 616 - Renta 4ta Mensual",
            dueDate = "Viernes 18 de Septiembre 2026",
            formCode = "Formulario Virtual 616",
            statusText = "Vence en 2 días",
            isUrgent = true,
            details = "Obligatorio si tus ingresos del mes superan S/ 3,755 y no cuentas con suspensión de retenciones."
        ),
        SunatObligation(
            title = "Declaración Jurada Anual 2025/2026",
            dueDate = "26 de Marzo 2027 (Dígito 4)",
            formCode = "Formulario Virtual 709",
            statusText = "Programado",
            details = "Regularización anual de rentas de trabajo de 4ta y 5ta categoría. Se deduce automáticamente las 7 UIT + hasta 3 UIT."
        ),
        SunatObligation(
            title = "Suspensión de Retenciones 4ta Categoría",
            dueDate = "Vigente hasta el 31 Dic 2026",
            formCode = "Formulario Virtual 1609",
            statusText = "Aprobado / Activo",
            isCompleted = true,
            details = "Constancia N° 2600192841 autorizada para no sufrir retenciones del 8% al no proyectar superar S/ 45,063 anuales."
        ),
        SunatObligation(
            title = "Declara Fácil 616 - Agosto 2026",
            dueDate = "17 de Agosto 2026",
            formCode = "Formulario Virtual 616",
            statusText = "Presentado",
            isCompleted = true,
            details = "Declaración presentada y pagada a tiempo. N° de orden: 981240182."
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
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "CRONOGRAMA OFICIAL SUNAT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowEmeraldDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Último dígito: 4",
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
                    text = "Vencimientos según Resolución de Superintendencia para personas naturales.",
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
                            text = "¡Alerta de Vencimiento Cercano!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowNavy
                        )
                        Text(
                            text = "El periodo tributario 2026-08 para RUC dígito 4 vence este viernes 18.",
                            fontSize = 12.sp,
                            color = TaxflowTextMuted
                        )
                    }
                }
            }
        }

        // Obligations List
        items(obligations) { ob ->
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
                                text = ob.formCode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowTextMuted
                            )
                            Text(
                                text = ob.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowTextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    when {
                                        ob.isUrgent -> TaxflowAmberContainer
                                        ob.isCompleted -> TaxflowEmeraldContainer
                                        else -> TaxflowSurfaceContainerLow
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = ob.statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    ob.isUrgent -> TaxflowAmber
                                    ob.isCompleted -> TaxflowEmeraldDark
                                    else -> TaxflowTextMuted
                                }
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(16.dp))
                        Text(
                            text = ob.dueDate,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TaxflowTextPrimary
                        )
                    }

                    Text(
                        text = ob.details,
                        fontSize = 12.sp,
                        color = TaxflowTextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Link to SUNAT portal
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://e-menu.sunat.gob.pe"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ingresar a SUNAT Operaciones en Línea", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
