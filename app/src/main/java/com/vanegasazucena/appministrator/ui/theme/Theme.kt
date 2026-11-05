package com.vanegasazucena.appministrator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = WinePrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = WineLight,
    onPrimaryContainer = SurfaceWhite,
    background = BackgroundGray,
    onBackground = TextMaroon,
    surface = SurfaceWhite,
    onSurface = TextMaroon
)

@Composable
fun AppministratorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
