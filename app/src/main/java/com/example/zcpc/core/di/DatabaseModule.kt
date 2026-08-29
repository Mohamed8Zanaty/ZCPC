package com.example.zcpc.core.di

import android.content.Context
import androidx.room.Room
import com.example.zcpc.data.local.AppDatabase
import com.example.zcpc.data.local.dao.ContestDao
import com.example.zcpc.data.local.dao.ProblemDao
import com.example.zcpc.data.local.dao.RivalDao
import com.example.zcpc.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "zcpc_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao
    }

    @Provides
    @Singleton
    fun provideContestDao(appDatabase: AppDatabase): ContestDao {
        return appDatabase.contestDao
    }
    @Provides
    @Singleton
    fun provideProblemDao(appDatabase: AppDatabase): ProblemDao {
        return appDatabase.problemDao
    }
    @Provides
    @Singleton
    fun provideRivalDao(appDatabase: AppDatabase): RivalDao {
        return appDatabase.rivalDao
    }
}