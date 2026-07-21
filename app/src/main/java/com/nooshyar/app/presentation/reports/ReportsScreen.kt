package com.nooshyar.app.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.R
import com.nooshyar.app.core.ui.components.*
import com.nooshyar.app.core.ui.theme.AlertRed
import com.nooshyar.app.core.ui.theme.SoftGreen
import com.nooshyar.app.core.ui.theme.WarningOrange
import com.nooshyar.app.core.util.JalaliDate
import com.nooshyar.app.domain.model.CalendarDay
import com.nooshyar.app.domain.model.ConsumptionLog
import com.nooshyar.app.domain.model.DayStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: ReportsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<ConsumptionLog?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_reports)) }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = state.selectedTab, edgePadding = 8.dp) {
                listOf(
                    R.string.daily_report,
                    R.string.weekly_report,
                    R.string.monthly_report,
                    R.string.yearly_report,
                    R.string.calendar,
                    R.string.history
                ).forEachIndexed { index, title ->
                    Tab(
                        selected = state.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = { Text(stringResource(title), maxLines = 1) }
                    )
                }
            }

            when (state.selectedTab) {
                0 -> DailyReportTab(state, Modifier.weight(1f))
                1 -> WeeklyReportTab(state, Modifier.weight(1f))
                2 -> MonthlyReportTab(state, Modifier.weight(1f))
                3 -> YearlyReportTab(state, Modifier.weight(1f))
                4 -> CalendarTab(state, viewModel, Modifier.weight(1f))
                5 -> HistoryTab(state, viewModel, { showDeleteDialog = it }, Modifier.weight(1f))
            }
        }
    }

    showDeleteDialog?.let { log ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.confirm_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteLog(log)
                    showDeleteDialog = null
                }) { Text(stringResource(R.string.yes)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text(stringResource(R.string.no)) }
            }
        )
    }
}

@Composable
private fun DailyReportTab(state: ReportsUiState, modifier: Modifier = Modifier) {
    val stats = state.dailyStats
    if (stats.totalDrinks == 0) {
        EmptyState(stringResource(R.string.no_data), modifier)
        return
    }
    Column(modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(stringResource(R.string.total_drinks), stats.totalDrinks.toString(), Modifier.weight(1f))
            StatCard(stringResource(R.string.total_volume), "${stats.totalVolume} ml", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(stringResource(R.string.total_caffeine), "${stats.totalCaffeine} mg", Modifier.weight(1f))
            StatCard(stringResource(R.string.total_sugar), String.format("%.0f g", stats.totalSugar), Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        stats.firstDrinkTime?.let { Text("${stringResource(R.string.first_drink)}: ${JalaliDate.formatTime(it)}") }
        stats.lastDrinkTime?.let { Text("${stringResource(R.string.last_drink)}: ${JalaliDate.formatTime(it)}") }
        stats.mostConsumedDrink?.let { Text("${stringResource(R.string.most_consumed)}: $it") }

        state.caffeineStatus?.let { cs ->
            Spacer(Modifier.height(16.dp))
            NooshCard {
                Text("کافئین ${stringResource(R.string.estimate_label)}", style = MaterialTheme.typography.titleMedium)
                Text("مصرف امروز: ${cs.consumedToday} mg")
                Text("کافئین فعال: ${cs.activeEstimate} mg")
                cs.lastConsumptionTime?.let { Text("آخرین مصرف: ${JalaliDate.formatTime(it)}") }
                Text(cs.recommendation, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun WeeklyReportTab(state: ReportsUiState, modifier: Modifier = Modifier) {
    val stats = state.weeklyStats
    Column(modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(stringResource(R.string.avg_water), "${stats.avgWater} ml", Modifier.weight(1f))
            StatCard(stringResource(R.string.avg_caffeine), "${stats.avgCaffeine} mg", Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        stats.popularDrink?.let { Text("${stringResource(R.string.popular_drink)}: $it") }
        Text("${stringResource(R.string.compare_last_week)}:")
        Text("آب: ${if (stats.waterChangePercent >= 0) "+" else ""}${stats.waterChangePercent}%")
        Text("کافئین: ${if (stats.caffeineChangePercent >= 0) "+" else ""}${stats.caffeineChangePercent}%")
        if (stats.dailyWater.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("روند آب هفتگی", style = MaterialTheme.typography.titleMedium)
            val dayNames = listOf("شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه")
            stats.dailyWater.forEachIndexed { i, v ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(dayNames.getOrElse(i) { "روز ${i + 1}" })
                    Text("$v ml")
                }
            }
        }
    }
}

@Composable
private fun MonthlyReportTab(state: ReportsUiState, modifier: Modifier = Modifier) {
    val stats = state.monthlyStats
    Column(modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(state.monthTitle, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(stringResource(R.string.total_drinks), stats.totalDrinks.toString(), Modifier.weight(1f))
            StatCard(stringResource(R.string.coffee_cups), stats.totalCoffeeCups.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(stringResource(R.string.tea_cups), stats.totalTeaCups.toString(), Modifier.weight(1f))
            StatCard(stringResource(R.string.decaf_count), stats.totalDecafDrinks.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(stringResource(R.string.total_water), "${stats.totalWaterMl} ml", Modifier.weight(1f))
            StatCard(stringResource(R.string.avg_caffeine), "${stats.avgDailyCaffeine} mg", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Text("${stringResource(R.string.days_over_limit)}: ${stats.daysOverLimit}")
        stats.bestDayLabel?.let { Text("${stringResource(R.string.best_day)}: $it") }
        stats.attentionDayLabel?.let { Text("${stringResource(R.string.attention_day)}: $it") }
        Text("تغییر آب نسبت به ماه قبل: ${if (stats.waterChangePercent >= 0) "+" else ""}${stats.waterChangePercent}%")
        Spacer(Modifier.height(12.dp))
        NooshCard {
            Text(stringResource(R.string.habit_tip), style = MaterialTheme.typography.titleMedium)
            Text(stats.habitTip, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun YearlyReportTab(state: ReportsUiState, modifier: Modifier = Modifier) {
    val stats = state.yearlyStats
    Column(modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(stringResource(R.string.yearly_highlight), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(stringResource(R.string.total_drinks), stats.totalDrinks.toString(), Modifier.weight(1f))
            StatCard(stringResource(R.string.logged_days), stats.loggedDays.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(stringResource(R.string.coffee_cups), stats.totalCoffeeCups.toString(), Modifier.weight(1f))
            StatCard(stringResource(R.string.tea_cups), stats.totalTeaCups.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        StatCard(stringResource(R.string.total_water), "${stats.totalWaterMl} ml")
        Spacer(Modifier.height(8.dp))
        StatCard(stringResource(R.string.avg_caffeine), "${stats.avgDailyCaffeine} mg")
        Spacer(Modifier.height(12.dp))
        stats.popularDrink?.let { Text("${stringResource(R.string.popular_drink)}: $it") }
        stats.popularHour?.let { Text("${stringResource(R.string.popular_hour)}: $it") }
        stats.busiestWeekday?.let { Text("${stringResource(R.string.busiest_weekday)}: $it") }
        stats.busiestMonth?.let { Text("${stringResource(R.string.busiest_month)}: $it") }
        Text("${stringResource(R.string.accepted_suggestions)}: ${stats.acceptedSuggestions}")
        Spacer(Modifier.height(16.dp))
        DeveloperFooter()
    }
}

@Composable
private fun CalendarTab(state: ReportsUiState, viewModel: ReportsViewModel, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text(state.monthTitle, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            LegendDot(SoftGreen, stringResource(R.string.status_complete))
            LegendDot(MaterialTheme.colorScheme.primary, stringResource(R.string.status_balanced))
            LegendDot(WarningOrange, stringResource(R.string.status_low_water))
            LegendDot(AlertRed, stringResource(R.string.status_high_caffeine))
        }
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(state.calendarDays) { day ->
                CalendarDayCell(day) { viewModel.selectCalendarDay(day) }
            }
        }
        state.selectedCalendarDay?.let { day ->
            Spacer(Modifier.height(16.dp))
            NooshCard {
                Text(JalaliDate.formatDayLabel(day.millis), style = MaterialTheme.typography.titleMedium)
                Text("${stringResource(R.string.drink_count)}: ${day.drinkCount}")
                Text("${stringResource(R.string.water_consumed)}: ${day.waterMl} ml")
                Text("${stringResource(R.string.caffeine_consumed)}: ${day.caffeineMg} mg")
                state.selectedDayStats?.mostConsumedDrink?.let {
                    Text("${stringResource(R.string.most_consumed)}: $it")
                }
                TextButton(onClick = { viewModel.clearCalendarSelection() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(day: CalendarDay, onClick: () -> Unit) {
    val color = when (day.status) {
        DayStatus.NO_DATA -> MaterialTheme.colorScheme.surfaceVariant
        DayStatus.BALANCED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        DayStatus.LOW_WATER -> WarningOrange
        DayStatus.HIGH_CAFFEINE -> AlertRed
        DayStatus.COMPLETE -> SoftGreen
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            day.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = if (day.status == DayStatus.NO_DATA) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun HistoryTab(
    state: ReportsUiState,
    viewModel: ReportsViewModel,
    onDelete: (ConsumptionLog) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = state.filterCaffeinated == null, onClick = { viewModel.setFilter(null) }, label = { Text(stringResource(R.string.all)) })
            FilterChip(selected = state.filterCaffeinated == true, onClick = { viewModel.setFilter(true) }, label = { Text(stringResource(R.string.caffeinated)) })
            FilterChip(selected = state.filterCaffeinated == false, onClick = { viewModel.setFilter(false) }, label = { Text(stringResource(R.string.decaf)) })
        }
        Spacer(Modifier.height(8.dp))
        if (state.history.isEmpty()) {
            EmptyState(stringResource(R.string.no_data))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(state.history, key = { it.id }) { log ->
                    ListItem(
                        headlineContent = { Text(log.drinkName) },
                        supportingContent = {
                            Text("${JalaliDate.formatTime(log.dateTimeMillis)} • ${log.volume} ml • ${log.caffeine} mg")
                        },
                        trailingContent = {
                            IconButton(onClick = { onDelete(log) }) {
                                Text("✕", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
