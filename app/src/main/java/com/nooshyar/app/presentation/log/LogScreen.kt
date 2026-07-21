package com.nooshyar.app.presentation.log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.R
import com.nooshyar.app.core.ui.components.*
import com.nooshyar.app.domain.model.ConsumptionMood
import com.nooshyar.app.domain.model.Drink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    preselectedDrinkId: Long? = null,
    viewModel: LogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(preselectedDrinkId) { preselectedDrinkId?.let { viewModel.loadDrinkById(it) } }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.log_drink)) }) }) { padding ->
        if (state.selectedDrink != null) {
            LogDetailForm(state, viewModel, Modifier.padding(padding))
        } else {
            LogDrinkList(state, viewModel, Modifier.padding(padding))
        }
    }
}

@Composable
private fun LogDrinkList(state: LogUiState, viewModel: LogViewModel, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::search,
            label = { Text(stringResource(R.string.search_drinks)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        if (state.recentDrinks.isNotEmpty()) {
            Text(stringResource(R.string.recent_drinks), style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.recentDrinks) { drink -> DrinkChip(drink) { viewModel.selectDrink(drink) } }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (state.popularDrinks.isNotEmpty()) {
            Text(stringResource(R.string.popular_drinks), style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.popularDrinks) { drink -> DrinkChip(drink) { viewModel.selectDrink(drink) } }
            }
            Spacer(Modifier.height(12.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(state.drinks) { drink ->
                ListItem(
                    headlineContent = { Text("${drink.icon} ${drink.nameFa}") },
                    supportingContent = { Text("${drink.category} • ${drink.caffeinePerServing} mg") },
                    modifier = Modifier.clickable { viewModel.selectDrink(drink) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun DrinkChip(drink: Drink, onClick: () -> Unit) {
    FilterChip(selected = false, onClick = onClick, label = { Text("${drink.icon} ${drink.nameFa}") })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun LogDetailForm(state: LogUiState, viewModel: LogViewModel, modifier: Modifier = Modifier) {
    val drink = state.selectedDrink ?: return
    Column(modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(drink.icon, style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(drink.nameFa, style = MaterialTheme.typography.headlineMedium)
                Text(drink.description, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("${stringResource(R.string.volume)}: ${state.volume} ml")
        Slider(value = state.volume.toFloat(), onValueChange = { viewModel.updateVolume(it.toInt()) }, valueRange = 50f..1000f)
        Text("${stringResource(R.string.servings)}: ${state.servings}")
        Slider(value = state.servings.toFloat(), onValueChange = { viewModel.updateServings(it.toInt()) }, valueRange = 1f..5f, steps = 4)

        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.mood), style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ConsumptionMood.entries.take(6).forEach { mood ->
                FilterChip(selected = state.mood == mood, onClick = { viewModel.updateMood(mood) }, label = { Text(moodLabel(mood)) })
            }
        }

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = state.note, onValueChange = viewModel::updateNote, label = { Text(stringResource(R.string.note)) }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        PrimaryButton(stringResource(R.string.save)) { viewModel.saveConsumption() }
        Spacer(Modifier.height(8.dp))
        SecondaryButton(stringResource(R.string.cancel)) { viewModel.clearSelection() }

        state.savedMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun moodLabel(mood: ConsumptionMood): String = when (mood) {
    ConsumptionMood.THIRSTY -> stringResource(R.string.mood_thirsty)
    ConsumptionMood.TIRED -> stringResource(R.string.mood_tired)
    ConsumptionMood.LOW_ENERGY -> stringResource(R.string.mood_low_energy)
    ConsumptionMood.ANXIOUS -> stringResource(R.string.mood_anxious)
    ConsumptionMood.FOCUS -> stringResource(R.string.mood_focus)
    ConsumptionMood.CALM -> stringResource(R.string.mood_calm)
    ConsumptionMood.HAPPY -> stringResource(R.string.mood_happy)
    ConsumptionMood.BORED -> stringResource(R.string.mood_bored)
    ConsumptionMood.AFTER_SPORT -> stringResource(R.string.mood_after_sport)
    ConsumptionMood.WITH_FOOD -> stringResource(R.string.mood_with_food)
    ConsumptionMood.NONE -> stringResource(R.string.mood_none)
}
