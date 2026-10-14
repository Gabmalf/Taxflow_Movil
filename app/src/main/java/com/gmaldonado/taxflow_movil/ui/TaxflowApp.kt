package com.gmaldonado.taxflow_movil.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.gmaldonado.taxflow_movil.data.formatters.Formatters
import com.gmaldonado.taxflow_movil.data.remote.models.ProfileDto
import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import com.gmaldonado.taxflow_movil.ui.components.*
import com.gmaldonado.taxflow_movil.ui.screens.*
import com.gmaldonado.taxflow_movil.ui.theme.TaxflowBackground
import kotlinx.coroutines.launch

enum class AppScreen {
    ONBOARDING,
    AUTH_SIGNUP,
    AUTH_LOGIN,
    MAIN_APP
}

@Composable
fun TaxflowApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.ONBOARDING) }
    var currentBottomTab by remember { mutableStateOf(BottomTab.INICIO) }
    var selectedReceipt by remember { mutableStateOf<ReceiptData?>(null) }
    var showSimulator by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

    val currentUser by TaxflowRepository.currentUser.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Si la sesión expira (401) en la pantalla principal, redirigir al login
    LaunchedEffect(currentUser) {
        if (currentScreen == AppScreen.MAIN_APP && currentUser == null && !TaxflowRepository.isLoggedIn()) {
            currentScreen = AppScreen.AUTH_LOGIN
            snackbarHostState.showSnackbar("Sesión expirada. Por favor ingresa nuevamente.")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen == AppScreen.MAIN_APP) {
                TaxflowTopBar(
                    currentUser = currentUser,
                    title = when (currentBottomTab) {
                        BottomTab.INICIO -> null
                        BottomTab.INGRESOS -> "Mis Ingresos"
                        BottomTab.GASTOS -> "Mis Gastos Deducibles"
                        BottomTab.SUNAT -> "Calendario SUNAT"
                    },
                    subtitle = when (currentBottomTab) {
                        BottomTab.INICIO -> null
                        BottomTab.INGRESOS -> "4ta Categoría"
                        BottomTab.GASTOS -> "3 UIT Deducibles"
                        BottomTab.SUNAT -> currentUser?.ultimoDigitoRuc?.let { "Dígito RUC: $it" } ?: "4ta Categoría"
                    },
                    onNotificationClick = { showNotifications = true },
                    onProfileClick = { showProfile = true }
                )
            }
        },
        bottomBar = {
            if (currentScreen == AppScreen.MAIN_APP) {
                TaxflowBottomBar(
                    currentTab = currentBottomTab,
                    onTabSelected = { currentBottomTab = it },
                    onCenterActionClick = { showSimulator = true }
                )
            }
        },
        containerColor = TaxflowBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.ONBOARDING -> {
                    OnboardingScreen(
                        onStartClick = { currentScreen = AppScreen.AUTH_SIGNUP },
                        onLoginClick = { currentScreen = AppScreen.AUTH_LOGIN },
                        onSkipClick = {
                            if (!TaxflowRepository.isLoggedIn()) {
                                scope.launch {
                                    val demo = TaxflowRepository.demoAccounts.first()
                                    TaxflowRepository.login(demo.email, demo.pass)
                                }
                            }
                            currentScreen = AppScreen.MAIN_APP
                        }
                    )
                }

                AppScreen.AUTH_SIGNUP -> {
                    AuthScreen(
                        initialIsSignUp = true,
                        onBackClick = { currentScreen = AppScreen.ONBOARDING },
                        onAuthSuccess = { profile ->
                            currentScreen = AppScreen.MAIN_APP
                            scope.launch {
                                snackbarHostState.showSnackbar("Bienvenido a Taxflow, ${profile.nombre}")
                            }
                        }
                    )
                }

                AppScreen.AUTH_LOGIN -> {
                    AuthScreen(
                        initialIsSignUp = false,
                        onBackClick = { currentScreen = AppScreen.ONBOARDING },
                        onAuthSuccess = { profile ->
                            currentScreen = AppScreen.MAIN_APP
                            scope.launch {
                                snackbarHostState.showSnackbar("Sesión iniciada como ${profile.nombre}")
                            }
                        }
                    )
                }

                AppScreen.MAIN_APP -> {
                    AnimatedContent(
                        targetState = currentBottomTab,
                        label = "BottomTabTransition"
                    ) { tab ->
                        when (tab) {
                            BottomTab.INICIO -> DashboardScreen(
                                currentUser = currentUser,
                                onNavigateToIngresos = { currentBottomTab = BottomTab.INGRESOS },
                                onNavigateToGastos = { currentBottomTab = BottomTab.GASTOS },
                                onNavigateToSunat = { currentBottomTab = BottomTab.SUNAT },
                                onReceiptClick = { selectedReceipt = it }
                            )

                            BottomTab.INGRESOS -> IngresosScreen(
                                currentUser = currentUser,
                                onReceiptClick = { selectedReceipt = it }
                            )

                            BottomTab.GASTOS -> GastosScreen(
                                currentUser = currentUser,
                                onReceiptClick = { selectedReceipt = it }
                            )

                            BottomTab.SUNAT -> SunatCalendarScreen(
                                currentUser = currentUser
                            )
                        }
                    }
                }
            }
        }

        // Receipt Detail Dialog
        selectedReceipt?.let { receipt ->
            ReceiptDetailDialog(
                receipt = receipt,
                onDismiss = { selectedReceipt = null }
            )
        }

        // Profile Dialog with Demo User Switching
        if (showProfile) {
            ProfileDialog(
                currentUser = currentUser,
                onSwitchDemoAccount = { acc ->
                    scope.launch {
                        val res = TaxflowRepository.login(acc.email, acc.pass)
                        res.onSuccess {
                            snackbarHostState.showSnackbar("Sesión cambiada a ${it.nombre} (${it.id})")
                        }
                    }
                },
                onDismiss = { showProfile = false },
                onLogout = {
                    showProfile = false
                    scope.launch {
                        TaxflowRepository.logout()
                        currentScreen = AppScreen.AUTH_LOGIN
                        snackbarHostState.showSnackbar("Sesión cerrada")
                    }
                }
            )
        }

        // Notifications Dialog
        if (showNotifications) {
            NotificationsDialog(
                onDismiss = { showNotifications = false }
            )
        }

        // Simulator / Register Bottom Sheet
        if (showSimulator) {
            SimulatorBottomSheet(
                onDismiss = { showSimulator = false },
                onSimulationDone = { result ->
                    scope.launch {
                        val formatted = Formatters.formatCentimosAsPen(result.montoCentimos)
                        snackbarHostState.showSnackbar("Simulación de $formatted calculada en AWS Lambda (${result.reglasVersion})")
                    }
                }
            )
        }
    }
}
