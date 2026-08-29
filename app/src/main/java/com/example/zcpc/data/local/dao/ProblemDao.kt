package com.example.zcpc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.zcpc.data.local.entity.SolvedProblemEntity

@Dao
interface ProblemDao {

    @Query("SELECT * FROM solved_problems")
    suspend fun getAllSolvedProblems(): List<SolvedProblemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProblems(problems: List<SolvedProblemEntity>)

    @Query("DELETE FROM solved_problems")
    suspend fun deleteAllProblems()

    @Transaction
    suspend fun clearAndInsertProblems(problems: List<SolvedProblemEntity>) {
        deleteAllProblems()
        insertProblems(problems)
    }
}