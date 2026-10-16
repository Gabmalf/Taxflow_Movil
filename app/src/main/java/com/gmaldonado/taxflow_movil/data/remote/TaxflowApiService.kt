package com.gmaldonado.taxflow_movil.data.remote

import com.gmaldonado.taxflow_movil.data.remote.models.*
import retrofit2.http.*

interface TaxflowApiService {

    // ── AUTENTICACIÓN ────────────────────────────────────────────────────────

    @POST("v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): SessionResponse

    @POST("v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): SessionResponse

    @POST("v1/auth/logout")
    suspend fun logout(
        @Body emptyBody: Map<String, String> = emptyMap() // Backend exige cuerpo JSON {}
    ): LogoutResponse

    @GET("v1/me")
    suspend fun getProfile(): ProfileDto

    // ── RECURSOS DEL USUARIO AUTENTICADO (/v1/me) ───────────────────────────

    @GET("v1/me/dashboard")
    suspend fun getDashboard(
        @Query("anio") anio: Int = 2026,
        @Query("mes") mes: Int = 9
    ): DashboardResponse

    @GET("v1/me/incomes")
    suspend fun getIncomes(
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null,
        @Query("q") q: String? = null,
        @Query("cliente") cliente: String? = null,
        @Query("estado") estado: String? = null
    ): IncomeListResponse

    @GET("v1/me/incomes/{id}")
    suspend fun getIncomeDetail(
        @Path("id") id: String
    ): IncomeDto

    @GET("v1/me/expenses")
    suspend fun getExpenses(
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null,
        @Query("categoria") categoria: String? = null,
        @Query("estado") estado: String? = null
    ): ExpenseListResponse

    @GET("v1/me/expenses/{id}")
    suspend fun getExpenseDetail(
        @Path("id") id: String
    ): ExpenseDto

    @GET("v1/me/calendar")
    suspend fun getCalendar(
        @Query("anio") anio: Int = 2026
    ): CalendarItemListResponse

    @GET("v1/me/notifications")
    suspend fun getNotifications(): NotificationListResponse

    @POST("v1/me/simulations")
    suspend fun simulateIncome(
        @Body request: IncomeSimulationRequest
    ): SimulationResultDto

    @POST("v1/me/simulations")
    suspend fun simulateExpense(
        @Body request: ExpenseSimulationRequest
    ): SimulationResultDto

    @POST("v1/me/incomes")
    suspend fun registerIncome(
        @Body request: RegisterIncomeRequest
    ): IncomeDto

    @POST("v1/me/expenses")
    suspend fun registerExpense(
        @Body request: RegisterExpenseRequest
    ): ExpenseDto
}
