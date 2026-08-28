package com.example.zcpc.data.mapper

import com.example.zcpc.data.codeforces.remote.dto.UserDto
import com.example.zcpc.domain.model.UserProfile

fun UserDto.toDomain(): UserProfile {
    return UserProfile(
        handle = this.handle,
        currentRating = this.rating ?: 0,
        maxRating = this.maxRating ?: 0,
        rank = this.rank?.replaceFirstChar { it.uppercase() } ?: "Unrated",
        maxRank = this.maxRank?.replaceFirstChar { it.uppercase() } ?: "Unrated",
        avatarUrl = this.avatar
    )
}