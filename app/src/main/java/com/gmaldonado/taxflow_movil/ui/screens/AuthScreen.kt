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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmaldonado.taxflow_movil.R
import com.gmaldonado.taxflow_movil.ui.theme.*

@Composable
fun AuthScreen(
    initialIsSignUp: Boolean = true,
    onBackClick: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(initialIsSignUp) }
    var fullName by remember { mutableStateOf("Carlos Mendoza Ramos") }
    var email by remember { mutableStateOf("carlos.mendoza@email.com") }
    var password by remember { mutableStateOf("ContraseñaSegura2026#") }
    var ruc by remember { mutableStateOf("10458923014") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

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
                    tint = TaxflowTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Headline
        Text(
            text = if (isSignUp) "Crea tu cuenta" else "Iniciar sesión",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TaxflowTextPrimary
        )
        Text(
            text = "Gestiona tus impuestos de 4ta categoría de forma automática y segura.",
            fontSize = 14.sp,
            color = TaxflowTextMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // Segmented Control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TaxflowSurfaceContainerLow, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { isSignUp = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSignUp) TaxflowSurfaceContainerLowest else Color.Transparent,
                    contentColor = if (isSignUp) TaxflowTextPrimary else TaxflowTextMuted
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSignUp) 2.dp else 0.dp)
            ) {
                Text("Registrarse", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { isSignUp = false },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isSignUp) TaxflowSurfaceContainerLowest else Color.Transparent,
                    contentColor = if (!isSignUp) TaxflowTextPrimary else TaxflowTextMuted
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (!isSignUp) 2.dp else 0.dp)
            ) {
                Text("Iniciar sesión", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Fields
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Full Name (Only on Sign Up)
            if (isSignUp) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Nombre completo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TaxflowTextPrimary)
                        Text("Como figura en tu DNI", fontSize = 11.sp, color = TaxflowTextMuted)
                    }
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = { Text("Ej. Carlos Mendoza Ramos") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TaxflowTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TaxflowSurfaceContainerLowest,
                            unfocusedContainerColor = TaxflowSurfaceContainerLowest,
                            focusedBorderColor = TaxflowNavy,
                            unfocusedBorderColor = TaxflowBorder
                        )
                    )
                }
            }

            // Email
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Correo electrónico", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TaxflowTextPrimary)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("tu.correo@ejemplo.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TaxflowTextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TaxflowSurfaceContainerLowest,
                        unfocusedContainerColor = TaxflowSurfaceContainerLowest,
                        focusedBorderColor = TaxflowNavy,
                        unfocusedBorderColor = TaxflowBorder
                    )
                )
            }

            // Password
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Contraseña de acceso", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TaxflowTextPrimary)
                    if (isSignUp) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(13.dp))
                            Text("Segura (8+ car.)", fontSize = 11.sp, color = TaxflowEmerald, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Mínimo 8 caracteres") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TaxflowTextMuted) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                tint = TaxflowTextMuted
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TaxflowSurfaceContainerLowest,
                        unfocusedContainerColor = TaxflowSurfaceContainerLowest,
                        focusedBorderColor = TaxflowNavy,
                        unfocusedBorderColor = TaxflowBorder
                    )
                )
            }

            // RUC (11 digits)
            if (isSignUp) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("RUC (11 dígitos)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TaxflowTextPrimary)
                    OutlinedTextField(
                        value = ruc,
                        onValueChange = { if (it.length <= 11 && it.all { ch -> ch.isDigit() }) ruc = it },
                        placeholder = { Text("10XXXXXXXXX") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = TaxflowTextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = TaxflowSurfaceContainerLowest,
                            unfocusedContainerColor = TaxflowSurfaceContainerLowest,
                            focusedBorderColor = TaxflowNavy,
                            unfocusedBorderColor = TaxflowBorder
                        )
                    )
                }

                // Terms checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { termsAccepted = !termsAccepted }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = TaxflowNavy)
                    )
                    Text(
                        text = "Acepto los Términos de Servicio y la Política de Privacidad respaldada por SUNAT.",
                        fontSize = 12.sp,
                        color = TaxflowTextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Submit Button
        Button(
            onClick = onAuthSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TaxflowNavy)
        ) {
            Text(
                text = if (isSignUp) "Crear cuenta" else "Ingresar a Taxflow",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Security footer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (!isSignUp) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TaxflowTextMuted
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = TaxflowEmerald, modifier = Modifier.size(14.dp))
                Text(
                    text = "Conexión cifrada de 256 bits con clave SOL",
                    fontSize = 12.sp,
                    color = TaxflowTextMuted
                )
            }
        }
    }
}
