package com.nooshyar.app.presentation.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nooshyar.app.data.repository.PreferencesRepository
import com.nooshyar.app.domain.model.ThemeMode
import com.nooshyar.app.presentation.main.MainScreen
import com.nooshyar.app.presentation.onboarding.OnboardingScreen
import com.nooshyar.app.presentation.profile.ProfileSetupScreen
import com.nooshyar.app.presentation.splash.SplashScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    prefs: PreferencesRepository
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = prefs.themeMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM
    )
}

@Composable
fun NooshYarNavHost(appViewModel: AppViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val themeMode by appViewModel.themeMode.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        com.nooshyar.app.core.ui.theme.NooshYarTheme(themeMode = themeMode) {
            NavHost(navController = navController, startDestination = Routes.SPLASH) {
                composable(Routes.SPLASH) {
                    SplashScreen(onNavigate = { dest ->
                        navController.navigate(dest) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    })
                }
                composable(Routes.ONBOARDING) {
                    OnboardingScreen(onNavigate = { dest ->
                        navController.navigate(dest) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    })
                }
                composable(Routes.PROFILE_SETUP) {
                    ProfileSetupScreen(onNavigate = { dest ->
                        navController.navigate(dest) {
                            popUpTo(Routes.PROFILE_SETUP) { inclusive = true }
                        }
                    })
                }
                composable(Routes.MAIN) { MainScreen() }
            }
        }
    }
}
