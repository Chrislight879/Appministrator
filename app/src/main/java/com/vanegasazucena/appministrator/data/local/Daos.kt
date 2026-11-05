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

    @Query("SELECT COUNT(*) FROM encomiendas")
    suspend fun getCount(): Int
}
