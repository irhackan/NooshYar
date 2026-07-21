package com.nooshyar.app.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nooshyar.app.R
import com.nooshyar.app.presentation.home.HomeScreen
import com.nooshyar.app.presentation.log.LogScreen
import com.nooshyar.app.presentation.navigation.BottomNavItem
import com.nooshyar.app.presentation.navigation.Routes
import com.nooshyar.app.presentation.profile.AboutScreen
import com.nooshyar.app.presentation.profile.ProfileScreen
import com.nooshyar.app.presentation.profile.ProfileSetupScreen
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
                                popUpTo(
                                    navController.graph
                                        .findStartDestination()
                                        .id
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Text(text = labelIcon.second)
                        },
                        label = {
                            Text(
                                text = stringResource(labelIcon.first),
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.HOME) {
                HomeScreen(
                    onWhatToDrink = {
                        navController.navigate(BottomNavItem.SUGGEST)
                    },
                    onLogDrink = {
                        navController.navigate(BottomNavItem.LOG)
                    }
                )
            }

            composable(BottomNavItem.LOG) {
                LogScreen()
            }

            composable(BottomNavItem.SUGGEST) {
                SuggestScreen(
                    onLogged = {
                        navController.navigate(BottomNavItem.HOME) {
                            popUpTo(BottomNavItem.HOME) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(BottomNavItem.REPORTS) {
                ReportsScreen()
            }

            composable(BottomNavItem.PROFILE) {
                ProfileScreen(
                    onNavigateAbout = {
                        navController.navigate(Routes.ABOUT)
                    },
                    onNavigateSetup = {
                        navController.navigate(Routes.PROFILE_SETUP)
                    }
                )
            }

            composable(Routes.ABOUT) {
                AboutScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.PROFILE_SETUP) {
                ProfileSetupScreen(
                    onNavigate = { destination ->
                        if (destination == Routes.MAIN) {
                            navController.popBackStack(
                                route = BottomNavItem.PROFILE,
                                inclusive = false
                            )
                        } else {
                            navController.navigate(destination)
                        }
                    }
                )
            }
        }
    }
}
