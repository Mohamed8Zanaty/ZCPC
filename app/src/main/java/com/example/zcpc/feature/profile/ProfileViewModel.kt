package com.example.zcpc.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.domain.repository.CodeforcesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: CodeforcesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Initial)
    val uiState : StateFlow<ProfileUiState> = _uiState.asStateFlow()

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
}