package com.example.zcpc.feature.rivals
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zcpc.core.network.NetworkResult
import com.example.zcpc.domain.model.UserProfile
import com.example.zcpc.domain.repository.CodeforcesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RivalsViewModel @Inject constructor(
    private val repository: CodeforcesRepository
) : ViewModel() {

    private val _isSearching = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<RivalsUiState> = combine(
        repository.getRivalsFlow(),
        _isSearching,
        _errorMessage
    ) { rivals, isSearching, error ->
        RivalsUiState.Success(
            rivals = rivals,
            isSearching = isSearching,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RivalsUiState.Loading
    )

    fun addRival(handle: String) {
        if (handle.isBlank()) return
        _isSearching.update { true }
        _errorMessage.update { null }

        viewModelScope.launch {
            when (val result = repository.addRival(handle.trim())) {
                is NetworkResult.Success -> {
                    _isSearching.update { false }
                }
                is NetworkResult.Error -> {
                    _isSearching.update { false }
                    _errorMessage.update { result.message }
                }
                is NetworkResult.Exception -> {
                    _isSearching.update { false }
                    _errorMessage.update { result.e.localizedMessage ?: "Unknown error" }
                }
            }
        }
    }

    fun removeRival(profile: UserProfile) {
        viewModelScope.launch {
            repository.removeRival(profile)
        }
    }

    fun clearError() {
        _errorMessage.update { null }
    }
}