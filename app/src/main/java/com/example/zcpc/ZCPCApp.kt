package com.example.zcpc

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.zcpc.core.worker.RivalTrackerWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class ZCPCApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleRivalTracker()
    }

    private fun scheduleRivalTracker() {
        val request = PeriodicWorkRequestBuilder<RivalTrackerWorker>(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "RivalTrackerWork",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        val instantTestRequest = OneTimeWorkRequestBuilder<RivalTrackerWorker>().build()
        WorkManager.getInstance(this).enqueue(instantTestRequest)
    }
}