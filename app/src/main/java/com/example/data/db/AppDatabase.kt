package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.InspectionRecord
import com.example.data.model.MetrologyRuleEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ScanEntity
import com.example.data.model.SyncQueueEntity
import com.example.data.model.ViolationEntity

@Database(
    entities = [
        InspectionRecord::class,
        MetrologyRuleEntity::class,
        SyncQueueEntity::class,
        ProductEntity::class,
        ScanEntity::class,
        ViolationEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inspectionDao(): InspectionDao
    abstract fun metrologyRuleDao(): MetrologyRuleDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun productDao(): ProductDao
    abstract fun scanDao(): ScanDao
    abstract fun violationDao(): ViolationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "legal_metrology_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
