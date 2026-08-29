package com.example.zcpc.data.codeforces.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProblemDto(
    val contestId: Int? = null,
    val index: String,
    val name: String,
    val rating: Int? = null,
    val tags: List<String> = emptyList()
)

@Serializable
data class SubmissionDto(
    val id: Int,
    val problem: ProblemDto,
    val verdict: String? = null
)
