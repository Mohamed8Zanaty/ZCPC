package com.example.zcpc.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.savedState
import com.example.zcpc.core.navigation.BottomNavItem
import com.example.zcpc.core.navigation.Contests
import com.example.zcpc.core.navigation.Notifications
import com.example.zcpc.core.navigation.Problems
import com.example.zcpc.core.navigation.Profile
import com.example.zcpc.core.navigation.Rivals
import com.example.zcpc.core.navigation.bottomNavItems
import com.example.zcpc.feature.contests.ContestsRoute
import com.example.zcpc.feature.notifications.NotificationsScreen
import com.example.zcpc.feature.notifications.NotificationsViewModel
import com.example.zcpc.feature.problems.ProblemsRoute
import com.example.zcpc.feature.profile.ProfileRoute
import com.example.zcpc.feature.rivals.RivalsRoute

@SuppressLint("RestrictedApi")
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {

            }
        }
    )

    // Trigger it when they try to add their first rival or open the screen
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.hasRoute(item.route::class)
                    } == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(painter = painterResource(item.icon), contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Profile,
            modifier = Modifier.padding(innerPadding)

        ) {
            composable<Profile> {
                ProfileRoute()
            }
            composable<Contests> {
                ContestsRoute()
            }
            composable<Problems> {
                ProblemsRoute()
            }
            composable<Rivals> {
                RivalsRoute(
                    onNavigateToNotifications = {
                        navController.navigate(Notifications)
                    }
                )
            }
            composable<Notifications> {
                NotificationsScreen()
            }
        }
    }
}