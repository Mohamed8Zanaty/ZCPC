package com.example.zcpc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zcpc.core.datastore.AppTheme
import com.example.zcpc.core.datastore.UserPreferences
import com.example.zcpc.core.design.theme.ZCPCTheme
import com.example.zcpc.core.navigation.MainGraph
import com.example.zcpc.core.navigation.Setup
import com.example.zcpc.feature.contests.ContestsRoute
import com.example.zcpc.feature.profile.ProfileRoute
import com.example.zcpc.feature.setup.SetupRoute
import com.example.zcpc.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferences: UserPreferences

    private var currentIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        currentIntent = intent
        setContent {
            val handle by userPreferences.userHandleFlow.collectAsState(initial = null)
            val appTheme by userPreferences.appThemeFlow.collectAsState(initial = AppTheme.SYSTEM)
            val isDarkMode = when (appTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }
            ZCPCTheme(darkTheme = isDarkMode) {
                when {
                    handle == null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    handle!!.isEmpty() -> {
                        SetupRoute(
                            onNavigateToMain = {

                            }
                        )
                    }
                    else -> {
                        MainScreen(
                            intent = currentIntent,
                            onIntentHandled = {
                                currentIntent = null
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        currentIntent = intent
    }
}
