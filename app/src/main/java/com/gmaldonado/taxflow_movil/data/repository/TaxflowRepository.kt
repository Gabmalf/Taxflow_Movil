package com.gmaldonado.taxflow_movil.data.repository

import com.gmaldonado.taxflow_movil.data.remote.ApiClient
import com.gmaldonado.taxflow_movil.data.remote.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TaxflowRepository {
    private val api = ApiClient.service

    // Cached demo users
    private var _cachedUsers: List<DemoUser> = emptyList()
    val cachedUsers: List<DemoUser> get() = _cachedUsers

    // Currently active demo user
    private val _currentUser = MutableStateFlow<DemoUser?>(null)
    val currentUser: StateFlow<DemoUser?> = _currentUser.asStateFlow()

    // Counter/Token to detect user switches and discard stale responses
    private var activeSessionToken: Long = 0L

    fun getSessionToken(): Long = activeSessionToken

    fun selectUser(user: DemoUser) {
        activeSessionToken++
        _currentUser.value = user
    }

    suspend fun fetchDemoUsers(): Result<List<DemoUser>> {
        return try {
            val response = api.getDemoUsers()
            _cachedUsers = response.items
            if (_currentUser.value == null && response.items.isNotEmpty()) {
                _currentUser.value = response.items.first()
            }
            Result.success(response.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDashboard(userId: String, anio: Int = 2026, mes: Int = 9): Result<DashboardResponse> {
        return try {
            val res = api.getDashboard(userId = userId, anio = anio, mes = mes)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIncomes(
        userId: String,
        desde: String? = null,
        hasta: String? = null,
        q: String? = null,
        cliente: String? = null,
        estado: String? = null
    ): Result<IncomeListResponse> {
        return try {
            val res = api.getIncomes(
                userId = userId,
                desde = desde,
                hasta = hasta,
                q = q,
                cliente = cliente,
                estado = estado
            )
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIncomeDetail(userId: String, id: String): Result<IncomeDto> {
        return try {
            val res = api.getIncomeDetail(userId = userId, id = id)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpenses(
        userId: String,
        desde: String? = null,
        hasta: String? = null,
        categoria: String? = null,
        estado: String? = null
    ): Result<ExpenseListResponse> {
        return try {
            val res = api.getExpenses(
                userId = userId,
                desde = desde,
                hasta = hasta,
                categoria = categoria,
                estado = estado
            )
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpenseDetail(userId: String, id: String): Result<ExpenseDto> {
        return try {
            val res = api.getExpenseDetail(userId = userId, id = id)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCalendar(userId: String, anio: Int = 2026): Result<CalendarItemListResponse> {
        return try {
            val res = api.getCalendar(userId = userId, anio = anio)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotifications(userId: String): Result<NotificationListResponse> {
        return try {
            val res = api.getNotifications(userId = userId)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun simulateIncome(
        userId: String,
        montoCentimos: Long,
        aplicarRetencion: Boolean
    ): Result<SimulationResultDto> {
        return try {
            val res = api.simulateIncome(
                userId = userId,
                request = IncomeSimulationRequest(
                    montoCentimos = montoCentimos,
                    aplicarRetencion = aplicarRetencion
                )
            )
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun simulateExpense(
        userId: String,
        montoCentimos: Long,
        categoria: String
    ): Result<SimulationResultDto> {
        return try {
            val res = api.simulateExpense(
                userId = userId,
                request = ExpenseSimulationRequest(
                    montoCentimos = montoCentimos,
                    categoria = categoria
                )
            )
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
