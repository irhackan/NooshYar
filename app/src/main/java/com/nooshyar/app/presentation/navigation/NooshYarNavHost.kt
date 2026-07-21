package com.nooshyar.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nooshyar.app.core.ui.theme.NooshYarTheme
import com.nooshyar.app.data.repository.PreferencesRepository
import com.nooshyar.app.domain.model.ThemeMode
import com.nooshyar.app.presentation.main.MainScreen
import com.nooshyar.app.presentation.onboarding.OnboardingScreen
import com.nooshyar.app.presentation.profile.ProfileSetupScreen
import com.nooshyar.app.presentation.splash.SplashScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AppViewModel @Inject constructor(
    preferencesRepository: PreferencesRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> =
        preferencesRepository.themeMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 5_000
            ),
            initialValue = ThemeMode.SYSTEM
        )
}

@Composable
fun NooshYarNavHost(
    appViewModel: AppViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val themeMode by appViewModel.themeMode.collectAsState()

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        NooshYarTheme(
            themeMode = themeMode
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH
            ) {
                composable(
                    route = Routes.SPLASH
                ) {
                    SplashScreen(
                        onNavigate = { destination ->
                            navController.navigate(destination) {
                                popUpTo(Routes.SPLASH) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(
                    route = Routes.ONBOARDING
                ) {
                    OnboardingScreen(
                        onNavigate = { destination ->
                            navController.navigate(destination) {
                                popUpTo(Routes.ONBOARDING) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(
                    route = Routes.PROFILE_SETUP
                ) {
                    ProfileSetupScreen(
                        onNavigate = { destination ->
                            navController.navigate(destination) {
                                popUpTo(Routes.PROFILE_SETUP) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(
                    route = Routes.MAIN
                ) {
                    MainScreen()
                }
            }
        }
    }
}
