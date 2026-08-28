package com.example.zcpc.data.codeforces.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val handle: String,
    val rating: Int? = null,
    val maxRating: Int? = null,
    val rank: String? = null,
    val maxRank: String? = null,
    val avatar: String,
    val titlePhoto: String,
    val contribution: Int? = null,
    val organization: String? = null
)
