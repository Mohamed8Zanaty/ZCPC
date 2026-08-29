package com.example.zcpc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.zcpc.data.local.entity.ContestEntity

@Dao
interface ContestDao {

    @Query("SELECT * FROM contests ORDER BY startTimeSeconds ASC")
    suspend fun getAllContests(): List<ContestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContests(contests: List<ContestEntity>)

    @Query("DELETE FROM contests")
    suspend fun deleteAllContests()

    @Transaction
    suspend fun clearAndInsertContests(contests: List<ContestEntity>) {
        deleteAllContests()
        insertContests(contests)
    }
}