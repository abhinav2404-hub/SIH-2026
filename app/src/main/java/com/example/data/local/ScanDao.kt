package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Scans, with transactional support for atomic offline-first persistence.
 */
@Dao
abstract class ScanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertScan(scan: ScanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertScans(scans: List<ScanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertProductInternal(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertViolationsInternal(violations: List<ViolationEntity>)

    @Update
    abstract suspend fun updateScan(scan: ScanEntity)

    @Query("SELECT * FROM scans WHERE scanId = :scanId LIMIT 1")
    abstract suspend fun getScanById(scanId: String): ScanEntity?

    @Transaction
    @Query("SELECT * FROM scans WHERE scanId = :scanId LIMIT 1")
    abstract fun getScanWithDetailsFlow(scanId: String): Flow<ScanWithDetails?>

    @Transaction
    @Query("SELECT * FROM scans WHERE scanId = :scanId LIMIT 1")
    abstract suspend fun getScanWithDetails(scanId: String): ScanWithDetails?

    @Transaction
    @Query("SELECT * FROM scans WHERE scanId = :scanId LIMIT 1")
    abstract fun getScanWithViolationsFlow(scanId: String): Flow<ScanWithViolations?>

    @Transaction
    @Query("SELECT * FROM scans WHERE scanId = :scanId LIMIT 1")
    abstract suspend fun getScanWithViolations(scanId: String): ScanWithViolations?

    @Query("SELECT * FROM scans ORDER BY timestamp DESC")
    abstract fun getAllScansFlow(): Flow<List<ScanEntity>>

    @Transaction
    @Query("SELECT * FROM scans ORDER BY timestamp DESC")
    abstract fun getAllScansWithDetailsFlow(): Flow<List<ScanWithDetails>>

    @Transaction
    @Query("SELECT * FROM scans ORDER BY timestamp DESC")
    abstract fun getAllScansWithViolationsFlow(): Flow<List<ScanWithViolations>>

    @Query("SELECT * FROM scans WHERE syncStatus = 'PENDING' ORDER BY timestamp ASC")
    abstract fun getPendingSyncScansFlow(): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scans WHERE syncStatus = 'PENDING' ORDER BY timestamp ASC")
    abstract suspend fun getPendingSyncScans(): List<ScanEntity>

    @Query("UPDATE scans SET syncStatus = :syncStatus WHERE scanId = :scanId")
    abstract suspend fun updateSyncStatus(scanId: String, syncStatus: String)

    @Query("SELECT COUNT(*) FROM scans")
    abstract fun getScanCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM scans WHERE overallStatus = 'PASS'")
    abstract fun getCompliantScanCountFlow(): Flow<Int>

    @Delete
    abstract suspend fun deleteScan(scan: ScanEntity)

    @Query("DELETE FROM scans WHERE scanId = :scanId")
    abstract suspend fun deleteScanById(scanId: String)

    /**
     * Transactional insertion ensuring the Scan, associated Product record,
     * and any detected Violation records are written atomically into the database.
     */
    @Transaction
    open suspend fun insertScanTransaction(
        scan: ScanEntity,
        product: ProductEntity? = null,
        violations: List<ViolationEntity> = emptyList()
    ) {
        if (product != null) {
            insertProductInternal(product)
        }
        insertScan(scan)
        if (violations.isNotEmpty()) {
            insertViolationsInternal(violations)
        }
    }
}
