package com.vanegasazucena.appministrator.data.repository

import com.vanegasazucena.appministrator.data.local.EncomiendaDao
import com.vanegasazucena.appministrator.data.local.EncomiendaEntity
import com.vanegasazucena.appministrator.data.local.UserDao
import com.vanegasazucena.appministrator.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val encomiendaDao: EncomiendaDao
) {
    suspend fun ensureInitialData() {
        if (userDao.getUserByUsername("admin") == null) {
            // Seed initial users
            userDao.insertUser(UserEntity("admin", "admin123", "ADMIN"))
            userDao.insertUser(UserEntity("PeterParker", "user123", "COMMON"))
            userDao.insertUser(UserEntity("JuanPerez", "user123", "COMMON"))

            // Seed initial encomiendas
            encomiendaDao.insertEncomienda(
                EncomiendaEntity(
                    id = "ENC-00124",
                    emisorNombre = "Carlos Lopez",
                    emisorDui = "01234567-8",
                    remitenteNombre = "Peter Parker",
                    remitenteDui = "87654321-0",
                    creadoPor = "PeterParker",
                    codigo = "ENC-00124",
                    estado = "PAGADO",
                    tamano = "GRANDE",
                    precio = 8.00,
                    fecha = "24/09/2026"
                )
            )
            encomiendaDao.insertEncomienda(
                EncomiendaEntity(
                    id = "ENC-00123",
                    emisorNombre = "Maria Ramos",
                    emisorDui = "05432167-9",
                    remitenteNombre = "Juan Pérez",
                    remitenteDui = "98765432-1",
                    creadoPor = "PeterParker",
                    codigo = "ENC-00123",
                    estado = "PENDIENTE",
                    tamano = "MEDIANO",
                    precio = 5.00,
                    fecha = "24/09/2026"
                )
            )
            encomiendaDao.insertEncomienda(
                EncomiendaEntity(
                    id = "ENC-00122",
                    emisorNombre = "Ana Gomez",
                    emisorDui = "03456789-2",
                    remitenteNombre = "Peter Parker",
                    remitenteDui = "87654321-0",
                    creadoPor = "PeterParker",
                    codigo = "ENC-00122",
                    estado = "ENTREGADO",
                    tamano = "GRANDE",
                    precio = 8.00,
                    fecha = "11 de Septiembre 2026"
                )
            )
        }
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
        val nextNumber = count + 125
        return "ENC-${String.format("%05d", nextNumber)}"
    }
}
