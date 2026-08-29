package com.example.zcpc.feature.contests

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
class ContestsViewModel @Inject constructor(
    private val repository: CodeforcesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ContestsUiState>(ContestsUiState.Loading)
    val uiState: StateFlow<ContestsUiState> = _uiState.asStateFlow()

    init {
        loadContests()
    }
    fun loadContests() {
        _uiState.update { ContestsUiState.Loading }
        viewModelScope.launch {
            when (val result = repository.getContests()) {
                is NetworkResult.Success -> {
                    _uiState.update { ContestsUiState.Success(
                        contests = result.data,
                        currentFilter = ContestFilter.ALL
                    ) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { ContestsUiState.Error(result.message) }
                }
                is NetworkResult.Exception -> {
                    _uiState.update { ContestsUiState.Error(result.e.localizedMessage ?: "Error") }
                }
            }
        }
    }
    fun setFilter(filter: ContestFilter) {
        val currentState = _uiState.value
        if(currentState is ContestsUiState.Success) {
            _uiState.update { currentState.copy(currentFilter = filter) }
        }
    }
}