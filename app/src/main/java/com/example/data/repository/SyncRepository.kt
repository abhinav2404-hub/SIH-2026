package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.data.api.BackendSyncService
import com.example.data.db.SyncQueueDao
import com.example.data.local.ScanDao
import com.example.data.local.ScanEntity
import com.example.data.local.ScanWithDetails
import com.example.data.sync.ScanSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Result metrics for a sync batch execution.
 */
data class SyncBatchResult(
    val totalProcessed: Int,
    val successfulCount: Int,
    val failedCount: Int,
    val errorMessage: String? = null
)

/**
 * Repository orchestrating background and immediate synchronization of inspection scans.
 * Monitors Room for scans and queue items with `syncStatus: PENDING`, uploads them to the backend API,
 * and marks their status as `SYNCED` upon success.
 */
class SyncRepository(
    private val scanDao: ScanDao,
    private val syncQueueDao: SyncQueueDao,
    private val backendSyncService: BackendSyncService,
    private val context: Context
) {
    companion object {
        const val TAG = "SyncRepository"
        const val WORK_NAME_PERIODIC = "LegalMetrologyPeriodicSync"
        const val WORK_NAME_IMMEDIATE = "LegalMetrologyImmediateSync"
    }

    private val workManager: WorkManager by lazy { WorkManager.getInstance(context) }

    /**
     * Flow emitting the list of all pending scans stored in Room.
     */
    val pendingScansFlow: Flow<List<ScanEntity>> = scanDao.getPendingSyncScansFlow()

    /**
     * Flow emitting the count of pending scans in Room.
     */
    val pendingScansCountFlow: Flow<Int> = scanDao.getPendingSyncScansFlow().map { it.size }

    /**
     * Flow observing the status of WorkManager synchronization tasks.
     */
    val syncWorkInfoFlow: Flow<List<WorkInfo>> =
        workManager.getWorkInfosForUniqueWorkFlow(WORK_NAME_IMMEDIATE)

    /**
     * Schedules periodic background synchronization with network connectivity constraints.
     */
    fun schedulePeriodicSync(repeatIntervalMinutes: Long = 15) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<ScanSyncWorker>(
            repeatIntervalMinutes,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    /**
     * Triggers an immediate one-time WorkManager synchronization task.
     */
    fun triggerImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeRequest = OneTimeWorkRequestBuilder<ScanSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME_IMMEDIATE,
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest
        )
    }

    /**
     * Core synchronization pipeline executed by [ScanSyncWorker] or called directly.
     * Fetches all pending items from both `syncQueueDao` and `scanDao` in Room,
     * uploads them to the backend API, and updates their `syncStatus` in Room to `SYNCED`.
     */
    suspend fun syncPendingScans(): SyncBatchResult = withContext(Dispatchers.IO) {
        val pendingQueueItems = syncQueueDao.getPendingItems()
        val pendingScans = scanDao.getPendingSyncScans()

        if (pendingQueueItems.isEmpty() && pendingScans.isEmpty()) {
            Log.d(TAG, "No pending scans or queue items to synchronize.")
            return@withContext SyncBatchResult(0, 0, 0)
        }

        var successCount = 0
        var failCount = 0
        var lastError: String? = null
        val processedScanIds = mutableSetOf<String>()

        // 1. Process Offline Sync Queue Items
        for (queueItem in pendingQueueItems) {
            processedScanIds.add(queueItem.scanId)
            try {
                syncQueueDao.updateStatusByScanId(queueItem.scanId, "SYNCING")
                val isUploaded = backendSyncService.uploadJsonPayload(
                    scanId = queueItem.scanId,
                    payloadJson = queueItem.payloadJson
                )

                if (isUploaded) {
                    syncQueueDao.updateStatusByScanId(queueItem.scanId, "SYNCED")
                    scanDao.updateSyncStatus(queueItem.scanId, "SYNCED")
                    successCount++
                    Log.d(TAG, "Queue item for scan ${queueItem.scanId} successfully synced to backend.")
                } else {
                    syncQueueDao.updateStatusByScanId(queueItem.scanId, "FAILED", "Backend rejected queue payload")
                    scanDao.updateSyncStatus(queueItem.scanId, "FAILED")
                    failCount++
                    lastError = "Backend rejected payload for ${queueItem.scanId}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during upload of queue item for scan ${queueItem.scanId}", e)
                syncQueueDao.updateStatusByScanId(queueItem.scanId, "FAILED", e.localizedMessage)
                scanDao.updateSyncStatus(queueItem.scanId, "FAILED")
                failCount++
                lastError = e.localizedMessage
            }
        }

        // 2. Process Remaining Pending Scans
        for (scan in pendingScans) {
            if (processedScanIds.contains(scan.scanId)) continue

            try {
                val scanWithDetails = scanDao.getScanWithDetails(scan.scanId)
                    ?: ScanWithDetails(scan = scan, product = null, violations = emptyList())

                val isUploaded = backendSyncService.uploadScan(scanWithDetails)

                if (isUploaded) {
                    scanDao.updateSyncStatus(scan.scanId, "SYNCED")
                    syncQueueDao.updateStatusByScanId(scan.scanId, "SYNCED")
                    successCount++
                    Log.d(TAG, "Scan ${scan.scanId} successfully synced to backend.")
                } else {
                    scanDao.updateSyncStatus(scan.scanId, "FAILED")
                    syncQueueDao.updateStatusByScanId(scan.scanId, "FAILED", "Backend rejected scan details")
                    failCount++
                    lastError = "Backend rejected payload for ${scan.scanId}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during upload of scan ${scan.scanId}", e)
                scanDao.updateSyncStatus(scan.scanId, "FAILED")
                syncQueueDao.updateStatusByScanId(scan.scanId, "FAILED", e.localizedMessage)
                failCount++
                lastError = e.localizedMessage
            }
        }

        val totalProcessed = pendingQueueItems.size + pendingScans.filterNot { processedScanIds.contains(it.scanId) }.size

        SyncBatchResult(
            totalProcessed = totalProcessed,
            successfulCount = successCount,
            failedCount = failCount,
            errorMessage = lastError
        )
    }
}
