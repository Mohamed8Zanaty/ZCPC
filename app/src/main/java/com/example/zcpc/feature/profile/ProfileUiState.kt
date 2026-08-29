package com.example.zcpc.feature.profile

import com.example.zcpc.domain.model.UserProfile

sealed interface ProfileUiState {
    data object Initial : ProfileUiState
    data object Loading : ProfileUiState
    data class Success(
        val profile: UserProfile,
        val isRefreshing: Boolean = false
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}