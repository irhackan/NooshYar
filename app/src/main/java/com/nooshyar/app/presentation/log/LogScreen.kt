package com.nooshyar.app.presentation.log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.R
import com.nooshyar.app.core.ui.components.PrimaryButton
import com.nooshyar.app.core.ui.components.SecondaryButton
import com.nooshyar.app.domain.model.ConsumptionMood
import com.nooshyar.app.domain.model.Drink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    preselectedDrinkId: Long? = null,
    viewModel: LogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(preselectedDrinkId) {
        preselectedDrinkId?.let { drinkId ->
            viewModel.loadDrinkById(drinkId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.log_drink)
                    )
                }
            )
        }
    ) { innerPadding ->
        if (state.selectedDrink != null) {
            LogDetailForm(
                state = state,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LogDrinkList(
                state = state,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun LogDrinkList(
    state: LogUiState,
    viewModel: LogViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::search,
            label = {
                Text(
                    text = stringResource(R.string.search_drinks)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (state.recentDrinks.isNotEmpty()) {
            Text(
                text = stringResource(R.string.recent_drinks),
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.recentDrinks) { drink ->
                    DrinkChip(
                        drink = drink,
                        onClick = {
                            viewModel.selectDrink(drink)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        if (state.popularDrinks.isNotEmpty()) {
            Text(
                text = stringResource(R.string.popular_drinks),
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.popularDrinks) { drink ->
                    DrinkChip(
                        drink = drink,
                        onClick = {
                            viewModel.selectDrink(drink)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(state.drinks) { drink ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = "${drink.icon} ${drink.nameFa}"
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "${drink.category} • ${drink.caffeinePerServing} mg"
                        )
                    },
                    modifier = Modifier.clickable {
                        viewModel.selectDrink(drink)
                    }
                )

                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun DrinkChip(
    drink: Drink,
    onClick: () -> Unit
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Text(
                text = "${drink.icon} ${drink.nameFa}"
            )
        }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun LogDetailForm(
    state: LogUiState,
    viewModel: LogViewModel,
    modifier: Modifier = Modifier
) {
    val drink = state.selectedDrink ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = drink.icon,
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {
                Text(
                    text = drink.nameFa,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = drink.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "${stringResource(R.string.volume)}: ${state.volume} ml"
        )

        Slider(
            value = state.volume.toFloat(),
            onValueChange = {
                viewModel.updateVolume(it.toInt())
            },
            valueRange = 50f..1000f
        )

        Text(
            text = "${stringResource(R.string.servings)}: ${state.servings}"
        )

        Slider(
            value = state.servings.toFloat(),
            onValueChange = {
                viewModel.updateServings(it.toInt())
            },
            valueRange = 1f..5f,
            steps = 4
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = stringResource(R.string.mood),
            style = MaterialTheme.typography.titleMedium
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ConsumptionMood.entries
                .take(6)
                .forEach { mood ->
                    FilterChip(
                        selected = state.mood == mood,
                        onClick = {
                            viewModel.updateMood(mood)
                        },
                        label = {
                            Text(
                                text = moodLabel(mood)
                            )
                        }
                    )
                }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = state.note,
            onValueChange = viewModel::updateNote,
            label = {
                Text(
                    text = stringResource(R.string.note)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        PrimaryButton(
            text = stringResource(R.string.save),
            onClick = {
                viewModel.saveConsumption()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SecondaryButton(
            text = stringResource(R.string.cancel),
            onClick = {
                viewModel.clearSelection()
            }
        )

        state.savedMessage?.let { message ->
            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun moodLabel(
    mood: ConsumptionMood
): String {
    return when (mood) {
        ConsumptionMood.THIRSTY ->
            stringResource(R.string.mood_thirsty)

        ConsumptionMood.TIRED ->
            stringResource(R.string.mood_tired)

        ConsumptionMood.LOW_ENERGY ->
            stringResource(R.string.mood_low_energy)

        ConsumptionMood.ANXIOUS ->
            stringResource(R.string.mood_anxious)

        ConsumptionMood.FOCUS ->
            stringResource(R.string.mood_focus)

        ConsumptionMood.CALM ->
            stringResource(R.string.mood_calm)

        ConsumptionMood.HAPPY ->
            stringResource(R.string.mood_happy)

        ConsumptionMood.BORED ->
            stringResource(R.string.mood_bored)

        ConsumptionMood.AFTER_SPORT ->
            stringResource(R.string.mood_after_sport)

        ConsumptionMood.WITH_FOOD ->
            stringResource(R.string.mood_with_food)

        ConsumptionMood.NONE ->
            stringResource(R.string.mood_none)
    }
}
