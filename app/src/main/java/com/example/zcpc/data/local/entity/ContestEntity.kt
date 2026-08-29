package com.example.zcpc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zcpc.domain.model.Contest
import com.example.zcpc.domain.model.ContestPhase

@Entity(tableName = "contests")
data class ContestEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val phase: String,
    val durationSeconds: Long,
    val startTimeSeconds: Long
)

fun ContestEntity.toDomain(): Contest {
    return Contest(
        id = id,
        name = name,
        phase = runCatching { ContestPhase.valueOf(phase) }.getOrDefault(ContestPhase.UNKNOWN),
        durationSeconds = durationSeconds,
        startTimeSeconds = startTimeSeconds
    )
}

fun Contest.toEntity(): ContestEntity {
    return ContestEntity(
        id = id,
        name = name,
        phase = phase.name,
        durationSeconds = durationSeconds,
        startTimeSeconds = startTimeSeconds
    )
}
