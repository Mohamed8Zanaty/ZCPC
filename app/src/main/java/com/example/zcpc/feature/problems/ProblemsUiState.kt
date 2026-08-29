package com.example.zcpc.feature.problems

import com.example.zcpc.domain.model.SolvedProblem

sealed interface ProblemsUiState {
    data object Loading : ProblemsUiState
    data class Success(
        val totalSolved: Int,
        val tagCounts: List<Pair<String, Int>>,
        val ratingDistribution: List<Pair<Int, Int>>,
        val problemsByTag: Map<String, List<SolvedProblem>>,
        val isRefreshing: Boolean = false
    ) : ProblemsUiState
    data class Error(val message: String) : ProblemsUiState
}