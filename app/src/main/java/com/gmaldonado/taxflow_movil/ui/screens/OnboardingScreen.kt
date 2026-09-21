package com.gmaldonado.taxflow_movil.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.ui.theme.*

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxflowBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(TaxflowSurfaceContainerLow, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(TaxflowEmerald, CircleShape)
                )
                Text(
                    text = "TAXFLOW",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextPrimary,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .background(TaxflowSurfaceContainerLowest, RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "4TA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowTextMuted
                    )
                }
            }

            TextButton(
                onClick = onSkipClick,
                shape = CircleShape
            ) {
                Text(
                    text = "Saltar",
                    fontSize = 14.sp,
                    color = TaxflowTextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Hero Stacked Showcase Graphic
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ambient glowing blobs
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = 60.dp, y = (-50).dp)
                    .background(
                        Brush.radialGradient(
                            listOf(TaxflowEmeraldContainer.copy(alpha = 0.6f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .offset(x = (-60).dp, y = 80.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(TaxflowSurfaceContainerHigh.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )

            // Back Layer Card: Digital RHE Mockup
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .offset(y = (-45).dp)
                    .rotate(-3f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = TaxflowEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "RHE E001-142",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxflowTextMuted
                            )
                        }
                        Text("SUNAT", fontSize = 11.sp, color = TaxflowTextMuted)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("Honorario Bruto", fontSize = 10.sp, color = TaxflowOutline)
                            Text("S/ 4,800.00", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .background(TaxflowSurfaceContainerLow, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Retención 8%", fontSize = 10.sp, color = TaxflowTextMuted)
                        }
                    }
                }
            }

            // Foreground Layer Card: Escudo Tributario & Fiscal Hub
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .offset(y = 15.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Shield header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(TaxflowEmeraldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = TaxflowEmeraldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Escudo Tributario",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TaxflowTextPrimary
                                )
                                Text(
                                    text = "● Deducciones 4ta Categoría",
                                    fontSize = 11.sp,
                                    color = TaxflowEmeraldDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(TaxflowEmeraldContainer, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("2026", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TaxflowEmeraldDark)
                        }
                    }

                    // Progress 1: Base legal 7 UIT
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Base legal 7 UIT (Automática)", fontSize = 11.sp, color = TaxflowTextMuted)
                            Text("S/ 36,050", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                        }
                        LinearProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TaxflowNavy,
                            trackColor = TaxflowSurfaceContainerLow
                        )
                    }

                    // Progress 2: Gastos 3 UIT
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gastos 3 UIT Adicionales", fontSize = 11.sp, color = TaxflowTextMuted)
                            Text("S/ 6,840 / 15,450", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TaxflowEmerald)
                        }
                        LinearProgressIndicator(
                            progress = { 0.44f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TaxflowEmerald,
                            trackColor = TaxflowSurfaceContainerLow
                        )
                    }

                    // Bottom Micro-chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TaxflowSurfaceContainerLow, RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(16.dp))
                            Column {
                                Text("Estado RUC", fontSize = 9.sp, color = TaxflowOutline)
                                Text("Activo / Habido", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = TaxflowAmber, modifier = Modifier.size(16.dp))
                            Column {
                                Text("Próx. DJ Anual", fontSize = 9.sp, color = TaxflowOutline)
                                Text("Dígito 4 · Mar 26", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TaxflowTextPrimary)
                            }
                        }
                    }
                }
            }

            // Floating Badge 1: Savings Tally (Top-Right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = 30.dp)
                    .shadow(6.dp, CircleShape)
                    .background(TaxflowEmerald, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.TrendingDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("+S/ 1,200 ahorro", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Floating Badge 2: Compliance (Bottom-Left)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-4).dp, y = 28.dp)
                    .shadow(6.dp, CircleShape)
                    .background(TaxflowNavy, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.TaskAlt, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(14.dp))
                    Text("SUNAT al día", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Carousel Pagination Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(6.dp)
                    .background(TaxflowNavy, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(TaxflowSurfaceContainerHigh, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(TaxflowSurfaceContainerHigh, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Headline and Description
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = "Controla tus impuestos sin complicaciones",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TaxflowTextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Gestiona ingresos, gastos deducibles y fechas SUNAT en un solo lugar diseñado para trabajadores independientes de 4ta categoría.",
                fontSize = 14.sp,
                color = TaxflowTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Comenzar ahora",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }

            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ya tengo una cuenta · Iniciar sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TaxflowTextMuted
                )
            }
        }
    }
}
