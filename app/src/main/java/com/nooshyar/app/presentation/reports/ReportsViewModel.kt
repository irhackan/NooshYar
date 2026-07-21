package com.nooshyar.app.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooshyar.app.core.util.JalaliDate
import com.nooshyar.app.data.repository.ConsumptionRepository
import com.nooshyar.app.data.repository.ReportRepository
import com.nooshyar.app.data.repository.SuggestionRepository
import com.nooshyar.app.data.repository.UserRepository
import com.nooshyar.app.domain.engine.CaffeineCalculator
import com.nooshyar.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportsUiState(
    val selectedTab: Int = 0,
    val dailyStats: DailyStats = DailyStats(),
    val weeklyStats: WeeklyStats = WeeklyStats(),
    val monthlyStats: MonthlyStats = MonthlyStats(),
    val yearlyStats: YearlyStats = YearlyStats(),
    val calendarDays: List<CalendarDay> = emptyList(),
    val selectedCalendarDay: CalendarDay? = null,
    val selectedDayStats: DailyStats? = null,
    val monthTitle: String = "",
    val caffeineStatus: CaffeineStatus? = null,
    val history: List<ConsumptionLog> = emptyList(),
    val filterCaffeinated: Boolean? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportRepo: ReportRepository,
    private val consumptionRepo: ConsumptionRepository,
    private val userRepo: UserRepository,
    private val suggestionRepo: SuggestionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsUiState())
    val state: StateFlow<ReportsUiState> = _state.asStateFlow()

    init { refresh() }

    fun selectTab(tab: Int) { _state.update { it.copy(selectedTab = tab) } }

    fun setFilter(caffeinated: Boolean?) {
        _state.update { it.copy(filterCaffeinated = caffeinated) }
        loadHistory()
    }

    fun selectCalendarDay(day: CalendarDay) {
        viewModelScope.launch {
            val stats = reportRepo.getDailyStats(day.millis)
            _state.update { it.copy(selectedCalendarDay = day, selectedDayStats = stats) }
        }
    }

    fun clearCalendarSelection() {
        _state.update { it.copy(selectedCalendarDay = null, selectedDayStats = null) }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val profile = userRepo.getProfile() ?: UserProfile()
            val daily = reportRepo.getDailyStats()
            val weekly = reportRepo.getWeeklyStats()
            val monthly = reportRepo.getMonthlyStats(caffeineLimit = profile.caffeineDailyLimit)
            val accepted = suggestionRepo.countAccepted()
            val yearly = reportRepo.getYearlyStats(accepted)
            val calendar = reportRepo.getCalendarDays(
                waterGoal = profile.waterDailyGoal,
                caffeineLimit = profile.caffeineDailyLimit
            )
            val todayLogs = consumptionRepo.getToday()
            val caffeineStatus = CaffeineCalculator.buildStatus(todayLogs, profile)
            _state.update {
                it.copy(
                    dailyStats = daily,
                    weeklyStats = weekly,
                    monthlyStats = monthly,
                    yearlyStats = yearly,
                    calendarDays = calendar,
                    monthTitle = JalaliDate.currentMonthTitle(),
                    caffeineStatus = caffeineStatus,
                    isLoading = false
                )
            }
            loadHistory()
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val monthStart = JalaliDate.startOfMonth()
            var logs = consumptionRepo.getBetween(monthStart, System.currentTimeMillis())
            _state.value.filterCaffeinated?.let { filter ->
                logs = logs.filter { if (filter) it.caffeine > 0 else it.caffeine == 0 }
            }
            _state.update { it.copy(history = logs) }
        }
    }

    fun deleteLog(log: ConsumptionLog) {
        viewModelScope.launch {
            consumptionRepo.delete(log)
            refresh()
        }
    }
}
