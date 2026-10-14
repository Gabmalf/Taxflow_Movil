package com.gmaldonado.taxflow_movil.data.remote.models

import com.google.gson.annotations.SerializedName

// --- USUARIO DEMO ---

data class DemoUser(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("profesion") val profesion: String,
    @SerializedName("ultimo_digito_ruc") val ultimoDigitoRuc: Int,
    @SerializedName("es_ficticio") val esFicticio: Boolean
)

data class DemoUserListResponse(
    @SerializedName("items") val items: List<DemoUser>,
    @SerializedName("total") val total: Int
)

// --- DASHBOARD ---

data class RecentActivityDto(
    @SerializedName("tipo") val tipo: String, // "INGRESO" | "GASTO"
    @SerializedName("id") val id: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("monto_centimos") val montoCentimos: Long
)

data class DashboardResponse(
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("anio") val anio: Int,
    @SerializedName("mes") val mes: Int,
    @SerializedName("ingresos_mes_centimos") val ingresosMesCentimos: Long,
    @SerializedName("ingresos_anio_centimos") val ingresosAnioCentimos: Long,
    @SerializedName("retenciones_anio_centimos") val retencionesAnioCentimos: Long,
    @SerializedName("gastos_anio_centimos") val gastosAnioCentimos: Long,
    @SerializedName("deducible_anio_centimos") val deducibleAnioCentimos: Long,
    @SerializedName("limite_7_uit_centimos") val limite7UitCentimos: Long,
    @SerializedName("limite_3_uit_centimos") val limite3UitCentimos: Long,
    @SerializedName("reglas_version") val reglasVersion: String,
    @SerializedName("actividad_reciente") val actividadReciente: List<RecentActivityDto>
)

// --- INGRESOS ---

data class IncomeDto(
    @SerializedName("id") val id: String,
    @SerializedName("cliente") val cliente: String,
    @SerializedName("serie") val serie: String,
    @SerializedName("numero") val numero: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("monto_bruto_centimos") val montoBrutoCentimos: Long,
    @SerializedName("retencion_centimos") val retencionCentimos: Long,
    @SerializedName("monto_neto_centimos") val montoNetoCentimos: Long,
    @SerializedName("estado") val estado: String, // "PAGADO" | "PENDIENTE" | "REVERTIDO"
    @SerializedName("origen") val origen: String
)

data class IncomeResumenDto(
    @SerializedName("bruto_centimos") val brutoCentimos: Long,
    @SerializedName("retencion_centimos") val retencionCentimos: Long,
    @SerializedName("neto_centimos") val netoCentimos: Long
)

data class IncomeListResponse(
    @SerializedName("items") val items: List<IncomeDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("resumen") val resumen: IncomeResumenDto
)

// --- GASTOS ---

data class ExpenseDto(
    @SerializedName("id") val id: String,
    @SerializedName("proveedor") val proveedor: String,
    @SerializedName("serie") val serie: String,
    @SerializedName("numero") val numero: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("categoria") val categoria: String, // "restaurantes" | "servicios" | "alojamiento" | "essalud"
    @SerializedName("monto_centimos") val montoCentimos: Long,
    @SerializedName("porcentaje_deducible_pb") val porcentajeDeduciblePb: Int,
    @SerializedName("deducible_centimos") val deducibleCentimos: Long,
    @SerializedName("estado") val estado: String, // "MANUAL" | "OBSERVADO"
    @SerializedName("origen") val origen: String
)

data class ExpenseResumenDto(
    @SerializedName("total_centimos") val totalCentimos: Long,
    @SerializedName("deducible_centimos") val deducibleCentimos: Long
)

data class ExpenseListResponse(
    @SerializedName("items") val items: List<ExpenseDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("resumen") val resumen: ExpenseResumenDto
)

// --- CALENDARIO SUNAT ---

data class CalendarItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("formulario") val formulario: String,
    @SerializedName("estado") val estado: String, // "PENDIENTE" | "PRESENTADO" | "ACTIVO"
    @SerializedName("detalle") val detalle: String,
    @SerializedName("origen") val origen: String
)

data class CalendarItemListResponse(
    @SerializedName("items") val items: List<CalendarItemDto>,
    @SerializedName("total") val total: Int
)

// --- NOTIFICACIONES ---

data class NotificationDto(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("es_urgente") val esUrgente: Boolean,
    @SerializedName("origen") val origen: String
)

data class NotificationListResponse(
    @SerializedName("items") val items: List<NotificationDto>,
    @SerializedName("total") val total: Int
)

// --- SIMULADOR TRIBUTARIO ---

data class IncomeSimulationRequest(
    @SerializedName("tipo") val tipo: String = "INGRESO",
    @SerializedName("monto_centimos") val montoCentimos: Long,
    @SerializedName("aplicar_retencion") val aplicarRetencion: Boolean
)

data class ExpenseSimulationRequest(
    @SerializedName("tipo") val tipo: String = "GASTO",
    @SerializedName("monto_centimos") val montoCentimos: Long,
    @SerializedName("categoria") val categoria: String
)

data class SimulationResultDto(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("monto_centimos") val montoCentimos: Long,
    @SerializedName("retencion_centimos") val retencionCentimos: Long,
    @SerializedName("neto_centimos") val netoCentimos: Long,
    @SerializedName("deducible_centimos") val deducibleCentimos: Long,
    @SerializedName("reglas_version") val reglasVersion: String,
    @SerializedName("persistido") val persistido: Boolean
)

// --- ERROR ---

data class ApiErrorDetail(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("request_id") val requestId: String
)

data class ApiErrorResponse(
    @SerializedName("error") val error: ApiErrorDetail
)
