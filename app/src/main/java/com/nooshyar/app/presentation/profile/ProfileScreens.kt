package com.nooshyar.app.presentation.profile

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.R
import com.nooshyar.app.core.ui.components.DeveloperFooter
import com.nooshyar.app.core.ui.components.NooshCard
import com.nooshyar.app.core.ui.components.PrimaryButton
import com.nooshyar.app.core.ui.components.SecondaryButton
import com.nooshyar.app.core.util.JalaliDate
import com.nooshyar.app.domain.model.ActivityLevel
import com.nooshyar.app.domain.model.CaffeineSensitivity
import com.nooshyar.app.domain.model.PressureLevel
import com.nooshyar.app.domain.model.ThemeMode
import com.nooshyar.app.domain.model.UserGoal
import com.nooshyar.app.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    var step by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.setup_profile)
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
            when (step) {
                0 -> {
                    OutlinedTextField(
                        value = profile.name,
                        onValueChange = viewModel::updateName,
                        label = {
                            Text(
                                text = stringResource(R.string.name)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedTextField(
                        value = profile.age.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let(viewModel::updateAge)
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.age)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    OutlinedTextField(
                        value = profile.weight.toInt().toString(),
                        onValueChange = { value ->
                            value.toFloatOrNull()?.let(viewModel::updateWeight)
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.weight)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "${stringResource(R.string.wake_time)}: ${
                            JalaliDate.formatMinutes(profile.wakeTimeMinutes)
                        }"
                    )

                    Slider(
                        value = profile.wakeTimeMinutes.toFloat(),
                        onValueChange = { value ->
                            viewModel.updateWakeTime(value.toInt())
                        },
                        valueRange = 300f..600f,
                        steps = 10
                    )

                    Text(
                        text = "${stringResource(R.string.sleep_time)}: ${
                            JalaliDate.formatMinutes(profile.sleepTimeMinutes)
                        }"
                    )

                    Slider(
                        value = profile.sleepTimeMinutes.toFloat(),
                        onValueChange = { value ->
                            viewModel.updateSleepTime(value.toInt())
                        },
                        valueRange = 1200f..1560f,
                        steps = 12
                    )
                }

                1 -> {
                    LevelSelector(
                        title = stringResource(R.string.activity_level),
                        options = ActivityLevel.entries,
                        selected = profile.activityLevel,
                        onSelect = viewModel::updateActivity
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    LevelSelector(
                        title = stringResource(R.string.work_pressure),
                        options = PressureLevel.entries,
                        selected = profile.workPressure,
                        onSelect = viewModel::updateWorkPressure
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    LevelSelector(
                        title = stringResource(R.string.caffeine_sensitivity),
                        options = CaffeineSensitivity.entries,
                        selected = profile.caffeineSensitivity,
                        onSelect = viewModel::updateCaffeineSensitivity
                    )
                }

                2 -> {
                    Text(
                        text = stringResource(R.string.goals),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    UserGoal.entries.forEach { goal ->
                        val selected = goal in profile.selectedGoals

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selected,
                                    onClick = {
                                        viewModel.toggleGoal(goal)
                                    }
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selected,
                                onCheckedChange = {
                                    viewModel.toggleGoal(goal)
                                }
                            )

                            Text(
                                text = goalLabel(goal),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (step > 0) {
                    SecondaryButton(
                        text = stringResource(R.string.cancel),
                        onClick = {
                            step--
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                PrimaryButton(
                    text = if (step < 2) {
                        stringResource(R.string.next)
                    } else {
                        stringResource(R.string.save_profile)
                    },
                    onClick = {
                        if (step < 2) {
                            step++
                        } else {
                            viewModel.save {
                                onNavigate(Routes.MAIN)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = stringResource(R.string.health_disclaimer),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                )
            )
        }
    }
}

@Composable
private fun <T> LevelSelector(
    title: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit
) where T : Enum<T> {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = {
                    onSelect(option)
                },
                label = {
                    Text(
                        text = levelLabel(option.name)
                    )
                }
            )
        }
    }
}

@Composable
fun goalLabel(goal: UserGoal): String {
    return when (goal) {
        UserGoal.INCREASE_WATER ->
            stringResource(R.string.goal_water)

        UserGoal.CONTROL_CAFFEINE ->
            stringResource(R.string.goal_caffeine)

        UserGoal.IMPROVE_SLEEP ->
            stringResource(R.string.goal_sleep)

        UserGoal.REDUCE_SUGAR ->
            stringResource(R.string.goal_sugar)

        UserGoal.INCREASE_FOCUS ->
            stringResource(R.string.goal_focus)

        UserGoal.REDUCE_ENERGY ->
            stringResource(R.string.goal_energy)

        UserGoal.TRACK_HABITS ->
            stringResource(R.string.goal_habit)

        UserGoal.BALANCE ->
            stringResource(R.string.goal_balance)
    }
}

@Composable
fun levelLabel(name: String): String {
    return when (name) {
        "LOW" -> stringResource(R.string.level_low)
        "MEDIUM" -> stringResource(R.string.level_medium)
        "HIGH" -> stringResource(R.string.level_high)
        else -> name
    }
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

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showImportDialog by remember {
        mutableStateOf(false)
    }

    var importText by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        viewModel.exportJson.collect { json ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"

                putExtra(
                    Intent.EXTRA_SUBJECT,
                    "NooshYar Backup"
                )

                putExtra(
                    Intent.EXTRA_TEXT,
                    json
                )
            }

            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.export_backup)
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.message.collect { message ->
            when {
                message == "backup_ready" -> {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.backup_success)
                    )
                }

                message.startsWith("imported:") -> {
                    val importedCount = message
                        .removePrefix("imported:")
                        .toIntOrNull()
                        ?: 0

                    snackbarHostState.showSnackbar(
                        context.getString(
                            R.string.import_success,
                            importedCount
                        )
                    )
                }

                message == "import_failed" -> {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.import_failed)
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile)
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
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
            profile?.let { currentProfile ->
                NooshCard {
                    Text(
                        text = currentProfile.name,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "${stringResource(R.string.water_goal)}: " +
                            "${currentProfile.waterDailyGoal} ml"
                    )

                    Text(
                        text = "${stringResource(R.string.caffeine_limit)}: " +
                            "${currentProfile.caffeineDailyLimit} mg"
                    )

                    Text(
                        text = "${stringResource(R.string.wake_time)}: ${
                            JalaliDate.formatMinutes(
                                currentProfile.wakeTimeMinutes
                            )
                        }"
                    )

                    Text(
                        text = "${stringResource(R.string.sleep_time)}: ${
                            JalaliDate.formatMinutes(
                                currentProfile.sleepTimeMinutes
                            )
                        }"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = {
                            viewModel.setTheme(mode)
                        },
                        label = {
                            Text(
                                text = when (mode) {
                                    ThemeMode.LIGHT ->
                                        stringResource(R.string.theme_light)

                                    ThemeMode.DARK ->
                                        stringResource(R.string.theme_dark)

                                    ThemeMode.SYSTEM ->
                                        stringResource(R.string.theme_system)
                                }
                            )
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            SecondaryButton(
                text = stringResource(R.string.export_backup),
                onClick = {
                    viewModel.exportBackup()
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            SecondaryButton(
                text = stringResource(R.string.import_backup),
                onClick = {
                    showImportDialog = true
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            SecondaryButton(
                text = stringResource(R.string.about),
                onClick = onNavigateAbout
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            SecondaryButton(
                text = stringResource(R.string.setup_profile),
                onClick = onNavigateSetup
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = {
                    showDeleteDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = stringResource(R.string.delete_all_data)
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            DeveloperFooter()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text(
                    text = stringResource(R.string.delete_all_data)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.confirm_delete_all)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAllData(onNavigateSetup)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.yes)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.no)
                    )
                }
            }
        )
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = {
                showImportDialog = false
            },
            title = {
                Text(
                    text = stringResource(R.string.import_backup)
                )
            },
            text = {
                OutlinedTextField(
                    value = importText,
                    onValueChange = { value ->
                        importText = value
                    },
                    label = {
                        Text(
                            text = stringResource(
                                R.string.paste_backup_json
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportDialog = false
                        viewModel.importBackup(importText) {}
                        importText = ""
                    }
                ) {
                    Text(
                        text = stringResource(R.string.save)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImportDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.cancel)
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.about_title)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Text(
                            text = "→"
                        )
                    }
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
            Text(
                text = stringResource(R.string.about_desc),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = stringResource(R.string.developer_credit),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(R.string.developer_email),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = stringResource(
                    R.string.version,
                    "1.1.0"
                )
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = stringResource(R.string.health_disclaimer),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                )
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = stringResource(R.string.privacy_policy),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(R.string.privacy_text),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            DeveloperFooter()
        }
    }
}
