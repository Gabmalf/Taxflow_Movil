package com.gmaldonado.taxflow_movil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.ui.theme.*

enum class BottomTab(val title: String, val icon: ImageVector) {
    INICIO("Inicio", Icons.Default.Home),
    INGRESOS("Ingresos", Icons.AutoMirrored.Filled.TrendingUp),
    GASTOS("Gastos", Icons.AutoMirrored.Filled.ReceiptLong),
    SUNAT("SUNAT", Icons.Default.CalendarMonth)
}

@Composable
fun TaxflowBottomBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    onCenterActionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
            color = TaxflowSurfaceContainerLowest,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Inicio
                BottomNavItem(
                    tab = BottomTab.INICIO,
                    isSelected = currentTab == BottomTab.INICIO,
                    onClick = { onTabSelected(BottomTab.INICIO) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 2: Ingresos
                BottomNavItem(
                    tab = BottomTab.INGRESOS,
                    isSelected = currentTab == BottomTab.INGRESOS,
                    onClick = { onTabSelected(BottomTab.INGRESOS) },
                    modifier = Modifier.weight(1f)
                )

                // Space in the center for the elevated FAB
                Spacer(modifier = Modifier.width(60.dp))

                // Tab 3: Gastos
                BottomNavItem(
                    tab = BottomTab.GASTOS,
                    isSelected = currentTab == BottomTab.GASTOS,
                    onClick = { onTabSelected(BottomTab.GASTOS) },
                    modifier = Modifier.weight(1f)
                )

                // Tab 4: SUNAT
                BottomNavItem(
                    tab = BottomTab.SUNAT,
                    isSelected = currentTab == BottomTab.SUNAT,
                    hasBadge = true,
                    onClick = { onTabSelected(BottomTab.SUNAT) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center Floating Action Button
        Box(
            modifier = Modifier
                .padding(bottom = 26.dp)
                .size(52.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(TaxflowEmerald)
                .clickable { onCenterActionClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Registrar Comprobante / Simulador",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: BottomTab,
    isSelected: Boolean,
    hasBadge: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = TaxflowNavy
    val inactiveColor = TaxflowTextMuted

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.title,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(22.dp)
            )
            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(6.dp)
                        .background(TaxflowAmber, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tab.title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}
