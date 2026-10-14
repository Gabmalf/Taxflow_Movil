package com.gmaldonado.taxflow_movil.data.repository

import com.gmaldonado.taxflow_movil.data.remote.ApiClient
import com.gmaldonado.taxflow_movil.data.remote.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DemoCredential(
    val id: String,
    val nombre: String,
    val profesion: String,
    val email: String,
    val pass: String,
    val ultimoDigitoRuc: Int
)

object TaxflowRepository {
    private val api = ApiClient.service

    // Cuentas pre-cargadas en DynamoDB para pruebas académicas rápidas
    val demoAccounts = listOf(
        DemoCredential(
            id = "demo-01",
            nombre = "Carlos Jara",
            profesion = "Desarrollador de Software",
            email = "carlos.jara@taxflow.test",
            pass = "Tf-mQ576UQEoAOEUjB9foM9!",
            ultimoDigitoRuc = 1
        ),
        DemoCredential(
            id = "demo-02",
            nombre = "Carmen González",
            profesion = "Consultora Legal",
            email = "carmen.gonzalez@taxflow.test",
            pass = "Tf-XG_yUzcZooFDsseznqqX!",
            ultimoDigitoRuc = 4
        ),
        DemoCredential(
            id = "demo-03",
            nombre = "Óscar Paredes",
            profesion = "Diseñador UX/UI",
            email = "oscar.paredes@taxflow.test",
            pass = "Tf-Xg8QqLT97kQ25ZxV8-Yw!",
            ultimoDigitoRuc = 7
        )
    )

    private var _authToken: String? = null
    fun getAuthToken(): String? = _authToken

    private val _currentUser = MutableStateFlow<ProfileDto?>(null)
    val currentUser: StateFlow<ProfileDto?> = _currentUser.asStateFlow()

    // Contador de sesión para descartar respuestas asíncronas obsoletas tras cambio de sesión
    private var activeSessionToken: Long = 0L
    fun getSessionToken(): Long = activeSessionToken

    fun isLoggedIn(): Boolean = !_authToken.isNullOrBlank()

    // ── AUTENTICACIÓN ────────────────────────────────────────────────────────

    suspend fun login(email: String, pass: String): Result<ProfileDto> {
        return try {
            val res = api.login(LoginRequest(email.trim().lowercase(), pass))
            activeSessionToken++
            _authToken = res.accessToken
            _currentUser.value = res.user
            Result.success(res.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(nombre: String, email: String, pass: String): Result<ProfileDto> {
        return try {
            val res = api.register(RegisterRequest(nombre.trim(), email.trim().lowercase(), pass))
            activeSessionToken++
            _authToken = res.accessToken
            _currentUser.value = res.user
            Result.success(res.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Boolean> {
        return try {
            if (isLoggedIn()) {
                api.logout()
            }
            clearSession()
            Result.success(true)
        } catch (e: Exception) {
            clearSession()
            Result.failure(e)
        }
    }

    fun clearSession() {
        activeSessionToken++
        _authToken = null
        _currentUser.value = null
    }

    // ── DATOS DE CUENTA (/v1/me) ─────────────────────────────────────────────

    suspend fun getProfile(): Result<ProfileDto> {
        return try {
            val res = api.getProfile()
            _currentUser.value = res
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    suspend fun getDashboard(anio: Int = 2026, mes: Int = 9): Result<DashboardResponse> {
        return try {
            val res = api.getDashboard(anio = anio, mes = mes)
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    suspend fun getIncomes(
        desde: String? = null,
        hasta: String? = null,
        q: String? = null,
        cliente: String? = null,
        estado: String? = null
    ): Result<IncomeListResponse> {
        return try {
            val res = api.getIncomes(desde, hasta, q, cliente, estado)
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    suspend fun getIncomeDetail(id: String): Result<IncomeDto> {
        return try {
            val res = api.getIncomeDetail(id)
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    suspend fun getExpenses(
        desde: String? = null,
        hasta: String? = null,
        categoria: String? = null,
        estado: String? = null
    ): Result<ExpenseListResponse> {
        return try {
            val res = api.getExpenses(desde, hasta, categoria, estado)
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    suspend fun getExpenseDetail(id: String): Result<ExpenseDto> {
        return try {
            val res = api.getExpenseDetail(id)
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    suspend fun getCalendar(anio: Int = 2026): Result<CalendarItemListResponse> {
        return try {
            val res = api.getCalendar(anio = anio)
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    suspend fun getNotifications(): Result<NotificationListResponse> {
        return try {
            val res = api.getNotifications()
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    suspend fun simulateIncome(montoCentimos: Long, aplicarRetencion: Boolean): Result<SimulationResultDto> {
        return try {
            val res = api.simulateIncome(IncomeSimulationRequest(montoCentimos = montoCentimos, aplicarRetencion = aplicarRetencion))
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    suspend fun simulateExpense(montoCentimos: Long, categoria: String): Result<SimulationResultDto> {
        return try {
            val res = api.simulateExpense(ExpenseSimulationRequest(montoCentimos = montoCentimos, categoria = categoria))
            Result.success(res)
        } catch (e: Exception) {
            handleUnauthorized(e)
            Result.failure(e)
        }
    }

    private fun handleUnauthorized(e: Exception) {
        if (e is retrofit2.HttpException && e.code() == 401) {
            clearSession()
        }
    }
}
