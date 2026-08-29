package com.example.zcpc.feature.problems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zcpc.core.datastore.UserPreferences
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
class ProblemsViewModel @Inject constructor(
    private val repository: CodeforcesRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProblemsUiState>(ProblemsUiState.Loading)
    val uiState: StateFlow<ProblemsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.userHandleFlow.collect { handle ->
                if(handle?.isNotBlank() ?: false) loadProblems(handle)
            }
        }
    }

    fun loadProblems(handle: String) {
        _uiState.update { ProblemsUiState.Loading }
        viewModelScope.launch {
            when(val result = repository.getSolvedProblems(handle)) {
                is NetworkResult.Success -> {
                    val problems = result.data

                    val tagFrequencies = problems
                        .flatMap { it.tags }
                        .groupingBy { it }
                        .eachCount()
                        .toList()
                        .sortedByDescending { it.second }

                    _uiState.update {
                        ProblemsUiState.Success(
                            totalSolved = problems.size,
                            tagCounts = tagFrequencies
                        )
                    }

                }
                is NetworkResult.Error -> _uiState.update { ProblemsUiState.Error(result.message) }
                is NetworkResult.Exception -> _uiState.update { ProblemsUiState.Error(result.e.localizedMessage ?: "Error") }
            }
        }
    }
}