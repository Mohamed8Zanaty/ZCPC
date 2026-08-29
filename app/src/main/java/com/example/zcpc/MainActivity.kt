package com.example.zcpc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZCPCTheme {
                val handle by userPreferences.userHandleFlow.collectAsState(initial = null)
                val navController = rememberNavController()
                if (handle == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val startDest = if (handle!!.isEmpty()) Setup else MainGraph

                    NavHost(navController = navController, startDestination = startDest) {
                        composable<Setup> {
                            SetupRoute(
                                onNavigateToMain = {
                                    navController.navigate(MainGraph) {
                                        popUpTo(Setup) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable<MainGraph> {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}
