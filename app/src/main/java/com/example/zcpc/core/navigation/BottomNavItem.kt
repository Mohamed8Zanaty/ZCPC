package com.example.zcpc.core.navigation

import com.example.zcpc.R

data class BottomNavItem(
    val title: String,
    val icon: Int,
    val route: Any
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Profile",
        icon = R.drawable.profile,
        route = Profile
    ),
    BottomNavItem(
        title = "Contests",
        icon = R.drawable.date_range,
        route = Contests
    ),
    BottomNavItem(
        title = "Problems",
        icon = R.drawable.list,
        route = Problems
    ),
    BottomNavItem(
        title = "Rivals",
        icon = R.drawable.rivals,
        route = Rivals
    )
)
