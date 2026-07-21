package com.nooshyar.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooshyar.app.data.repository.PreferencesRepository
import com.nooshyar.app.data.repository.UserRepository
import com.nooshyar.app.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs: PreferencesRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<String?>(null)
    val destination: StateFlow<String?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2200)
            val onboardingDone = prefs.onboardingDone.first()
            val profileDone = prefs.profileSetupDone.first()
            val hasUser = userRepo.getProfile() != null

            _destination.value = when {
                !onboardingDone -> Routes.ONBOARDING
                !profileDone || !hasUser -> Routes.PROFILE_SETUP
                else -> Routes.MAIN
            }
        }
    }
}
