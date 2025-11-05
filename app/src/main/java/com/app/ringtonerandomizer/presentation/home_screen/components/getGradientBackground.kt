package com.app.ringtonerandomizer.presentation.home_screen.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun getGradientBackground(): Brush {
    val infiniteTransition = rememberInfiniteTransition()

    val color1 = infiniteTransition.animateColor(
        initialValue = Color.Transparent,
        targetValue = MaterialTheme.colorScheme.tertiaryContainer,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3 * 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color2 = infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.tertiaryContainer,
        targetValue = MaterialTheme.colorScheme.secondaryContainer,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3 * 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color3 = infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.secondaryContainer,
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3 * 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    return Brush.horizontalGradient(colors = listOf(color1.value, color2.value, color3.value))
}