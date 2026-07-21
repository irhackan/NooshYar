package com.nooshyar.app.presentation.suggest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooshyar.app.core.util.JalaliDate
import com.nooshyar.app.data.repository.*
import com.nooshyar.app.domain.engine.RecommendationEngine
import com.nooshyar.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SuggestUiState(
    val feelings: Set<UserFeeling> = emptySet(),
    val activity: UserActivity? = null,
    val temperaturePref: TemperatureType = TemperatureType.ANY,
    val hoursUntilSleep: Float = 8f,
    val suggestions: List<DrinkSuggestion> = emptyList(),
    val showResults: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class SuggestViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val drinkRepo: DrinkRepository,
    private val consumptionRepo: ConsumptionRepository,
    private val suggestionRepo: SuggestionRepository,
    private val engine: RecommendationEngine
) : ViewModel() {

    private val _state = MutableStateFlow(SuggestUiState())
    val state: StateFlow<SuggestUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userRepo.getProfile()?.let { profile ->
                _state.update {
                    it.copy(hoursUntilSleep = JalaliDate.minutesUntilSleep(System.currentTimeMillis(), profile.sleepTimeMinutes))
                }
            }
        }
    }

    fun toggleFeeling(f: UserFeeling) {
        _state.update {
            val set = it.feelings.toMutableSet()
            if (f in set) set.remove(f) else set.add(f)
            it.copy(feelings = set)
        }
    }

    fun setActivity(a: UserActivity) { _state.update { it.copy(activity = a) } }
    fun setTemperature(t: TemperatureType) { _state.update { it.copy(temperaturePref = t) } }
    fun setHoursUntilSleep(h: Float) { _state.update { it.copy(hoursUntilSleep = h) } }

    fun getSuggestions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val profile = userRepo.getProfile() ?: UserProfile()
            val drinks = drinkRepo.getAll()
            val logs = consumptionRepo.getToday()
            val context = SuggestionContext(
                feelings = _state.value.feelings,
                activity = _state.value.activity,
                temperaturePref = _state.value.temperaturePref,
                hoursUntilSleep = _state.value.hoursUntilSleep
            )
            val behaviorWeights = suggestionRepo.getBehaviorWeights()
            val suggestions = engine.getSuggestions(drinks, profile, logs, context, behaviorWeights)
            _state.update { it.copy(suggestions = suggestions, showResults = true, isLoading = false) }
        }
    }

    fun logSuggestion(suggestion: DrinkSuggestion, onDone: () -> Unit) {
        viewModelScope.launch {
            val id = suggestionRepo.saveSuggestion(1, suggestion.drink.id, suggestion.score, suggestion.reasons.joinToString("\n"))
            suggestionRepo.recordResponse(id, SuggestionResponse.ACCEPTED)
            val drink = suggestion.drink
            consumptionRepo.log(ConsumptionLog(
                drinkId = drink.id,
                drinkName = drink.nameFa,
                dateTimeMillis = System.currentTimeMillis(),
                volume = suggestion.suggestedVolume,
                servings = 1,
                caffeine = suggestion.approxCaffeine,
                sugar = drink.sugarPerServing,
                calories = drink.caloriesPerServing,
                wasSuggested = true,
                suggestionId = id
            ))
            onDone()
        }
    }

    fun reset() { _state.update { it.copy(showResults = false, suggestions = emptyList()) } }
}
