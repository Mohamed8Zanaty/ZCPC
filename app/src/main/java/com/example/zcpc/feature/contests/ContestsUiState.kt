package com.example.zcpc.feature.contests

import com.example.zcpc.domain.model.Contest

sealed interface ContestsUiState {
    data object Loading : ContestsUiState
    data class Success(val contests: List<Contest>) : ContestsUiState
    data class Error(val message: String) : ContestsUiState
}