package com.nooshyar.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooshyar.app.core.util.JalaliDate
import com.nooshyar.app.data.repository.*
import com.nooshyar.app.domain.engine.CaffeineCalculator
import com.nooshyar.app.domain.engine.RecommendationEngine
import com.nooshyar.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val jalaliDate: String = "",
    val currentTime: String = "",
    val dailyInsight: String = "",
    val topSuggestion: DrinkSuggestion? = null,
    val waterMl: Int = 0,
    val caffeineMg: Int = 0,
    val drinkCount: Int = 0,
    val lastDrinkTime: String? = null,
    val waterProgress: Float = 0f,
    val caffeineProgress: Float = 0f,
    val hoursToSleep: Float = 0f,
    val waterGoal: Int = 2450,
    val caffeineLimit: Int = 400,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val drinkRepo: DrinkRepository,
    private val consumptionRepo: ConsumptionRepository,
    private val suggestionRepo: SuggestionRepository,
    private val engine: RecommendationEngine
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var currentSuggestionId: Long? = null
    private var allDrinks: List<Drink> = emptyList()
    private var behaviorWeights: List<DrinkBehaviorWeight> = emptyList()

    init {
        viewModelScope.launch {
            allDrinks = drinkRepo.getAll()
            behaviorWeights = suggestionRepo.getBehaviorWeights()
            combine(userRepo.observeProfile(), consumptionRepo.observeToday()) { profile, logs ->
                loadState(profile, logs)
            }.collect()
        }
    }

    private suspend fun loadState(profile: UserProfile?, logs: List<ConsumptionLog>) {
        val p = profile ?: UserProfile()
        val water = logs.filter { it.drinkName.contains("آب") || it.drinkName == "آب" }.sumOf { it.volume }
        val caffeine = logs.sumOf { it.caffeine }
        val suggestion = engine.getTopSuggestion(allDrinks, p, logs, behaviorWeights)
        val insight = engine.getDailyInsight(p, logs)
        val lastTime = logs.maxByOrNull { it.dateTimeMillis }?.dateTimeMillis

        _state.value = HomeUiState(
            userName = p.name.ifBlank { "کاربر" },
            jalaliDate = JalaliDate.formatToday(),
            currentTime = JalaliDate.formatTime(System.currentTimeMillis()),
            dailyInsight = insight,
            topSuggestion = suggestion,
            waterMl = water,
            caffeineMg = caffeine,
            drinkCount = logs.size,
            lastDrinkTime = lastTime?.let { JalaliDate.formatTime(it) },
            waterProgress = if (p.waterDailyGoal > 0) water.toFloat() / p.waterDailyGoal else 0f,
            caffeineProgress = if (p.caffeineDailyLimit > 0) caffeine.toFloat() / p.caffeineDailyLimit else 0f,
            hoursToSleep = JalaliDate.minutesUntilSleep(System.currentTimeMillis(), p.sleepTimeMinutes),
            waterGoal = p.waterDailyGoal,
            caffeineLimit = p.caffeineDailyLimit,
            isLoading = false
        )
    }

    fun acceptSuggestion(onLogged: () -> Unit) {
        viewModelScope.launch {
            val suggestion = _state.value.topSuggestion ?: return@launch
            val id = suggestionRepo.saveSuggestion(1, suggestion.drink.id, suggestion.score, suggestion.reasons.joinToString("\n"))
            currentSuggestionId = id
            logDrink(suggestion.drink, suggestion.suggestedVolume, wasSuggested = true, suggestionId = id)
            suggestionRepo.recordResponse(id, SuggestionResponse.ACCEPTED)
            behaviorWeights = suggestionRepo.getBehaviorWeights()
            onLogged()
        }
    }

    fun rejectSuggestion() {
        viewModelScope.launch {
            currentSuggestionId?.let { suggestionRepo.recordResponse(it, SuggestionResponse.REJECTED) }
            behaviorWeights = suggestionRepo.getBehaviorWeights()
            refreshSuggestion()
        }
    }

    fun refreshSuggestion() {
        viewModelScope.launch {
            val profile = userRepo.getProfile() ?: return@launch
            val logs = consumptionRepo.getToday()
            val suggestion = engine.getTopSuggestion(allDrinks, profile, logs, behaviorWeights)
            _state.update { it.copy(topSuggestion = suggestion) }
        }
    }

    fun logDrink(drink: Drink, volume: Int, wasSuggested: Boolean = false, suggestionId: Long? = null) {
        viewModelScope.launch {
            val servings = maxOf(1, volume / drink.defaultVolume)
            consumptionRepo.log(ConsumptionLog(
                drinkId = drink.id,
                drinkName = drink.nameFa,
                dateTimeMillis = System.currentTimeMillis(),
                volume = volume,
                servings = servings,
                caffeine = drink.caffeinePerServing * servings,
                sugar = drink.sugarPerServing * servings,
                calories = drink.caloriesPerServing * servings,
                wasSuggested = wasSuggested,
                suggestionId = suggestionId
            ))
        }
    }
}
