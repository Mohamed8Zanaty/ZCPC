package com.example.zcpc.feature.profile.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.zcpc.core.design.theme.rankColors

@Composable
fun getRankColor(rank: String): Color {
    val colors = MaterialTheme.rankColors
    return when (rank.lowercase()) {
        "newbie" -> colors.newbie
        "pupil" -> colors.pupil
        "specialist" -> colors.specialist
        "expert" -> colors.expert
        "candidate master" -> colors.candidateMaster
        "master", "international master" -> colors.master
        "grandmaster", "international grandmaster", "legendary grandmaster" -> colors.grandmaster
        else -> MaterialTheme.colorScheme.onSurface
    }

}