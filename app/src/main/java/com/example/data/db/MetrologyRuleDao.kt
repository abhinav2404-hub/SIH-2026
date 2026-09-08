package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MetrologyRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetrologyRuleDao {
    @Query("SELECT * FROM metrology_rules ORDER BY ruleNumber ASC")
    fun getAllRules(): Flow<List<MetrologyRuleEntity>>

    @Query("SELECT * FROM metrology_rules WHERE ruleId = :ruleId LIMIT 1")
    suspend fun getRuleById(ruleId: String): MetrologyRuleEntity?

    @Query("SELECT * FROM metrology_rules WHERE category = :category ORDER BY ruleNumber ASC")
    fun getRulesByCategory(category: String): Flow<List<MetrologyRuleEntity>>

    @Query("SELECT * FROM metrology_rules WHERE isAgriSpecific = 1 ORDER BY ruleNumber ASC")
    fun getAgriSpecificRules(): Flow<List<MetrologyRuleEntity>>

    @Query("SELECT * FROM metrology_rules WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR ruleNumber LIKE '%' || :query || '%'")
    fun searchRules(query: String): Flow<List<MetrologyRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<MetrologyRuleEntity>)

    @Query("SELECT COUNT(*) FROM metrology_rules")
    suspend fun getRulesCount(): Int
}
