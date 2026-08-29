package com.example.zcpc.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zcpc.core.datastore.AppTheme
import com.example.zcpc.core.datastore.UserPreferences
import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.domain.repository.CodeforcesRepository
import com.example.zcpc.feature.contests.ContestFilter
import com.example.zcpc.feature.contests.ContestsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: CodeforcesRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Initial)
    val uiState : StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val currentTheme: StateFlow<AppTheme> = userPreferences.appThemeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    init {
        viewModelScope.launch {
            userPreferences.userHandleFlow.collect { savedHandle ->
                if(!savedHandle.isNullOrBlank()) {
                    loadProfile(savedHandle)
                }
            }
        }
    }
    fun loadProfile(handle: String) {
        if(_uiState.value is ProfileUiState.Loading) return
        _uiState.update { ProfileUiState.Loading }

        viewModelScope.launch {
            when (val result = repository.getUserProfile(handle)) {
                is NetworkResult.Success -> {
                    _uiState.update { ProfileUiState.Success(result.data) }

                }
                is NetworkResult.Error -> {
                    _uiState.update { ProfileUiState.Error(result.message) }
                }
                is NetworkResult.Exception -> {
                    _uiState.update { ProfileUiState.Error(result.e.localizedMessage ?: "Unknown error occurred") }
                }
            }
        }
    }
    fun clearHandle() {
        viewModelScope.launch {
            userPreferences.clearHandle()
            _uiState.update { ProfileUiState.Initial }
        }
    }
    fun refreshProfile(handle: String) {
        val currentState = _uiState.value

        if(currentState is ProfileUiState.Success) {
            if(currentState.isRefreshing) return
            _uiState.update { currentState.copy(isRefreshing = true) }
        } else {
            _uiState.update { ProfileUiState.Loading }
        }

        viewModelScope.launch {
            when(val result = repository.getUserProfile(handle)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        ProfileUiState.Success(
                            profile = result.data,
                            isRefreshing = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { ProfileUiState.Error(result.message) }
                }
                is NetworkResult.Exception -> {
                    _uiState.update { ProfileUiState.Error(result.e.localizedMessage ?: "Error") }
                }
            }
        }
    }

    fun setAppTheme(theme: AppTheme) {
        viewModelScope.launch {
            userPreferences.saveTheme(theme)
        }
    }
}