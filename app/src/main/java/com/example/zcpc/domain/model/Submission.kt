package com.example.zcpc.domain.model

data class Submission(
    val id: Int,
    val contestId: Int?,
    val index: String,
    val name: String,
    val rating: Int,
    val verdict: String?,
    val tags: List<String>
)
