package com.nooshyar.app.presentation.suggest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.R
import com.nooshyar.app.core.ui.components.NooshCard
import com.nooshyar.app.core.ui.components.PrimaryButton
import com.nooshyar.app.core.ui.components.SecondaryButton
import com.nooshyar.app.domain.model.DrinkSuggestion
import com.nooshyar.app.domain.model.TemperatureType
import com.nooshyar.app.domain.model.UserActivity
import com.nooshyar.app.domain.model.UserFeeling

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun SuggestScreen(
    onLogged: () -> Unit = {},
    viewModel: SuggestViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            R.string.what_to_drink_now
                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (!state.showResults) {
                QuestionSection(
                    title = stringResource(R.string.how_do_you_feel)
                ) {
                    UserFeeling.entries.forEach { feeling ->
                        FilterChip(
                            selected = feeling in state.feelings,
                            onClick = {
                                viewModel.toggleFeeling(feeling)
                            },
                            label = {
                                Text(
                                    text = feelingLabel(feeling)
                                )
                            },
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

                QuestionSection(
                    title = stringResource(R.string.time_until_sleep)
                ) {
                    Text(
                        text = String.format(
                            "%.1f ساعت",
                            state.hoursUntilSleep
                        )
                    )

                    Slider(
                        value = state.hoursUntilSleep,
                        onValueChange = viewModel::setHoursUntilSleep,
                        valueRange = 0.5f..12f
                    )
                }

                QuestionSection(
                    title = stringResource(R.string.current_activity)
                ) {
                    UserActivity.entries.forEach { activity ->
                        FilterChip(
                            selected = state.activity == activity,
                            onClick = {
                                viewModel.setActivity(activity)
                            },
                            label = {
                                Text(
                                    text = activityLabel(activity)
                                )
                            },
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

                QuestionSection(
                    title = stringResource(R.string.temperature_pref)
                ) {
                    TemperatureType.entries.forEach { temperature ->
                        FilterChip(
                            selected = state.temperaturePref == temperature,
                            onClick = {
                                viewModel.setTemperature(temperature)
                            },
                            label = {
                                Text(
                                    text = tempLabel(temperature)
                                )
                            },
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                PrimaryButton(
                    text = stringResource(R.string.get_suggestions),
                    onClick = {
                        viewModel.getSuggestions()
                    }
                )
            } else {
                state.suggestions.forEachIndexed { index, suggestion ->
                    SuggestionCard(
                        rank = index + 1,
                        suggestion = suggestion,
                        onLog = {
                            viewModel.logSuggestion(
                                suggestion = suggestion,
                                onDone = onLogged
                            )
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }

                SecondaryButton(
                    text = stringResource(R.string.cancel),
                    onClick = {
                        viewModel.reset()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestionSection(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(
        modifier = Modifier.height(8.dp)
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        content()
    }

    Spacer(
        modifier = Modifier.height(16.dp)
    )
}

@Composable
private fun SuggestionCard(
    rank: Int,
    suggestion: DrinkSuggestion,
    onLog: () -> Unit
) {
    NooshCard {
        Text(
            text = "#$rank ${suggestion.drink.icon} ${suggestion.drink.nameFa}",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(
                R.string.suggestion_score,
                suggestion.score
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = stringResource(R.string.reason),
            style = MaterialTheme.typography.labelMedium
        )

        suggestion.reasons.forEach { reason ->
            Text(
                text = "• $reason",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = stringResource(
                R.string.suggested_amount,
                suggestion.suggestedVolume
            )
        )

        Text(
            text = stringResource(
                R.string.approx_caffeine,
                suggestion.approxCaffeine
            )
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        PrimaryButton(
            text = stringResource(R.string.i_consumed),
            onClick = onLog
        )
    }
}

@Composable
fun feelingLabel(
    feeling: UserFeeling
): String {
    return when (feeling) {
        UserFeeling.TIRED ->
            stringResource(R.string.feel_tired)

        UserFeeling.THIRSTY ->
            stringResource(R.string.feel_thirsty)

        UserFeeling.FOCUS ->
            stringResource(R.string.feel_focus)

        UserFeeling.ANXIOUS ->
            stringResource(R.string.feel_anxious)

        UserFeeling.HOT ->
            stringResource(R.string.feel_hot)

        UserFeeling.COLD ->
            stringResource(R.string.feel_cold)

        UserFeeling.REST ->
            stringResource(R.string.feel_rest)

        UserFeeling.TASTY ->
            stringResource(R.string.feel_tasty)
    }
}

@Composable
fun activityLabel(
    activity: UserActivity
): String {
    return when (activity) {
        UserActivity.WORK ->
            stringResource(R.string.act_work)

        UserActivity.STUDY ->
            stringResource(R.string.act_study)

        UserActivity.MEETING ->
            stringResource(R.string.act_meeting)

        UserActivity.DRIVING ->
            stringResource(R.string.act_driving)

        UserActivity.EXERCISE ->
            stringResource(R.string.act_exercise)

        UserActivity.REST ->
            stringResource(R.string.act_rest)

        UserActivity.EATING ->
            stringResource(R.string.act_eating)
    }
}

@Composable
fun tempLabel(
    temperature: TemperatureType
): String {
    return when (temperature) {
        TemperatureType.HOT ->
            stringResource(R.string.temp_hot)

        TemperatureType.COLD ->
            stringResource(R.string.temp_cold)

        TemperatureType.ANY ->
            stringResource(R.string.temp_any)
    }
}
