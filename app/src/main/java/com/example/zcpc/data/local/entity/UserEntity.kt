package com.example.zcpc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zcpc.domain.model.UserProfile

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val handle: String,
    val currentRating: Int,
    val maxRating: Int,
    val rank: String,
    val maxRank: String,
    val avatarUrl: String
)

fun UserEntity.toDomain(): UserProfile {
    return UserProfile(
        handle = handle,
        currentRating = currentRating,
        maxRating = maxRating,
        rank = rank,
        maxRank = maxRank,
        avatarUrl = avatarUrl
    )
}

fun UserProfile.toEntity(): UserEntity {
    return UserEntity(
        handle = handle,
        currentRating = currentRating,
        maxRating = maxRating,
        rank = rank,
        maxRank = maxRank,
        avatarUrl = avatarUrl
    )
}
