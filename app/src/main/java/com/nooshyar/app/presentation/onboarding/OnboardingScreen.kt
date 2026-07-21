package com.nooshyar.app.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nooshyar.app.R
import com.nooshyar.app.core.ui.components.PrimaryButton
import com.nooshyar.app.presentation.navigation.Routes
import kotlinx.coroutines.launch

data class OnboardingPage(val title: Int, val desc: Int, val emoji: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigate: (String) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage(R.string.onboarding_1_title, R.string.onboarding_1_desc, "📝"),
        OnboardingPage(R.string.onboarding_2_title, R.string.onboarding_2_desc, "☕"),
        OnboardingPage(R.string.onboarding_3_title, R.string.onboarding_3_desc, "💡"),
        OnboardingPage(R.string.onboarding_4_title, R.string.onboarding_4_desc, "📊")
    )
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = {
                viewModel.completeOnboarding { onNavigate(Routes.PROFILE_SETUP) }
            }) { Text(stringResource(R.string.skip)) }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(pages[page].emoji, style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(24.dp))
                Text(stringResource(pages[page].title), style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(pages[page].desc), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(pages.size) { i ->
                val selected = pagerState.currentPage == i
                Box(Modifier.padding(4.dp).size(if (selected) 10.dp else 8.dp).then(
                    Modifier.padding(0.dp)
                )) {
                    Surface(shape = MaterialTheme.shapes.small, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), modifier = Modifier.fillMaxSize()) {}
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (pagerState.currentPage < pages.size - 1) {
            PrimaryButton(stringResource(R.string.next)) {
                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            }
        } else {
            PrimaryButton(stringResource(R.string.start_using)) {
                viewModel.completeOnboarding { onNavigate(Routes.PROFILE_SETUP) }
            }
        }
    }
}
