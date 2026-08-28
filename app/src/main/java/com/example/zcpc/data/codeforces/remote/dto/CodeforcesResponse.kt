package com.example.zcpc.data.codeforces.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CodeforcesResponse<T>(
    val status: String,
    val result: T? = null,
    val comment: String? = null
)
