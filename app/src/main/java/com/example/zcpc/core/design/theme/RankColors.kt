package com.example.zcpc.core.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class RankColors(
    val newbie: Color = RankGray,
    val pupil: Color = RankGreen,
    val specialist: Color = RankCyan,
    val expert: Color = RankBlue,
    val candidateMaster: Color = RankViolet,
    val master: Color = RankOrange,
    val grandmaster: Color = RankRed,
)
val LocalRankColors = staticCompositionLocalOf { RankColors() }
