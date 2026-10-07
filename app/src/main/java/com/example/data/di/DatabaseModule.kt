package com.example.data.di

import android.content.Context
import com.example.data.api.BackendSyncService
import com.example.data.db.InspectionDao
import com.example.data.db.MetrologyRuleDao
import com.example.data.db.SyncQueueDao
import com.example.data.local.AppDatabase
import com.example.data.local.ProductDao
import com.example.data.local.ScanDao
import com.example.data.local.ViolationDao
import com.example.data.repository.InspectionRepository
import com.example.data.repository.MetrologyRuleRepository
import com.example.data.repository.ScanRepository
import com.example.data.repository.SyncRepository

/**
 * Dependency Injection / Service Locator Module for database lifecycle management.
 * Provides singleton instances of [AppDatabase], DAOs, and Repositories.
 */
object DatabaseModule {

    @Volatile
    private var databaseInstance: AppDatabase? = null

    @Volatile
    private var scanRepositoryInstance: ScanRepository? = null

    @Volatile
    private var syncRepositoryInstance: SyncRepository? = null

    @Volatile
    private var backendSyncServiceInstance: BackendSyncService? = null

    @Volatile
    private var inspectionRepositoryInstance: InspectionRepository? = null

    @Volatile
    private var metrologyRuleRepositoryInstance: MetrologyRuleRepository? = null

    /**
     * Provides the thread-safe singleton instance of [AppDatabase].
     */
    fun provideDatabase(context: Context): AppDatabase {
        return databaseInstance ?: synchronized(this) {
            databaseInstance ?: AppDatabase.getInstance(context).also {
                databaseInstance = it
            }
        }
    }

    /**
     * Provides [ProductDao] instance.
     */
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    /**
     * Provides [ScanDao] instance.
     */
    fun provideScanDao(database: AppDatabase): ScanDao = database.scanDao()

    /**
     * Provides [ViolationDao] instance.
     */
    fun provideViolationDao(database: AppDatabase): ViolationDao = database.violationDao()

    /**
     * Provides [InspectionDao] instance.
     */
    fun provideInspectionDao(database: AppDatabase): InspectionDao = database.inspectionDao()

    /**
     * Provides [MetrologyRuleDao] instance.
     */
    fun provideMetrologyRuleDao(database: AppDatabase): MetrologyRuleDao = database.metrologyRuleDao()

    /**
     * Provides [SyncQueueDao] instance.
     */
    fun provideSyncQueueDao(database: AppDatabase): SyncQueueDao = database.syncQueueDao()

    /**
     * Provides the singleton [ScanRepository] instance.
     */
    fun provideScanRepository(context: Context): ScanRepository {
        return scanRepositoryInstance ?: synchronized(this) {
            scanRepositoryInstance ?: run {
                val db = provideDatabase(context)
                ScanRepository(
                    scanDao = db.scanDao(),
                    productDao = db.productDao(),
                    violationDao = db.violationDao()
                ).also { scanRepositoryInstance = it }
            }
        }
    }

    /**
     * Provides the singleton [InspectionRepository] instance.
     */
    fun provideInspectionRepository(context: Context): InspectionRepository {
        return inspectionRepositoryInstance ?: synchronized(this) {
            inspectionRepositoryInstance ?: run {
                val db = provideDatabase(context)
                InspectionRepository(db.inspectionDao()).also {
                    inspectionRepositoryInstance = it
                }
            }
        }
    }

    /**
     * Provides the singleton [MetrologyRuleRepository] instance.
     */
    fun provideMetrologyRuleRepository(context: Context): MetrologyRuleRepository {
        return metrologyRuleRepositoryInstance ?: synchronized(this) {
            metrologyRuleRepositoryInstance ?: run {
                val db = provideDatabase(context)
                MetrologyRuleRepository(db.metrologyRuleDao()).also {
                    metrologyRuleRepositoryInstance = it
                }
            }
        }
    }

    /**
     * Provides [BackendSyncService] instance.
     */
    fun provideBackendSyncService(): BackendSyncService {
        return backendSyncServiceInstance ?: synchronized(this) {
            backendSyncServiceInstance ?: BackendSyncService.create().also {
                backendSyncServiceInstance = it
            }
        }
    }

    /**
     * Provides the singleton [SyncRepository] instance for WorkManager tasks and online synchronization.
     */
    fun provideSyncRepository(context: Context): SyncRepository {
        return syncRepositoryInstance ?: synchronized(this) {
            syncRepositoryInstance ?: run {
                val db = provideDatabase(context)
                val backendService = provideBackendSyncService()
                SyncRepository(
                    scanDao = db.scanDao(),
                    syncQueueDao = db.syncQueueDao(),
                    backendSyncService = backendService,
                    context = context.applicationContext
                ).also { syncRepositoryInstance = it }
            }
        }
    }

    /**
     * Tears down database instances when releasing resources.
     */
    fun resetDatabase() {
        synchronized(this) {
            databaseInstance?.let { db ->
                if (db.isOpen) {
                    db.close()
                }
            }
            databaseInstance = null
            scanRepositoryInstance = null
            syncRepositoryInstance = null
            backendSyncServiceInstance = null
            inspectionRepositoryInstance = null
            metrologyRuleRepositoryInstance = null
        }
    }
}
