package com.example.zcpc.feature.contests

import com.example.zcpc.domain.model.Contest
import com.example.zcpc.domain.model.ContestPhase


enum class ContestFilter {
    ALL, UPCOMING, RUNNING
}
sealed interface ContestsUiState {
    data object Loading : ContestsUiState
    data class Success(
        val contests: List<Contest>,
        val currentFilter: ContestFilter = ContestFilter.ALL
    ) : ContestsUiState {
        val filteredContests: List<Contest>
            get() = when(currentFilter){
                ContestFilter.ALL -> contests
                ContestFilter.UPCOMING -> contests.filter { it.phase == ContestPhase.UPCOMING }
                ContestFilter.RUNNING -> contests.filter { it.phase == ContestPhase.RUNNING }
            }
    }
    data class Error(val message: String) : ContestsUiState
}