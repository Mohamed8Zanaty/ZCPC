package com.example.zcpc.domain.model

enum class ContestPhase {
    UPCOMING,
    RUNNING,
    FINISHED,
    UNKNOWN
}
data class Contest(
    val id: Int,
    val name: String,
    val phase: ContestPhase,
    val durationSeconds: Long,
    val startTimeSeconds: Long
)
