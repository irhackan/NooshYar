package com.nooshyar.app.presentation.suggest

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.R
import com.nooshyar.app.core.ui.components.*
import com.nooshyar.app.domain.model.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SuggestScreen(onLogged: () -> Unit = {}, viewModel: SuggestViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.what_to_drink_now)) }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            if (!state.showResults) {
                QuestionSection(stringResource(R.string.how_do_you_feel)) {
                    UserFeeling.entries.forEach { f ->
                        FilterChip(selected = f in state.feelings, onClick = { viewModel.toggleFeeling(f) }, label = { Text(feelingLabel(f)) }, modifier = Modifier.padding(2.dp))
                    }
                }

                QuestionSection(stringResource(R.string.time_until_sleep)) {
                    Text(String.format("%.1f ساعت", state.hoursUntilSleep))
                    Slider(value = state.hoursUntilSleep, onValueChange = viewModel::setHoursUntilSleep, valueRange = 0.5f..12f)
                }

                QuestionSection(stringResource(R.string.current_activity)) {
                    UserActivity.entries.forEach { a ->
                        FilterChip(selected = state.activity == a, onClick = { viewModel.setActivity(a) }, label = { Text(activityLabel(a)) }, modifier = Modifier.padding(2.dp))
                    }
                }

                QuestionSection(stringResource(R.string.temperature_pref)) {
                    TemperatureType.entries.forEach { t ->
                        FilterChip(selected = state.temperaturePref == t, onClick = { viewModel.setTemperature(t) }, label = { Text(tempLabel(t)) }, modifier = Modifier.padding(2.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))
                PrimaryButton(stringResource(R.string.get_suggestions)) { viewModel.getSuggestions() }
            } else {
                state.suggestions.forEachIndexed { index, suggestion ->
                    SuggestionCard(index + 1, suggestion, onLog = { viewModel.logSuggestion(suggestion, onLogged) })
                    Spacer(Modifier.height(12.dp))
                }
                SecondaryButton(stringResource(R.string.cancel)) { viewModel.reset() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun QuestionSection(title: String, content: @Composable () -> Unit) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) { content() }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun SuggestionCard(rank: Int, suggestion: DrinkSuggestion, onLog: () -> Unit) {
    NooshCard {
        Text("#$rank ${suggestion.drink.icon} ${suggestion.drink.nameFa}", style = MaterialTheme.typography.titleLarge)
        Text(stringResource(R.string.suggestion_score, suggestion.score), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.reason), style = MaterialTheme.typography.labelMedium)
        suggestion.reasons.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.suggested_amount, suggestion.suggestedVolume))
        Text(stringResource(R.string.approx_caffeine, suggestion.approxCaffeine))
        Spacer(Modifier.height(12.dp))
        PrimaryButton(stringResource(R.string.i_consumed), onLog)
    }
}

@Composable
fun feelingLabel(f: UserFeeling): String = when (f) {
    UserFeeling.TIRED -> stringResource(R.string.feel_tired)
    UserFeeling.THIRSTY -> stringResource(R.string.feel_thirsty)
    UserFeeling.FOCUS -> stringResource(R.string.feel_focus)
    UserFeeling.ANXIOUS -> stringResource(R.string.feel_anxious)
    UserFeeling.HOT -> stringResource(R.string.feel_hot)
    UserFeeling.COLD -> stringResource(R.string.feel_cold)
    UserFeeling.REST -> stringResource(R.string.feel_rest)
    UserFeeling.TASTY -> stringResource(R.string.feel_tasty)
}

@Composable
fun activityLabel(a: UserActivity): String = when (a) {
    UserActivity.WORK -> stringResource(R.string.act_work)
    UserActivity.STUDY -> stringResource(R.string.act_study)
    UserActivity.MEETING -> stringResource(R.string.act_meeting)
    UserActivity.DRIVING -> stringResource(R.string.act_driving)
    UserActivity.EXERCISE -> stringResource(R.string.act_exercise)
    UserActivity.REST -> stringResource(R.string.act_rest)
    UserActivity.EATING -> stringResource(R.string.act_eating)
}

@Composable
fun tempLabel(t: TemperatureType): String = when (t) {
    TemperatureType.HOT -> stringResource(R.string.temp_hot)
    TemperatureType.COLD -> stringResource(R.string.temp_cold)
    TemperatureType.ANY -> stringResource(R.string.temp_any)
}
