package com.example.data.repository

import com.example.data.local.ProductDao
import com.example.data.local.ProductEntity
import com.example.data.local.ScanDao
import com.example.data.local.ScanEntity
import com.example.data.local.ScanWithDetails
import com.example.data.local.ViolationDao
import com.example.data.local.ViolationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository orchestrating offline-first scans, products, and violations
 * with transactional guarantees.
 */
class ScanRepository(
    private val scanDao: ScanDao,
    private val productDao: ProductDao,
    private val violationDao: ViolationDao
) {

    val allScansWithDetails: Flow<List<ScanWithDetails>> = scanDao.getAllScansWithDetailsFlow()
    val pendingSyncScans: Flow<List<ScanEntity>> = scanDao.getPendingSyncScansFlow()
    val totalScanCount: Flow<Int> = scanDao.getScanCountFlow()
    val compliantScanCount: Flow<Int> = scanDao.getCompliantScanCountFlow()

    fun getScanWithDetailsFlow(scanId: String): Flow<ScanWithDetails?> =
        scanDao.getScanWithDetailsFlow(scanId)

    suspend fun getScanWithDetails(scanId: String): ScanWithDetails? =
        scanDao.getScanWithDetails(scanId)

    /**
     * Atomically saves an entire scan operation (Scan + Product + Violations)
     * inside a single SQLite transaction.
     */
    suspend fun saveScanTransaction(
        scan: ScanEntity,
        product: ProductEntity? = null,
        violations: List<ViolationEntity> = emptyList()
    ) {
        scanDao.insertScanTransaction(scan, product, violations)
    }

    suspend fun markScanAsSynced(scanId: String) {
        scanDao.updateSyncStatus(scanId, "SYNCED")
    }

    suspend fun markScanAsFailed(scanId: String) {
        scanDao.updateSyncStatus(scanId, "FAILED")
    }

    suspend fun deleteScan(scanId: String) {
        scanDao.deleteScanById(scanId)
    }
}
