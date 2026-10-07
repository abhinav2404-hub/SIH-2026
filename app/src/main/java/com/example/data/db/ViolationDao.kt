package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ViolationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ViolationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolation(violation: ViolationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViolations(violations: List<ViolationEntity>)

    @Update
    suspend fun updateViolation(violation: ViolationEntity)

    @Query("SELECT * FROM violations WHERE scanId = :scanId")
    suspend fun getViolationsForScan(scanId: String): List<ViolationEntity>

    @Query("SELECT * FROM violations WHERE scanId = :scanId")
    fun getViolationsForScanFlow(scanId: String): Flow<List<ViolationEntity>>

    @Query("SELECT * FROM violations WHERE severity = 'CRITICAL'")
    fun getCriticalViolationsFlow(): Flow<List<ViolationEntity>>

    @Query("DELETE FROM violations WHERE scanId = :scanId")
    suspend fun deleteViolationsForScan(scanId: String)

    @Delete
    suspend fun deleteViolation(violation: ViolationEntity)
}
