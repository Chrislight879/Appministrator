package com.vanegasazucena.appministrator.data.repository

import com.vanegasazucena.appministrator.data.local.AuditLogDao
import com.vanegasazucena.appministrator.data.local.AuditLogEntity
import com.vanegasazucena.appministrator.data.local.EncomiendaDao
import com.vanegasazucena.appministrator.data.local.EncomiendaDataGenerator
import com.vanegasazucena.appministrator.data.local.EncomiendaEntity
import com.vanegasazucena.appministrator.data.local.UserDao
import com.vanegasazucena.appministrator.data.local.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(
    private val userDao: UserDao,
    private val encomiendaDao: EncomiendaDao,
    private val auditLogDao: AuditLogDao
) {
    private val codigoMutex = Mutex()
    private val random = SecureRandom()
    private val codigoCaracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    private fun nuevoCodigo(): String = buildString(7) {
        repeat(7) { append(codigoCaracteres[random.nextInt(codigoCaracteres.length)]) }
    }

    // Cambia únicamente los códigos antiguos; los ID y demás datos permanecen iguales.
    private suspend fun actualizarCodigosAnteriores() = codigoMutex.withLock {
        val existentes = encomiendaDao.getAllCodigos().toMutableSet()
        encomiendaDao.getEncomiendasConCodigoAnterior().forEach { encomienda ->
            var codigo: String
            do { codigo = nuevoCodigo() } while (codigo in existentes)
            encomiendaDao.actualizarCodigo(encomienda.id, codigo)
            existentes.add(codigo)
        }
    }

    suspend fun ensureInitialData() {
        if (userDao.getUserByUsername("admin") == null) {
            userDao.insertUser(UserEntity("admin", "admin123", "ADMIN"))
            userDao.insertUser(UserEntity("PeterParker", "user123", "COMMON"))
            userDao.insertUser(UserEntity("JuanPerez", "user123", "COMMON"))
        }

        if (encomiendaDao.getCount() == 0) {
            val list = EncomiendaDataGenerator.generate300Encomiendas()
            // Los ejemplos ya deben tener códigos diferentes de los ID.
            val usados = mutableSetOf<String>()
            list.forEach { encomienda ->
                var codigo: String
                do { codigo = nuevoCodigo() } while (codigo in usados)
                usados.add(codigo)
                encomiendaDao.insertEncomienda(encomienda.copy(codigo = codigo))
            }


        }
        actualizarCodigosAnteriores()
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

    suspend fun insertEncomienda(encomienda: EncomiendaEntity) = codigoMutex.withLock {
        val anterior = encomiendaDao.getEncomiendaById(encomienda.id)
        val codigo = if (anterior != null && anterior.codigo.isNotBlank() && anterior.codigo != anterior.id) {
            anterior.codigo // No cambiar el código cuando editamos una encomienda.
        } else {
            val ocupados = encomiendaDao.getAllCodigos().toSet()
            var generado: String
            do { generado = nuevoCodigo() } while (generado in ocupados)
            generado
        }
        encomiendaDao.insertEncomienda(encomienda.copy(codigo = codigo))
    }

    suspend fun updateEncomienda(encomienda: EncomiendaEntity) = codigoMutex.withLock {
        val anterior = encomiendaDao.getEncomiendaById(encomienda.id)
        if (anterior != null) {
            encomiendaDao.updateEncomienda(encomienda.copy(codigo = anterior.codigo))
        } else {
            0
        }
    }

    suspend fun deleteEncomienda(encomienda: EncomiendaEntity) = encomiendaDao.deleteEncomienda(encomienda)

    suspend fun generateNextEncomiendaCode(): String {
        val nextNumber = (encomiendaDao.getMaxIdNumber() ?: 0) + 1
        return "ENC-${String.format("%05d", nextNumber)}"
    }
}
