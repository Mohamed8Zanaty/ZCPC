package com.example.zcpc.feature.problems

import com.example.zcpc.domain.model.SolvedProblem
import com.example.zcpc.domain.model.Submission

sealed interface ProblemsUiState {
    data object Loading : ProblemsUiState
    data class Success(
        val totalSolved: Int,
        val tagCounts: List<Pair<String, Int>>,
        val ratingDistribution: List<Pair<Int, Int>>,
        val problemsByTag: Map<String, List<SolvedProblem>>,
        val failedProblems: List<Submission> = emptyList(),
        val isRefreshing: Boolean = false
    ) : ProblemsUiState
    data class Error(val message: String) : ProblemsUiState
}