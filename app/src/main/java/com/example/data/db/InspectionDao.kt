package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.InspectionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface InspectionDao {
    @Query("SELECT * FROM inspection_records ORDER BY timestamp DESC")
    fun getAllInspections(): Flow<List<InspectionRecord>>

    @Query("SELECT * FROM inspection_records WHERE id = :id")
    fun getInspectionById(id: Long): Flow<InspectionRecord?>

    @Query("SELECT * FROM inspection_records WHERE overallStatus = :status ORDER BY timestamp DESC")
    fun getInspectionsByStatus(status: String): Flow<List<InspectionRecord>>

    @Query("SELECT * FROM inspection_records WHERE productName LIKE '%' || :query || '%' OR brandName LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchInspections(query: String): Flow<List<InspectionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(record: InspectionRecord): Long

    @Update
    suspend fun updateInspection(record: InspectionRecord)

    @Query("DELETE FROM inspection_records WHERE id = :id")
    suspend fun deleteInspection(id: Long)

    @Query("DELETE FROM inspection_records")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM inspection_records")
    suspend fun getCount(): Int
}
