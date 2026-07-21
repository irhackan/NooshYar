package com.nooshyar.app.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.nooshyar.app.R
import com.nooshyar.app.presentation.home.HomeScreen
import com.nooshyar.app.presentation.log.LogScreen
import com.nooshyar.app.presentation.navigation.BottomNavItem
import com.nooshyar.app.presentation.navigation.Routes
import com.nooshyar.app.presentation.profile.*
import com.nooshyar.app.presentation.reports.ReportsScreen
import com.nooshyar.app.presentation.suggest.SuggestScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.HOME to Pair(R.string.nav_home, "🏠"),
        BottomNavItem.LOG to Pair(R.string.nav_log, "➕"),
        BottomNavItem.SUGGEST to Pair(R.string.nav_suggest, "💡"),
        BottomNavItem.REPORTS to Pair(R.string.nav_reports, "📊"),
        BottomNavItem.PROFILE to Pair(R.string.nav_profile, "👤")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { (route, labelIcon) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(labelIcon.second) },
                        label = { Text(stringResource(labelIcon.first), maxLines = 1) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomNavItem.HOME) {
                HomeScreen(
                    onWhatToDrink = { navController.navigate(BottomNavItem.SUGGEST) },
                    onLogDrink = { navController.navigate(BottomNavItem.LOG) }
                )
            }
            composable(BottomNavItem.LOG) { LogScreen() }
            composable(BottomNavItem.SUGGEST) {
                SuggestScreen(onLogged = { navController.navigate(BottomNavItem.HOME) })
            }
            composable(BottomNavItem.REPORTS) { ReportsScreen() }
            composable(BottomNavItem.PROFILE) {
                ProfileScreen(
                    onNavigateAbout = { navController.navigate(Routes.ABOUT) },
                    onNavigateSetup = { navController.navigate(Routes.PROFILE_SETUP) }
                )
            }
            composable(Routes.ABOUT) { AboutScreen(onBack = { navController.popBackStack() }) }
            composable(Routes.PROFILE_SETUP) {
                ProfileSetupScreen(onNavigate = { navController.popBackStack() })
            }
        }
    }
}
