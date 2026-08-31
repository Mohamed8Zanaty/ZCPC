package com.example.zcpc.feature.problems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zcpc.core.datastore.UserPreferences
import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.domain.model.SolvedProblem
import com.example.zcpc.domain.repository.CodeforcesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
        _uiState.update { 
            if (it is ProblemsUiState.Success) it.copy(isRefreshing = true) else ProblemsUiState.Loading 
        }
        viewModelScope.launch {
            val solvedResult = repository.getSolvedProblems(handle)
            val submissionsResult = repository.getSubmissions(handle)

            if (solvedResult is NetworkResult.Success && submissionsResult is NetworkResult.Success) {
                val solvedProblems = solvedResult.data
                val allSubmissions = submissionsResult.data

                val solvedProblemNames = solvedProblems.map { it.name }.toSet()
                val failedProblems = allSubmissions
                    .filter { it.verdict != "OK" && it.verdict != "TESTING" && it.name !in solvedProblemNames }
                    .distinctBy { it.name }

                val tagFrequencies = solvedProblems
                    .flatMap { it.tags }
                    .groupingBy { it }
                    .eachCount()
                    .toList()
                    .sortedByDescending { it.second }

                val ratingDist = solvedProblems
                    .filter { it.rating > 0 }
                    .groupingBy { it.rating }
                    .eachCount()
                    .toList()
                    .sortedBy { it.first }

                val groupedByTag = mutableMapOf<String, MutableList<SolvedProblem>>()
                solvedProblems.forEach { problem ->
                    problem.tags.forEach { tag ->
                        groupedByTag.getOrPut(tag) { mutableListOf() }.add(problem)
                    }
                }
                val sortedProblemsByTag = groupedByTag.mapValues { (_, problemList) ->
                    problemList.sortedByDescending { it.rating }
                }

                _uiState.update {
                    ProblemsUiState.Success(
                        totalSolved = solvedProblems.size,
                        tagCounts = tagFrequencies,
                        ratingDistribution = ratingDist,
                        problemsByTag = sortedProblemsByTag,
                        failedProblems = failedProblems,
                        isRefreshing = false
                    )
                }
            } else {
                val errorMessage = when {
                    solvedResult is NetworkResult.Error -> solvedResult.message
                    submissionsResult is NetworkResult.Error -> submissionsResult.message
                    solvedResult is NetworkResult.Exception -> solvedResult.e.localizedMessage
                    submissionsResult is NetworkResult.Exception -> submissionsResult.e.localizedMessage
                    else -> "Unknown error"
                } ?: "Error"
                _uiState.update { ProblemsUiState.Error(errorMessage) }
            }
        }
    }

    fun refreshProblems() {
        viewModelScope.launch {
            val handle = userPreferences.userHandleFlow.first()
            if (!handle.isNullOrBlank()) {
                loadProblems(handle)
            }
        }
    }

}