package com.example.zcpc.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.zcpc.core.navigation.Profile
import com.example.zcpc.core.navigation.bottomNavItems
import com.example.zcpc.feature.contests.ContestsRoute
import com.example.zcpc.feature.profile.ProfileRoute

@SuppressLint("RestrictedApi")
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

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
        }
    }
}