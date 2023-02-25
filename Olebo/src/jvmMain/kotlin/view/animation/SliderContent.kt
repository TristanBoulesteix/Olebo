package jdr.exia.view.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val ANIMATION_DURATION_MILLIS = 220

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <S> SliderContent(
    targetState: S,
    isBackAction: AnimatedContentScope<S>.() -> Boolean = { false },
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(targetState: S) -> Unit
) = AnimatedContent(
    targetState = targetState,
    transitionSpec = {
        val directionModifier = if (!isBackAction()) 1 else -1

        slideInHorizontally(
            animationSpec = tween(ANIMATION_DURATION_MILLIS),
            initialOffsetX = { it * directionModifier }
        ) with slideOutHorizontally(
            animationSpec = tween(ANIMATION_DURATION_MILLIS),
            targetOffsetX = { it * -directionModifier }
        )
    },
    content = content,
    modifier = modifier
)