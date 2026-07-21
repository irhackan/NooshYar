package com.nooshyar.app.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.core.ui.components.DeveloperFooter

@Composable
fun SplashScreen(
    onNavigate: (String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()
    LaunchedEffect(destination) {
        destination?.let { onNavigate(it) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(32.dp)
        ) {
            Spacer(Modifier.weight(1f))
            Text("☕", fontSize = 72.sp, modifier = Modifier.scale(scale).alpha(alpha))
            Spacer(Modifier.height(16.dp))
            Text("نوش‌یار", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("هر زمان، نوشیدنی مناسب‌تر", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            DeveloperFooter()
            Spacer(Modifier.height(24.dp))
        }
    }
}
