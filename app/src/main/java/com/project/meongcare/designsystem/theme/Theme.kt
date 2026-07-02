package com.project.meongcare.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme =
    lightColorScheme(
        primary = Main4,
        onPrimary = White,
        secondary = Main3,
        onSecondary = White,
        background = Gray2,
        onBackground = Black,
        surface = White,
        surfaceTint = Color.Transparent,
        onSurface = Black,
        error = Sub1,
        onError = White,
    )

@Composable
fun SemobanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content,
    )
}
