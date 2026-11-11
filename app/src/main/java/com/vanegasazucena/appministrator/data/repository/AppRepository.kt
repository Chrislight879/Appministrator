package com.vanegasazucena.appministrator.data.repository

import com.vanegasazucena.appministrator.data.local.AuditLogDao
import com.vanegasazucena.appministrator.data.local.AuditLogEntity
import com.vanegasazucena.appministrator.data.local.EncomiendaDao
import com.vanegasazucena.appministrator.data.local.EncomiendaDataGenerator
import com.vanegasazucena.appministrator.data.local.EncomiendaEntity
import com.vanegasazucena.appministrator.data.local.UserDao
import com.vanegasazucena.appministrator.data.local.UserEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(
    private val userDao: UserDao,
    private val encomiendaDao: EncomiendaDao,
    private val auditLogDao: AuditLogDao
) {
    suspend fun ensureInitialData() {
        if (userDao.getUserByUsername("admin") == null) {
            userDao.insertUser(UserEntity("admin", "admin123", "ADMIN"))
            userDao.insertUser(UserEntity("PeterParker", "user123", "COMMON"))
            userDao.insertUser(UserEntity("JuanPerez", "user123", "COMMON"))
        }

        if (encomiendaDao.getCount() < 300) {
            val list = EncomiendaDataGenerator.generate300Encomiendas()
            list.forEach { encomienda ->
                encomiendaDao.insertEncomienda(encomienda)
            }

            // Seed initial sample audit logs
            val nowStr = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            auditLogDao.insertLog(
                AuditLogEntity(
                    encomiendaId = "ENC-00001",
                    accion = "CREACIÓN",
                    usuario = "admin",
                    detalles = "Encomienda creada para Evelyn Castaneda (DUI: 82591605-7), Estado: ENTREGADO, Tamaño: LIVIANO",
                    fechaHora = nowStr
                )
            )
            auditLogDao.insertLog(
                AuditLogEntity(
                    encomiendaId = "ENC-00002",
                    accion = "ACTUALIZACIÓN",
                    usuario = "PeterParker",
                    detalles = "Estado cambiado de PENDIENTE a PAGADO. Total cobrado: $8.00",
                    fechaHora = nowStr
                )
            )
        }
    }

    fun getAllLogs(): Flow<List<AuditLogEntity>> = auditLogDao.getAllLogs()

    suspend fun insertLog(encomiendaId: String, accion: String, usuario: String, detalles: String) {
        val nowStr = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        auditLogDao.insertLog(
            AuditLogEntity(
                encomiendaId = encomiendaId,
                accion = accion,
                usuario = usuario,
                detalles = detalles,
                fechaHora = nowStr
            )
        )
    }

    suspend fun login(username: String, password: String): UserEntity? {
        val user = userDao.getUserByUsername(username)
        return if (user != null && user.password == password) user else null
    }

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun getUserByUsername(username: String): UserEntity? = userDao.getUserByUsername(username)

    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)

    suspend fun deleteUser(username: String) = userDao.deleteUserByUsername(username)

    fun getAllEncomiendas(): Flow<List<EncomiendaEntity>> = encomiendaDao.getAllEncomiendas()

    suspend fun getEncomiendaById(id: String): EncomiendaEntity? = encomiendaDao.getEncomiendaById(id)

    suspend fun insertEncomienda(encomienda: EncomiendaEntity) = encomiendaDao.insertEncomienda(encomienda)

    suspend fun updateEncomienda(encomienda: EncomiendaEntity) = encomiendaDao.updateEncomienda(encomienda)

    suspend fun deleteEncomienda(encomienda: EncomiendaEntity) = encomiendaDao.deleteEncomienda(encomienda)

    suspend fun generateNextEncomiendaCode(): String {
        val count = encomiendaDao.getCount()
        val nextNumber = count + 1
        return "ENC-${String.format("%05d", nextNumber)}"
    }
}
