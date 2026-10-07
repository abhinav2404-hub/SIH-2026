package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.db.InspectionDao
import com.example.data.db.MetrologyRuleDao
import com.example.data.db.SyncQueueDao
import com.example.data.local.migration.DatabaseMigrations
import com.example.data.model.InspectionRecord
import com.example.data.model.MetrologyRuleEntity
import com.example.data.model.SyncQueueEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Abstract Room Database class for Mudra Check / Legal Metrology local storage.
 * Manages database lifecycle, migrations, and table entities.
 */
@Database(
    entities = [
        InspectionRecord::class,
        MetrologyRuleEntity::class,
        SyncQueueEntity::class,
        ProductEntity::class,
        ScanEntity::class,
        ViolationEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun scanDao(): ScanDao
    abstract fun violationDao(): ViolationDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun metrologyRuleDao(): MetrologyRuleDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        const val DATABASE_NAME = "legal_metrology_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the thread-safe singleton instance of [AppDatabase].
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        /**
         * Builds the Room Database instance with proper migration strategies
         * and lifecycle callbacks.
         */
        fun buildDatabase(
            context: Context,
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Trigger async initial setup if necessary
                        coroutineScope.launch {
                            // Room database initialized
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Enable foreign key constraints in SQLite
                        db.execSQL("PRAGMA foreign_keys = ON;")
                    }
                })
                .build()
        }

        /**
         * Closes the database instance and clears the singleton reference.
         */
        fun closeDatabase() {
            synchronized(this) {
                INSTANCE?.let { db ->
                    if (db.isOpen) {
                        db.close()
                    }
                    INSTANCE = null
                }
            }
        }
    }
}
