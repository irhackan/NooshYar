package com.nooshyar.app.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.R
import com.nooshyar.app.core.ui.components.*
import com.nooshyar.app.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onWhatToDrink: () -> Unit,
    onLogDrink: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text(stringResource(R.string.greeting, state.userName))
                    Text(state.jalaliDate, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onWhatToDrink, icon = { Text("💡") }, text = { Text(stringResource(R.string.what_to_drink_now)) })
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
        ) {
            Text(state.dailyInsight, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(vertical = 8.dp))
            Text(state.currentTime, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(16.dp))

            state.topSuggestion?.let { suggestion ->
                NooshCard {
                    Text(stringResource(R.string.current_suggestion), style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Text(suggestion.drink.icon, style = MaterialTheme.typography.headlineLarge)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(suggestion.drink.nameFa, style = MaterialTheme.typography.titleLarge)
                            suggestion.reasons.forEach { reason ->
                                Text("• $reason", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(stringResource(R.string.i_consumed)) { viewModel.acceptSuggestion(onLogDrink) }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecondaryButton(stringResource(R.string.another_suggestion), { viewModel.refreshSuggestion() }, Modifier.weight(1f))
                        SecondaryButton(stringResource(R.string.not_now), { viewModel.rejectSuggestion() }, Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(stringResource(R.string.water_consumed), "${state.waterMl} ml", Modifier.weight(1f))
                StatCard(stringResource(R.string.caffeine_consumed), "${state.caffeineMg} mg", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(stringResource(R.string.drink_count), state.drinkCount.toString(), Modifier.weight(1f))
                StatCard(stringResource(R.string.last_drink), state.lastDrinkTime ?: stringResource(R.string.none), Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))
            ProgressBar(stringResource(R.string.water_progress), state.waterProgress, "${state.waterMl}/${state.waterGoal} ml")
            Spacer(Modifier.height(12.dp))
            ProgressBar(stringResource(R.string.caffeine_progress), state.caffeineProgress, "${state.caffeineMg}/${state.caffeineLimit} mg")
            Spacer(Modifier.height(12.dp))
            ProgressBar(stringResource(R.string.time_to_sleep), 1f - (state.hoursToSleep / 16f).coerceIn(0f, 1f), String.format("%.1f ساعت", state.hoursToSleep))

            Spacer(Modifier.height(80.dp))
        }
    }
}
