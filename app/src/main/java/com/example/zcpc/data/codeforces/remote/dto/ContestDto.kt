package com.example.zcpc.data.codeforces.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContestDto(
    val id: Int,
    val name: String,
    val type: String,
    val phase: String,
    val durationSeconds: Long,
    val startTimeSeconds: Long? = null
)
