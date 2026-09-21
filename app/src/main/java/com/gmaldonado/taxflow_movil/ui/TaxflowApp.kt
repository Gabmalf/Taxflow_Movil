package com.gmaldonado.taxflow_movil.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen == AppScreen.MAIN_APP) {
                TaxflowTopBar(
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
                        BottomTab.SUNAT -> "Dígito RUC: 4"
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
                        onSkipClick = { currentScreen = AppScreen.MAIN_APP }
                    )
                }

                AppScreen.AUTH_SIGNUP -> {
                    AuthScreen(
                        initialIsSignUp = true,
                        onBackClick = { currentScreen = AppScreen.ONBOARDING },
                        onAuthSuccess = {
                            currentScreen = AppScreen.MAIN_APP
                            scope.launch {
                                snackbarHostState.showSnackbar("¡Bienvenido a Taxflow Perú, Carlos!")
                            }
                        }
                    )
                }

                AppScreen.AUTH_LOGIN -> {
                    AuthScreen(
                        initialIsSignUp = false,
                        onBackClick = { currentScreen = AppScreen.ONBOARDING },
                        onAuthSuccess = {
                            currentScreen = AppScreen.MAIN_APP
                            scope.launch {
                                snackbarHostState.showSnackbar("Sesión iniciada con Clave SOL correctamente")
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
                                onNavigateToIngresos = { currentBottomTab = BottomTab.INGRESOS },
                                onNavigateToGastos = { currentBottomTab = BottomTab.GASTOS },
                                onNavigateToSunat = { currentBottomTab = BottomTab.SUNAT },
                                onReceiptClick = { selectedReceipt = it }
                            )

                            BottomTab.INGRESOS -> IngresosScreen(
                                onReceiptClick = { selectedReceipt = it }
                            )

                            BottomTab.GASTOS -> GastosScreen(
                                onReceiptClick = { selectedReceipt = it }
                            )

                            BottomTab.SUNAT -> SunatCalendarScreen()
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

        // Profile Dialog
        if (showProfile) {
            ProfileDialog(
                onDismiss = { showProfile = false },
                onLogout = {
                    showProfile = false
                    currentScreen = AppScreen.ONBOARDING
                    scope.launch {
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
                onSimulateSuccess = { client, gross, retention, net ->
                    scope.launch {
                        snackbarHostState.showSnackbar("Comprobante de S/ %,.2f registrado en el prototipo".format(gross))
                    }
                }
            )
        }
    }
}
