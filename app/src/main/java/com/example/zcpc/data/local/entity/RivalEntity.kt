package com.example.zcpc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zcpc.domain.model.UserProfile

@Entity(tableName = "rivals")
data class RivalEntity(
    @PrimaryKey val handle: String,
    val avatarUrl: String,
    val currentRating: Int,
    val maxRating: Int,
    val rank: String,
    val lastSubmissionId: Int = 0
)

fun RivalEntity.toDomain(): UserProfile {
    return UserProfile(
        handle = handle,
        avatarUrl = avatarUrl,
        currentRating = currentRating,
        maxRating = maxRating,
        rank = rank,
        maxRank = ""
    )
}

fun UserProfile.toRivalEntity(): RivalEntity {
    return RivalEntity(
        handle = handle,
        avatarUrl = avatarUrl,
        currentRating = currentRating,
        maxRating = maxRating,
        rank = rank
    )
}