package com.example.zcpc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zcpc.data.local.entity.RivalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RivalDao {
    @Query("SELECT * FROM rivals ORDER BY currentRating DESC")
    fun getAllRivalsFlow(): Flow<List<RivalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRival(rival: RivalEntity)

    @Delete
    suspend fun deleteRival(rival: RivalEntity)

    @Query("SELECT * FROM rivals")
    suspend fun getAllRivalsSync(): List<RivalEntity>
}