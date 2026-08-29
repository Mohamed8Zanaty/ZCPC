package com.example.zcpc.feature.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zcpc.core.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    fun saveHandle(handle: String) {
        viewModelScope.launch {
            userPreferences.saveHandle(handle.trim())
        }
    }
}