package com.gmaldonado.taxflow_movil.data.remote

import com.gmaldonado.taxflow_movil.data.remote.models.*
import retrofit2.http.*

interface TaxflowApiService {

    @GET("v1/demo/users")
    suspend fun getDemoUsers(): DemoUserListResponse

    @GET("v1/demo/users/{user_id}/dashboard")
    suspend fun getDashboard(
        @Path("user_id") userId: String,
        @Query("anio") anio: Int = 2026,
        @Query("mes") mes: Int = 9
    ): DashboardResponse

    @GET("v1/demo/users/{user_id}/incomes")
    suspend fun getIncomes(
        @Path("user_id") userId: String,
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null,
        @Query("q") q: String? = null,
        @Query("cliente") cliente: String? = null,
        @Query("estado") estado: String? = null
    ): IncomeListResponse

    @GET("v1/demo/users/{user_id}/incomes/{id}")
    suspend fun getIncomeDetail(
        @Path("user_id") userId: String,
        @Path("id") id: String
    ): IncomeDto

    @GET("v1/demo/users/{user_id}/expenses")
    suspend fun getExpenses(
        @Path("user_id") userId: String,
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null,
        @Query("categoria") categoria: String? = null,
        @Query("estado") estado: String? = null
    ): ExpenseListResponse

    @GET("v1/demo/users/{user_id}/expenses/{id}")
    suspend fun getExpenseDetail(
        @Path("user_id") userId: String,
        @Path("id") id: String
    ): ExpenseDto

    @GET("v1/demo/users/{user_id}/calendar")
    suspend fun getCalendar(
        @Path("user_id") userId: String,
        @Query("anio") anio: Int = 2026
    ): CalendarItemListResponse

    @GET("v1/demo/users/{user_id}/notifications")
    suspend fun getNotifications(
        @Path("user_id") userId: String
    ): NotificationListResponse

    @POST("v1/demo/users/{user_id}/simulations")
    suspend fun simulateIncome(
        @Path("user_id") userId: String,
        @Body request: IncomeSimulationRequest
    ): SimulationResultDto

    @POST("v1/demo/users/{user_id}/simulations")
    suspend fun simulateExpense(
        @Path("user_id") userId: String,
        @Body request: ExpenseSimulationRequest
    ): SimulationResultDto
}
