package com.nooshyar.app.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.R
import com.nooshyar.app.core.ui.components.NooshCard
import com.nooshyar.app.core.ui.components.PrimaryButton
import com.nooshyar.app.core.ui.components.ProgressBar
import com.nooshyar.app.core.ui.components.SecondaryButton
import com.nooshyar.app.core.ui.components.StatCard

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
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(
                                R.string.greeting,
                                state.userName
                            )
                        )

                        Text(
                            text = state.jalaliDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.6f
                            )
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onWhatToDrink,
                icon = {
                    Text(
                        text = "💡"
                    )
                },
                text = {
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = state.dailyInsight,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = state.currentTime,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            state.topSuggestion?.let { suggestion ->
                NooshCard {
                    Text(
                        text = stringResource(
                            R.string.current_suggestion
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row {
                        Text(
                            text = suggestion.drink.icon,
                            style = MaterialTheme.typography.headlineLarge
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column {
                            Text(
                                text = suggestion.drink.nameFa,
                                style = MaterialTheme.typography.titleLarge
                            )

                            suggestion.reasons.forEach { reason ->
                                Text(
                                    text = "• $reason",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.7f
                                    )
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    PrimaryButton(
                        text = stringResource(
                            R.string.i_consumed
                        ),
                        onClick = {
                            viewModel.acceptSuggestion(onLogDrink)
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SecondaryButton(
                            text = stringResource(
                                R.string.another_suggestion
                            ),
                            onClick = {
                                viewModel.refreshSuggestion()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        SecondaryButton(
                            text = stringResource(
                                R.string.not_now
                            ),
                            onClick = {
                                viewModel.rejectSuggestion()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = stringResource(
                        R.string.water_consumed
                    ),
                    value = "${state.waterMl} ml",
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = stringResource(
                        R.string.caffeine_consumed
                    ),
                    value = "${state.caffeineMg} mg",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = stringResource(
                        R.string.drink_count
                    ),
                    value = state.drinkCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = stringResource(
                        R.string.last_drink
                    ),
                    value = state.lastDrinkTime
                        ?: stringResource(R.string.none),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            ProgressBar(
                title = stringResource(
                    R.string.water_progress
                ),
                progress = state.waterProgress,
                label = "${state.waterMl}/${state.waterGoal} ml"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ProgressBar(
                title = stringResource(
                    R.string.caffeine_progress
                ),
                progress = state.caffeineProgress,
                label = "${state.caffeineMg}/${state.caffeineLimit} mg"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            ProgressBar(
                title = stringResource(
                    R.string.time_to_sleep
                ),
                progress = 1f - (
                    state.hoursToSleep / 16f
                    ).coerceIn(0f, 1f),
                label = String.format(
                    "%.1f ساعت",
                    state.hoursToSleep
                )
            )

            Spacer(
                modifier = Modifier.height(80.dp)
            )
        }
    }
}
