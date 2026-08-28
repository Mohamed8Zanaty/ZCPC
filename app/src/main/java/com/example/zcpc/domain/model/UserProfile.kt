package com.example.zcpc.domain.model

data class UserProfile(
    val handle: String,
    val currentRating: Int,
    val maxRating: Int,
    val rank: String,
    val maxRank: String,
    val avatarUrl: String
)
