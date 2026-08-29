package com.example.zcpc.data.mapper

import com.example.zcpc.data.codeforces.remote.dto.ContestDto
import com.example.zcpc.domain.model.Contest
import com.example.zcpc.domain.model.ContestPhase

fun ContestDto.toDomain(): Contest {
    val phaseEnum = when(this.phase) {
        "BEFORE" -> ContestPhase.UPCOMING
        "CODING" -> ContestPhase.RUNNING
        "FINISHED" -> ContestPhase.FINISHED
        else -> ContestPhase.UNKNOWN
    }
    return Contest(
        id = this.id,
        name = this.name,
        phase = phaseEnum,
        durationSeconds = this.durationSeconds,
        startTimeSeconds = this.startTimeSeconds ?: 0L
    )
}