package com.example.zcpc.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.core.network.safeApiCall
import com.example.zcpc.core.notification.NotificationHelper
import com.example.zcpc.data.codeforces.remote.CodeforcesApi
import com.example.zcpc.data.local.dao.NotificationDao
import com.example.zcpc.data.local.dao.RivalDao
import com.example.zcpc.data.local.entity.NotificationEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.math.abs

@HiltWorker
class RivalTrackerWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val rivalDao: RivalDao,
    private val notificationDao: NotificationDao,
    private val api: CodeforcesApi
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val rivals = rivalDao.getAllRivalsSync()
        if (rivals.isEmpty()) return Result.success()

        for (rival in rivals) {
            val result = safeApiCall { api.getUserStatus(rival.handle) }

            if (result is NetworkResult.Success) {
                val submissions = result.data.result ?: emptyList()
                val latestSubmission = submissions.firstOrNull() ?: continue

                if (rival.lastSubmissionId == 0) {
                    rivalDao.insertRival(rival.copy(lastSubmissionId = latestSubmission.id))
                    continue
                }

                if (latestSubmission.id > rival.lastSubmissionId) {
                    rivalDao.insertRival(rival.copy(lastSubmissionId = latestSubmission.id))

                    if (latestSubmission.verdict == "OK") {
                        val probName = latestSubmission.problem.name
                        val contestId = latestSubmission.problem.contestId

                        val title = "⚔️ Rival Activity: ${rival.handle}"
                        val message = if (contestId != null && contestId > 100000) {
                            "${rival.handle} just solved '$probName' in Gym/Mashup #$contestId!"
                        } else {
                            "${rival.handle} just solved '$probName'!"
                        }

                        NotificationHelper.showRivalNotification(
                            context = applicationContext,
                            id = abs(rival.handle.hashCode()),
                            title = title,
                            message = message
                        )
                        notificationDao.insertNotification(
                            NotificationEntity(
                                title = title,
                                message = message,
                                rivalHandle = rival.handle
                            )
                        )
                    }
                }
            }
        }
        return Result.success()
    }
}