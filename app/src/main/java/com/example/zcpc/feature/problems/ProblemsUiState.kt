package com.example.zcpc.feature.problems

sealed interface ProblemsUiState {
    data object Loading : ProblemsUiState
    data class Success(
        val totalSolved: Int,
        val tagCounts: List<Pair<String, Int>>,
        val ratingDistribution: List<Pair<Int, Int>>
    ) : ProblemsUiState
    data class Error(val message: String) : ProblemsUiState
}