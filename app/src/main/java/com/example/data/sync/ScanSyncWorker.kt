package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.di.DatabaseModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Background WorkManager worker that monitors Room for scans with `syncStatus: PENDING`,
 * uploads them via [SyncRepository] to the backend API, and updates their `syncStatus` to `SYNCED`.
 */
class ScanSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("ScanSyncWorker", "Executing background scan sync worker...")
        try {
            val syncRepository = DatabaseModule.provideSyncRepository(applicationContext)
            val result = syncRepository.syncPendingScans()

            Log.d(
                "ScanSyncWorker",
                "Sync completed: processed ${result.totalProcessed}, success: ${result.successfulCount}, failed: ${result.failedCount}"
            )

            if (result.failedCount > 0 && result.successfulCount == 0) {
                // If all attempts failed, request WorkManager retry with backoff
                Result.retry()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Log.e("ScanSyncWorker", "Error executing scan sync worker", e)
            Result.retry()
        }
    }
}
