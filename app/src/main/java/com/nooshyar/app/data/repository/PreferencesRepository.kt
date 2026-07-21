package com.nooshyar.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.nooshyar.app.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val PROFILE_SETUP_DONE = booleanPreferencesKey("profile_setup_done")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val onboardingDone: Flow<Boolean> = dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }
    val profileSetupDone: Flow<Boolean> = dataStore.data.map { it[Keys.PROFILE_SETUP_DONE] ?: false }
    val themeMode: Flow<ThemeMode> = dataStore.data.map {
        ThemeMode.valueOf(it[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
    }

    suspend fun setOnboardingDone(done: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }

    suspend fun setProfileSetupDone(done: Boolean) {
        dataStore.edit { it[Keys.PROFILE_SETUP_DONE] = done }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
