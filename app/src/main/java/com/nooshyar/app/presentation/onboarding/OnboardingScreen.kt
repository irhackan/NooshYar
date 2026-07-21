package com.nooshyar.app.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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

data class OnboardingPage(
    val title: Int,
    val desc: Int,
    val emoji: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigate: (String) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage(
            title = R.string.onboarding_1_title,
            desc = R.string.onboarding_1_desc,
            emoji = "📝"
        ),
        OnboardingPage(
            title = R.string.onboarding_2_title,
            desc = R.string.onboarding_2_desc,
            emoji = "☕"
        ),
        OnboardingPage(
            title = R.string.onboarding_3_title,
            desc = R.string.onboarding_3_desc,
            emoji = "💡"
        ),
        OnboardingPage(
            title = R.string.onboarding_4_title,
            desc = R.string.onboarding_4_desc,
            emoji = "📊"
        )
    )

    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = {
                    viewModel.completeOnboarding {
                        onNavigate(Routes.PROFILE_SETUP)
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.skip)
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = pages[page].emoji,
                    style = MaterialTheme.typography.displayLarge
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = stringResource(pages[page].title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = stringResource(pages[page].desc),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.7f
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val selected = pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(
                            if (selected) {
                                10.dp
                            } else {
                                8.dp
                            }
                        )
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.2f
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {}
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (pagerState.currentPage < pages.lastIndex) {
            PrimaryButton(
                text = stringResource(R.string.next),
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
                    }
                }
            )
        } else {
            PrimaryButton(
                text = stringResource(R.string.start_using),
                onClick = {
                    viewModel.completeOnboarding {
                        onNavigate(Routes.PROFILE_SETUP)
                    }
                }
            )
        }
    }
}
