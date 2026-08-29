package com.example.zcpc.domain.model

data class SolvedProblem(
    val contestId: Int?,
    val index: String,
    val name: String,
    val rating: Int,
    val tags: List<String>
)
