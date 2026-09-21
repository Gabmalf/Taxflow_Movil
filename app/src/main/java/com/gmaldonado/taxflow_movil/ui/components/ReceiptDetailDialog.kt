package com.gmaldonado.taxflow_movil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gmaldonado.taxflow_movil.ui.theme.*

data class ReceiptData(
    val title: String,
    val seriesNumber: String,
    val date: String,
    val grossAmount: Double,
    val retentionAmount: Double,
    val netAmount: Double,
    val status: String = "Validado SUNAT",
    val type: String = "Recibo por Honorarios Electrónico"
)

@Composable
fun ReceiptDetailDialog(
    receipt: ReceiptData,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
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
                                text = "Comprobante Digital",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowEmeraldDark
                            )
                            Text(
                                text = receipt.seriesNumber,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowTextPrimary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TaxflowTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Client name
                Text(
                    text = "Cliente / Emisor",
                    fontSize = 11.sp,
                    color = TaxflowTextMuted
                )
                Text(
                    text = receipt.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TaxflowTextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Detail card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fecha de emisión", fontSize = 13.sp, color = TaxflowTextMuted)
                            Text(receipt.date, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TaxflowTextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Honorario Bruto", fontSize = 13.sp, color = TaxflowTextMuted)
                            Text(String.format("S/ %,.2f", receipt.grossAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Retención SUNAT (8%)", fontSize = 13.sp, color = TaxflowTextMuted)
                            Text(String.format("-S/ %,.2f", receipt.retentionAmount), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TaxflowCoralRed)
                        }
                        HorizontalDivider(color = TaxflowBorder, thickness = 1.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Neto a Percibir", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                            Text(String.format("S/ %,.2f", receipt.netAmount), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TaxflowEmerald)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Verification badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TaxflowEmeraldContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TaxflowEmeraldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Estado: ${receipt.status} con Clave SOL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TaxflowEmeraldDark
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartir o Descargar PDF", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
