package com.nooshyar.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooshyar.app.data.repository.PreferencesRepository
import com.nooshyar.app.data.repository.UserRepository
import com.nooshyar.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val prefs: PreferencesRepository
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _saved = MutableSharedFlow<Unit>()
    val saved = _saved.asSharedFlow()

    init {
        viewModelScope.launch {
            userRepo.getProfile()?.let { _profile.value = it }
        }
    }

    fun updateName(v: String) { _profile.update { it.copy(name = v) } }
    fun updateAge(v: Int) { _profile.update { it.copy(age = v.coerceIn(10, 100)) } }
    fun updateWeight(v: Float) {
        _profile.update {
            val w = v.coerceIn(30f, 200f)
            it.copy(weight = w, waterDailyGoal = (w * 35).toInt())
        }
    }
    fun updateWakeTime(minutes: Int) { _profile.update { it.copy(wakeTimeMinutes = minutes) } }
    fun updateSleepTime(minutes: Int) { _profile.update { it.copy(sleepTimeMinutes = minutes) } }
    fun updateActivity(level: ActivityLevel) { _profile.update { it.copy(activityLevel = level) } }
    fun updateWorkPressure(level: PressureLevel) { _profile.update { it.copy(workPressure = level) } }
    fun updateCaffeineSensitivity(s: CaffeineSensitivity) { _profile.update { it.copy(caffeineSensitivity = s) } }
    fun toggleGoal(goal: UserGoal) {
        _profile.update {
            val goals = it.selectedGoals.toMutableSet()
            if (goal in goals) goals.remove(goal) else goals.add(goal)
            it.copy(selectedGoals = goals)
        }
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val p = _profile.value.copy(name = _profile.value.name.ifBlank { "کاربر" })
            userRepo.saveProfile(p)
            prefs.setProfileSetupDone(true)
            _saved.emit(Unit)
            onDone()
        }
    }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val prefs: PreferencesRepository,
    private val consumptionRepo: com.nooshyar.app.data.repository.ConsumptionRepository,
    private val suggestionRepo: com.nooshyar.app.data.repository.SuggestionRepository,
    private val notificationRepo: com.nooshyar.app.data.repository.NotificationRepository,
    private val backupRepo: com.nooshyar.app.data.repository.BackupRepository
) : ViewModel() {

    val profile = userRepo.observeProfile().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val themeMode = prefs.themeMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    private val _profileEdit = MutableStateFlow<UserProfile?>(null)
    val profileEdit: StateFlow<UserProfile?> = _profileEdit.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    private val _exportJson = MutableSharedFlow<String>()
    val exportJson = _exportJson.asSharedFlow()

    fun startEdit() {
        viewModelScope.launch {
            _profileEdit.value = userRepo.getProfile() ?: UserProfile()
        }
    }

    fun updateEdit(block: (UserProfile) -> UserProfile) {
        _profileEdit.update { it?.let(block) }
    }

    fun saveEdit() {
        viewModelScope.launch {
            _profileEdit.value?.let { userRepo.saveProfile(it) }
            _profileEdit.value = null
        }
    }

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch { prefs.setThemeMode(mode) }
    }

    fun exportBackup() {
        viewModelScope.launch {
            val json = backupRepo.exportJson()
            _exportJson.emit(json)
            _message.emit("backup_ready")
        }
    }

    fun importBackup(json: String, onDone: () -> Unit) {
        viewModelScope.launch {
            backupRepo.importJson(json)
                .onSuccess { count ->
                    _message.emit("imported:$count")
                    onDone()
                }
                .onFailure {
                    _message.emit("import_failed")
                }
        }
    }

    fun deleteAllData(onDone: () -> Unit) {
        viewModelScope.launch {
            consumptionRepo.deleteAll()
            suggestionRepo.deleteAll()
            notificationRepo.deleteAll()
            userRepo.deleteAll()
            prefs.clearAll()
            onDone()
        }
    }
}
