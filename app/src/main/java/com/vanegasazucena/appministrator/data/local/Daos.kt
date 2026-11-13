package com.vanegasazucena.appministrator.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity): Int

    @Delete
    suspend fun deleteUser(user: UserEntity): Int

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUserByUsername(username: String): Int
}

@Dao
interface EncomiendaDao {
    @Query("SELECT * FROM encomiendas ORDER BY timestampMillis DESC")
    fun getAllEncomiendas(): Flow<List<EncomiendaEntity>>

    @Query("SELECT * FROM encomiendas WHERE id = :id LIMIT 1")
    suspend fun getEncomiendaById(id: String): EncomiendaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncomienda(encomienda: EncomiendaEntity): Long

    @Update
    suspend fun updateEncomienda(encomienda: EncomiendaEntity): Int

    @Delete
    suspend fun deleteEncomienda(encomienda: EncomiendaEntity): Int

    @Query("SELECT codigo FROM encomiendas")
    suspend fun getAllCodigos(): List<String>

    @Query("SELECT * FROM encomiendas WHERE codigo = id OR codigo = '' OR codigo IS NULL")
    suspend fun getEncomiendasConCodigoAnterior(): List<EncomiendaEntity>

    @Query("UPDATE encomiendas SET codigo = :codigo WHERE id = :id")
    suspend fun actualizarCodigo(id: String, codigo: String): Int

    @Query("SELECT COUNT(*) FROM encomiendas")
    suspend fun getCount(): Int

    @Query("SELECT MAX(CAST(SUBSTR(id, 5) AS INTEGER)) FROM encomiendas WHERE id LIKE 'ENC-%'")
    suspend fun getMaxIdNumber(): Int?
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY id DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity): Long
}
