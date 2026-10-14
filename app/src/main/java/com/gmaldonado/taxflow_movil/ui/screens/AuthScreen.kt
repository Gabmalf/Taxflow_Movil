package com.gmaldonado.taxflow_movil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.R
import com.gmaldonado.taxflow_movil.data.remote.models.DemoUser
import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import com.gmaldonado.taxflow_movil.ui.theme.*

@Composable
fun AuthScreen(
    initialIsSignUp: Boolean = true,
    onBackClick: () -> Unit,
    onAuthSuccess: (DemoUser) -> Unit
) {
    var isSignUp by remember { mutableStateOf(initialIsSignUp) }
    var passwordVisible by remember { mutableStateOf(false) }

    var demoUsers by remember { mutableStateOf<List<DemoUser>>(emptyList()) }
    var selectedUser by remember { mutableStateOf<DemoUser?>(null) }
    var isLoadingUsers by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        val res = TaxflowRepository.fetchDemoUsers()
        res.onSuccess { users ->
            demoUsers = users
            if (users.isNotEmpty()) {
                selectedUser = users.first()
            }
            isLoadingUsers = false
        }.onFailure {
            isLoadingUsers = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaxflowBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(38.dp)
                    .background(TaxflowSurfaceContainerLow, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TaxflowTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.taxflow_app_icon),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Text(
                    text = "TAXFLOW ",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxflowTextPrimary
                )
                Text(
                    text = "Perú",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = TaxflowEmerald
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(TaxflowSurfaceContainerLow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = TaxflowEmerald,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Headline
        Text(
            text = "Acceso Demo en la Nube",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TaxflowTextPrimary
        )
        Text(
            text = "Conexión en vivo con base de datos AWS DynamoDB y API REST Gateway.",
            fontSize = 13.sp,
            color = TaxflowTextMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Demo User Selector Card
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PERFILES FICTICIOS EN AWS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowEmeraldDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Selector Demo",
                        fontSize = 11.sp,
                        color = TaxflowTextMuted
                    )
                }

                if (isLoadingUsers) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TaxflowEmerald)
                    }
                } else if (demoUsers.isEmpty()) {
                    Text(
                        text = "No se pudieron obtener usuarios de AWS. Verifique la conexión a Internet.",
                        color = TaxflowCoralRed,
                        fontSize = 12.sp
                    )
                } else {
                    demoUsers.forEach { user ->
                        val isSelected = selectedUser?.id == user.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedUser = user
                                    TaxflowRepository.selectUser(user)
                                    onAuthSuccess(user)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) TaxflowEmeraldContainer.copy(alpha = 0.5f) else TaxflowSurfaceContainerLow
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, TaxflowEmerald) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) TaxflowEmerald else TaxflowSurfaceContainerLowest),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.id.takeLast(2),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else TaxflowTextPrimary
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = user.nombre,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TaxflowTextPrimary
                                        )
                                        Text(
                                            text = "${user.profesion} • RUC: ...${user.ultimoDigitoRuc}",
                                            fontSize = 12.sp,
                                            color = TaxflowTextMuted
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = if (isSelected) TaxflowEmerald else TaxflowTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Direct Login Button
        selectedUser?.let { user ->
            Button(
                onClick = {
                    TaxflowRepository.selectUser(user)
                    onAuthSuccess(user)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
            ) {
                Text(
                    text = "Entrar como ${user.nombre}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Security / Demo footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.CloudQueue, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(16.dp))
                Text(
                    text = "API REST AWS: execute-api.us-east-1.amazonaws.com",
                    fontSize = 11.sp,
                    color = TaxflowTextMuted
                )
            }
            Text(
                text = "Modo Demostración Académica · Datos ficticios en DynamoDB",
                fontSize = 11.sp,
                color = TaxflowTextMuted
            )
        }
    }
}
