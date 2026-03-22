package com.kroman.bookshelf.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = PrimaryDim,
    onPrimary = Color.White,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    surfaceContainer = SurfaceContainer,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerHigh = SurfaceContainerHigh,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outlineVariant = OutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = PrimaryDim,
    onPrimary = Color.White,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    surfaceContainer = SurfaceContainer,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerHigh = SurfaceContainerHigh,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outlineVariant = OutlineVariant
)

private val BookshelfShapes = Shapes(
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
@Suppress("UNUSED_PARAMETER")
fun BookshelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = BookshelfShapes,
        content = content
    )
}
