package com.nooshyar.app.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import com.nooshyar.app.core.util.JalaliDate
import com.nooshyar.app.domain.model.*
import com.nooshyar.app.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(onNavigate: (String) -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val profile by viewModel.profile.collectAsState()
    var step by remember { mutableIntStateOf(0) }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.setup_profile)) }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            when (step) {
                0 -> {
                    OutlinedTextField(value = profile.name, onValueChange = viewModel::updateName, label = { Text(stringResource(R.string.name)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = profile.age.toString(), onValueChange = { it.toIntOrNull()?.let(viewModel::updateAge) }, label = { Text(stringResource(R.string.age)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = profile.weight.toInt().toString(), onValueChange = { it.toFloatOrNull()?.let(viewModel::updateWeight) }, label = { Text(stringResource(R.string.weight)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    Text("${stringResource(R.string.wake_time)}: ${JalaliDate.formatMinutes(profile.wakeTimeMinutes)}")
                    Slider(value = profile.wakeTimeMinutes.toFloat(), onValueChange = { viewModel.updateWakeTime(it.toInt()) }, valueRange = 300f..600f, steps = 10)
                    Text("${stringResource(R.string.sleep_time)}: ${JalaliDate.formatMinutes(profile.sleepTimeMinutes)}")
                    Slider(value = profile.sleepTimeMinutes.toFloat(), onValueChange = { viewModel.updateSleepTime(it.toInt()) }, valueRange = 1200f..1560f, steps = 12)
                }
                1 -> {
                    LevelSelector(stringResource(R.string.activity_level), ActivityLevel.entries, profile.activityLevel) { viewModel.updateActivity(it) }
                    Spacer(Modifier.height(16.dp))
                    LevelSelector(stringResource(R.string.work_pressure), PressureLevel.entries, profile.workPressure) { viewModel.updateWorkPressure(it) }
                    Spacer(Modifier.height(16.dp))
                    LevelSelector(stringResource(R.string.caffeine_sensitivity), CaffeineSensitivity.entries, profile.caffeineSensitivity) { viewModel.updateCaffeineSensitivity(it) }
                }
                2 -> {
                    Text(stringResource(R.string.goals), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    UserGoal.entries.forEach { goal ->
                        val label = goalLabel(goal)
                        Row(Modifier.fillMaxWidth().selectable(selected = goal in profile.selectedGoals, onClick = { viewModel.toggleGoal(goal) }).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = goal in profile.selectedGoals, onCheckedChange = { viewModel.toggleGoal(goal) })
                            Text(label, Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (step > 0) SecondaryButton(stringResource(R.string.cancel), { step-- }, Modifier.weight(1f))
                PrimaryButton(if (step < 2) stringResource(R.string.next) else stringResource(R.string.save_profile), {
                    if (step < 2) step++ else viewModel.save { onNavigate(Routes.MAIN) }
                }, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.health_disclaimer), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun <T> LevelSelector(title: String, options: List<T>, selected: T, onSelect: (T) -> Unit) where T : Enum<T> {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { opt ->
            FilterChip(selected = opt == selected, onClick = { onSelect(opt) }, label = { Text(levelLabel(opt.name)) })
        }
    }
}

@Composable
fun goalLabel(goal: UserGoal): String = when (goal) {
    UserGoal.INCREASE_WATER -> stringResource(R.string.goal_water)
    UserGoal.CONTROL_CAFFEINE -> stringResource(R.string.goal_caffeine)
    UserGoal.IMPROVE_SLEEP -> stringResource(R.string.goal_sleep)
    UserGoal.REDUCE_SUGAR -> stringResource(R.string.goal_sugar)
    UserGoal.INCREASE_FOCUS -> stringResource(R.string.goal_focus)
    UserGoal.REDUCE_ENERGY -> stringResource(R.string.goal_energy)
    UserGoal.TRACK_HABITS -> stringResource(R.string.goal_habit)
    UserGoal.BALANCE -> stringResource(R.string.goal_balance)
}

@Composable
fun levelLabel(name: String): String = when (name) {
    "LOW" -> stringResource(R.string.level_low)
    "MEDIUM" -> stringResource(R.string.level_medium)
    "HIGH" -> stringResource(R.string.level_high)
    else -> name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateAbout: () -> Unit,
    onNavigateSetup: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.exportJson.collect { json ->
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(android.content.Intent.EXTRA_SUBJECT, "NooshYar Backup")
                putExtra(android.content.Intent.EXTRA_TEXT, json)
            }
            context.startActivity(android.content.Intent.createChooser(intent, context.getString(R.string.export_backup)))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            when {
                msg == "backup_ready" -> snackbarHostState.showSnackbar(context.getString(R.string.backup_success))
                msg.startsWith("imported:") -> {
                    val count = msg.removePrefix("imported:")
                    snackbarHostState.showSnackbar(context.getString(R.string.import_success, count.toIntOrNull() ?: 0))
                }
                msg == "import_failed" -> snackbarHostState.showSnackbar(context.getString(R.string.import_failed))
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.profile)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            profile?.let { p ->
                NooshCard {
                    Text(p.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("${stringResource(R.string.water_goal)}: ${p.waterDailyGoal} ml")
                    Text("${stringResource(R.string.caffeine_limit)}: ${p.caffeineDailyLimit} mg")
                    Text("${stringResource(R.string.wake_time)}: ${JalaliDate.formatMinutes(p.wakeTimeMinutes)}")
                    Text("${stringResource(R.string.sleep_time)}: ${JalaliDate.formatMinutes(p.sleepTimeMinutes)}")
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.theme), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(selected = themeMode == mode, onClick = { viewModel.setTheme(mode) }, label = {
                        Text(when (mode) {
                            ThemeMode.LIGHT -> stringResource(R.string.theme_light)
                            ThemeMode.DARK -> stringResource(R.string.theme_dark)
                            ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
                        })
                    })
                }
            }

            Spacer(Modifier.height(16.dp))
            SecondaryButton(stringResource(R.string.export_backup)) { viewModel.exportBackup() }
            Spacer(Modifier.height(8.dp))
            SecondaryButton(stringResource(R.string.import_backup)) { showImportDialog = true }
            Spacer(Modifier.height(8.dp))
            SecondaryButton(stringResource(R.string.about), onNavigateAbout)
            Spacer(Modifier.height(8.dp))
            SecondaryButton(stringResource(R.string.setup_profile), onNavigateSetup)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete_all_data))
            }

            Spacer(Modifier.height(24.dp))
            DeveloperFooter()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_all_data)) },
            text = { Text(stringResource(R.string.confirm_delete_all)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteAllData(onNavigateSetup)
                }) { Text(stringResource(R.string.yes)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.no)) }
            }
        )
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(stringResource(R.string.import_backup)) },
            text = {
                OutlinedTextField(
                    value = importText,
                    onValueChange = { importText = it },
                    label = { Text(stringResource(R.string.paste_backup_json)) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showImportDialog = false
                    viewModel.importBackup(importText) { }
                    importText = ""
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.about_title)) }, navigationIcon = {
            IconButton(onClick = onBack) { Text("→") }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text(stringResource(R.string.about_desc), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.developer_credit), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.developer_email), color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.version, "1.1.0"))
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.health_disclaimer), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.privacy_policy), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.privacy_text), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(32.dp))
            DeveloperFooter()
        }
    }
}
