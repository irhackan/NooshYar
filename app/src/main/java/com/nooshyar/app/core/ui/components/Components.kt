package com.nooshyar.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NooshCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            onClick = onClick,
            content = { Column(Modifier.padding(16.dp), content = content) }
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = { Column(Modifier.padding(16.dp), content = content) }
        )
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    NooshCard(modifier = modifier) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        enabled = enabled
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(text)
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}

@Composable
fun ProgressBar(label: String, progress: Float, displayText: String) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(displayText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp), trackColor = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@Composable
fun DeveloperFooter(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("طراح و برنامه‌نویس: کاظم دهناد", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Text("irhackan@gmail.com", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
    }
}
