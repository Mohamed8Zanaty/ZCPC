package com.example.zcpc.feature.rivals

import com.example.zcpc.domain.model.Submission
import com.example.zcpc.domain.model.UserProfile

sealed interface RivalsUiState {
    data object Loading : RivalsUiState
    data class Success(
        val rivals: List<UserProfile>,
        val rivalsFailedProblems: Map<String, List<Submission>> = emptyMap(),
        val isSearching: Boolean = false,
        val errorMessage: String? = null
    ) : RivalsUiState
}
