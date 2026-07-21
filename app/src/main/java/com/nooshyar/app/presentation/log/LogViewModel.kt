package com.nooshyar.app.presentation.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooshyar.app.data.repository.ConsumptionRepository
import com.nooshyar.app.data.repository.DrinkRepository
import com.nooshyar.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LogUiState(
    val drinks: List<Drink> = emptyList(),
    val popularDrinks: List<Drink> = emptyList(),
    val recentDrinks: List<Drink> = emptyList(),
    val searchQuery: String = "",
    val selectedDrink: Drink? = null,
    val volume: Int = 250,
    val servings: Int = 1,
    val mood: ConsumptionMood? = null,
    val note: String = "",
    val savedMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class LogViewModel @Inject constructor(
    private val drinkRepo: DrinkRepository,
    private val consumptionRepo: ConsumptionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LogUiState())
    val state: StateFlow<LogUiState> = _state.asStateFlow()

    init {
        loadDrinks()
    }

    fun loadDrinks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val all = drinkRepo.getAll()
            val popularIds = consumptionRepo.getPopularDrinkIds(8)
            val recentIds = consumptionRepo.getRecent(5).map { it.drinkId }.distinct()
            _state.update {
                it.copy(
                    drinks = all,
                    popularDrinks = popularIds.mapNotNull { id -> all.find { d -> d.id == id } },
                    recentDrinks = recentIds.mapNotNull { id -> all.find { d -> d.id == id } },
                    isLoading = false
                )
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(searchQuery = query) }
            val results = drinkRepo.search(query)
            _state.update { it.copy(drinks = results) }
        }
    }

    fun selectDrink(drink: Drink) {
        _state.update { it.copy(selectedDrink = drink, volume = drink.defaultVolume, servings = 1) }
    }

    fun updateVolume(v: Int) { _state.update { it.copy(volume = v.coerceIn(50, 2000)) } }
    fun updateServings(v: Int) { _state.update { it.copy(servings = v.coerceIn(1, 10)) } }
    fun updateMood(m: ConsumptionMood?) { _state.update { it.copy(mood = m) } }
    fun updateNote(n: String) { _state.update { it.copy(note = n) } }
    fun clearSelection() { _state.update { it.copy(selectedDrink = null, savedMessage = null) } }

    fun saveConsumption(onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            val s = _state.value
            val drink = s.selectedDrink ?: return@launch
            val caffeine = drink.caffeinePerServing * s.servings
            consumptionRepo.log(ConsumptionLog(
                drinkId = drink.id,
                drinkName = drink.nameFa,
                dateTimeMillis = System.currentTimeMillis(),
                volume = s.volume,
                servings = s.servings,
                caffeine = caffeine,
                sugar = drink.sugarPerServing * s.servings,
                calories = drink.caloriesPerServing * s.servings,
                mood = s.mood,
                note = s.note.ifBlank { null }
            ))
            _state.update { it.copy(savedMessage = "این نوشیدنی حدود $caffeine میلی‌گرم کافئین به مصرف امروز شما اضافه کرد.", selectedDrink = null) }
            loadDrinks()
            onSaved()
        }
    }

    fun loadDrinkById(id: Long) {
        viewModelScope.launch {
            drinkRepo.getById(id)?.let { selectDrink(it) }
        }
    }
}
