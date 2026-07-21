package com.nooshyar.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooshyar.app.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: PreferencesRepository
) : ViewModel() {
    fun completeOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            prefs.setOnboardingDone(true)
            onDone()
        }
    }
}
