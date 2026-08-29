package com.example.zcpc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.zcpc.data.local.dao.ContestDao
import com.example.zcpc.data.local.dao.ProblemDao
import com.example.zcpc.data.local.dao.UserDao
import com.example.zcpc.data.local.entity.ContestEntity
import com.example.zcpc.data.local.entity.SolvedProblemEntity
import com.example.zcpc.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ContestEntity::class,
        SolvedProblemEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val contestDao: ContestDao
    abstract val problemDao: ProblemDao
}