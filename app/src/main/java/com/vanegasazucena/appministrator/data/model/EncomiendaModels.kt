package com.vanegasazucena.appministrator.data.model

enum class EstadoEncomienda(val displayName: String) {
    PAGADO("Pagado"),
    PENDIENTE("Pendiente"),
    ENTREGADO("Entregado")
}

enum class TamanoEncomienda(val displayName: String, val precio: Double) {
    LIVIANO("Liviano", 3.00),
    MEDIANO("Mediano", 5.00),
    GRANDE("Grande", 8.00)
}

data class Encomienda(
    val id: String,
    val emisorNombre: String,
    val emisorDui: String,
    val remitenteNombre: String,
    val remitenteDui: String,
    val creadoPor: String,
    val codigo: String,
    val estado: EstadoEncomienda,
    val tamano: TamanoEncomienda,
    val precio: Double,
    val fecha: String
)
