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
import com.gmaldonado.taxflow_movil.data.remote.models.ProfileDto
import com.gmaldonado.taxflow_movil.data.repository.DemoCredential
import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import com.gmaldonado.taxflow_movil.ui.theme.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun AuthScreen(
    initialIsSignUp: Boolean = false,
    onBackClick: () -> Unit,
    onAuthSuccess: (ProfileDto) -> Unit
) {
    var isSignUp by remember { mutableStateOf(initialIsSignUp) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    fun performLogin(loginEmail: String, loginPass: String) {
        if (loginEmail.isBlank() || loginPass.isBlank()) {
            errorMessage = "Ingresa tu correo y contraseña"
            return
        }
        if (loginPass.length < 12) {
            errorMessage = "La contraseña debe tener mínimo 12 caracteres"
            return
        }

        isLoading = true
        errorMessage = null

        scope.launch {
            val result = TaxflowRepository.login(loginEmail, loginPass)
            isLoading = false
            result.onSuccess { profile ->
                onAuthSuccess(profile)
            }.onFailure { ex ->
                errorMessage = when {
                    ex is HttpException && ex.code() == 401 -> "Correo o contraseña incorrectos"
                    ex is HttpException && ex.code() == 400 -> "Verifique los datos ingresados (contraseña de 12 a 128 caracteres)"
                    ex is HttpException && ex.code() == 429 -> "Límite de solicitudes alcanzado, reintentando..."
                    else -> "Error de conexión: ${ex.localizedMessage ?: "Revise su conexión a Internet"}"
                }
            }
        }
    }

    fun performRegister() {
        if (nombre.trim().length < 2) {
            errorMessage = "El nombre debe tener al menos 2 caracteres"
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            errorMessage = "Ingresa un correo electrónico válido"
            return
        }
        if (password.length < 12 || password.length > 128) {
            errorMessage = "La contraseña debe tener entre 12 y 128 caracteres"
            return
        }

        isLoading = true
        errorMessage = null

        scope.launch {
            val result = TaxflowRepository.register(nombre, email, password)
            isLoading = false
            result.onSuccess { profile ->
                onAuthSuccess(profile)
            }.onFailure { ex ->
                errorMessage = when {
                    ex is HttpException && ex.code() == 409 -> "El correo ya se encuentra registrado"
                    ex is HttpException && ex.code() == 400 -> "Datos inválidos (contraseña entre 12 y 128 caracteres)"
                    ex is HttpException && ex.code() == 429 -> "Límite de solicitudes alcanzado, espere un momento"
                    else -> "Error al registrarse: ${ex.localizedMessage ?: "Revise su conexión"}"
                }
            }
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
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TaxflowEmerald,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Headline
        Text(
            text = if (isSignUp) "Crea tu cuenta" else "Bienvenido a Taxflow",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TaxflowTextPrimary
        )
        Text(
            text = if (isSignUp)
                "Registra tu cuenta personal para gestionar tus impuestos en AWS."
            else
                "Ingresa con tus credenciales o usa una de las cuentas demo pre-cargadas.",
            fontSize = 13.sp,
            color = TaxflowTextMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Auth Tabs Toggle (Iniciar Sesión / Registrarse)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TaxflowSurfaceContainerLow, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (!isSignUp) TaxflowEmerald else Color.Transparent)
                    .clickable {
                        isSignUp = false
                        errorMessage = null
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isSignUp) Color.White else TaxflowTextMuted
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (isSignUp) TaxflowEmerald else Color.Transparent)
                    .clickable {
                        isSignUp = true
                        errorMessage = null
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Registrarse",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSignUp) Color.White else TaxflowTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error Banner
        if (errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TaxflowCoralRed.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = TaxflowCoralRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = TaxflowCoralRed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Credentials Form Card
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // If Sign Up, ask for Full Name
                if (isSignUp) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "NOMBRE COMPLETO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextMuted
                        )
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            placeholder = { Text("Ej. Ana Torres", fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = TaxflowTextMuted)
                            }
                        )
                    }
                }

                // Email field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "CORREO ELECTRÓNICO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowTextMuted
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("usuario@taxflow.test", fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = TaxflowTextMuted)
                        }
                    )
                }

                // Password field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CONTRASEÑA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxflowTextMuted
                        )
                        Text(
                            text = "12 a 128 caracteres",
                            fontSize = 11.sp,
                            color = TaxflowTextMuted
                        )
                    }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••••••", fontSize = 13.sp) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TaxflowTextMuted)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                    tint = TaxflowTextMuted
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (isSignUp) performRegister() else performLogin(email, password)
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (isSignUp) "Crear Cuenta en AWS" else "Iniciar Sesión",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pre-loaded Demo Accounts (One-tap login for rapid evaluation)
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
                        text = "ACCESO RÁPIDO CON CUENTAS DEMO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaxflowEmeraldDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Datos en DynamoDB",
                        fontSize = 11.sp,
                        color = TaxflowTextMuted
                    )
                }

                Text(
                    text = "Selecciona una cuenta de prueba para iniciar sesión automáticamente con Bearer Token y cargar comprobantes precargados:",
                    fontSize = 12.sp,
                    color = TaxflowTextMuted,
                    lineHeight = 16.sp
                )

                TaxflowRepository.demoAccounts.forEach { acc ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isLoading) {
                                email = acc.email
                                password = acc.pass
                                isSignUp = false
                                performLogin(acc.email, acc.pass)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = TaxflowSurfaceContainerLow)
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
                                        .background(TaxflowEmerald),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = acc.id.takeLast(2),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }

                                Column {
                                    Text(
                                        text = acc.nombre,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TaxflowTextPrimary
                                    )
                                    Text(
                                        text = "${acc.profesion} • RUC terminación ${acc.ultimoDigitoRuc}",
                                        fontSize = 11.sp,
                                        color = TaxflowTextMuted
                                    )
                                    Text(
                                        text = acc.email,
                                        fontSize = 10.sp,
                                        color = TaxflowEmeraldDark
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Ingresar",
                                tint = TaxflowTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Security / Backend footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.CloudQueue, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(16.dp))
                Text(
                    text = "API v2: execute-api.us-east-1.amazonaws.com",
                    fontSize = 11.sp,
                    color = TaxflowTextMuted
                )
            }
            Text(
                text = "Autenticación Bearer Token (256 bits) · Expiración 7200s",
                fontSize = 11.sp,
                color = TaxflowTextMuted
            )
        }
    }
}
