package com.example.zcpc.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

enum class AppTheme { SYSTEM, LIGHT, DARK }
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val HANDLE_KEY = stringPreferencesKey("codeforces_handle")
    private val THEME_KEY = stringPreferencesKey("app_theme")

    val userHandleFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[HANDLE_KEY] ?: ""
    }

    val appThemeFlow: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        when(preferences[THEME_KEY]) {
            AppTheme.LIGHT.name -> AppTheme.LIGHT
            AppTheme.DARK.name -> AppTheme.DARK
            else -> AppTheme.SYSTEM
        }
    }
    suspend fun saveHandle(handle: String) {
        context.dataStore.edit { preferences ->
            preferences[HANDLE_KEY] = handle
        }
    }

    suspend fun clearHandle() {
        context.dataStore.edit { preferences ->
            preferences.remove(HANDLE_KEY)
        }
    }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}