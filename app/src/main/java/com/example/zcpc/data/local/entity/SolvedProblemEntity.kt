package com.example.zcpc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zcpc.domain.model.SolvedProblem

@Entity(tableName = "solved_problems")
data class SolvedProblemEntity(
    @PrimaryKey val name: String,
    val rating: Int,
    val tags: String
)

fun SolvedProblemEntity.toDomain(): SolvedProblem {
    return SolvedProblem(
        name = name,
        rating = rating,
        tags = if (tags.isBlank()) emptyList() else tags.split(",")
    )
}

fun SolvedProblem.toEntity(): SolvedProblemEntity {
    return SolvedProblemEntity(
        name = name,
        rating = rating,
        tags = tags.joinToString(separator = ",")
    )
}