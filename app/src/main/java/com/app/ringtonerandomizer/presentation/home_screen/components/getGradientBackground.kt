package com.app.ringtonerandomizer.presentation.home_screen.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun getGradientBackground() =
    Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
            Color.Transparent
        )
    )