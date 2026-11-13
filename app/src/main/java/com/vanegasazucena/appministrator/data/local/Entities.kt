package com.vanegasazucena.appministrator.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val password: String,
    val role: String // "ADMIN" or "COMMON"
)

@Entity(tableName = "encomiendas")
data class EncomiendaEntity(
    @PrimaryKey val id: String, // e.g. "ENC-00123"
    val emisorNombre: String,
    val emisorDui: String,
    val remitenteNombre: String,
    val remitenteDui: String,
    val creadoPor: String,
    val codigo: String,
    val estado: String, // "PAGADO", "PENDIENTE", "ENTREGADO"
    val tamano: String, // "LIVIANO", "MEDIANO", "GRANDE"
    val precio: Double,
    val fecha: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val encomiendaId: String,
    val accion: String,
    val usuario: String,
    val detalles: String,
    val fechaHora: String
)
