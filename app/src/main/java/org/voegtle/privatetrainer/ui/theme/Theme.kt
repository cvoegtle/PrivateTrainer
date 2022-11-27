package org.voegtle.privatetrainer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val privateTrainerDarkColorScheme = darkColorScheme(
    primary = privateDarkPrimary,
    onPrimary = privateDarkOnPrimary,
    primaryContainer = privateDarkPrimaryContainer,
    onPrimaryContainer = privateDarkOnPrimaryContainer,
    inversePrimary = privateDarkPrimaryInverse,
    secondary = privateDarkSecondary,
    onSecondary = privateDarkOnSecondary,
    secondaryContainer = privateDarkSecondaryContainer,
    onSecondaryContainer = privateDarkOnSecondaryContainer,
    tertiary = privateDarkTertiary,
    onTertiary = privateDarkOnTertiary,
    tertiaryContainer = privateDarkTertiaryContainer,
    onTertiaryContainer = privateDarkOnTertiaryContainer,
    error = privateDarkError,
    onError = privateDarkOnError,
    errorContainer = privateDarkErrorContainer,
    onErrorContainer = privateDarkOnErrorContainer,
    background = privateDarkBackground,
    onBackground = privateDarkOnBackground,
    surface = privateDarkSurface,
    onSurface = privateDarkOnSurface,
    inverseSurface = privateDarkInverseSurface,
    inverseOnSurface = privateDarkInverseOnSurface,
    surfaceVariant = privateDarkSurfaceVariant,
    onSurfaceVariant = privateDarkOnSurfaceVariant,
    outline = privateDarkOutline
)

private val privateTrainerLightColorScheme = lightColorScheme(
    primary = privateLightPrimary,
    onPrimary = privateLightOnPrimary,
    primaryContainer = privateLightPrimaryContainer,
    onPrimaryContainer = privateLightOnPrimaryContainer,
    inversePrimary = privateLightPrimaryInverse,
    secondary = privateLightSecondary,
    onSecondary = privateLightOnSecondary,
    secondaryContainer = privateLightSecondaryContainer,
    onSecondaryContainer = privateLightOnSecondaryContainer,
    tertiary = privateLightTertiary,
    onTertiary = privateLightOnTertiary,
    tertiaryContainer = privateLightTertiaryContainer,
    onTertiaryContainer = privateLightOnTertiaryContainer,
    error = privateLightError,
    onError = privateLightOnError,
    errorContainer = privateLightErrorContainer,
    onErrorContainer = privateLightOnErrorContainer,
    background = privateLightBackground,
    onBackground = privateLightOnBackground,
    surface = privateLightSurface,
    onSurface = privateLightOnSurface,
    inverseSurface = privateLightInverseSurface,
    inverseOnSurface = privateLightInverseOnSurface,
    surfaceVariant = privateLightSurfaceVariant,
    onSurfaceVariant = privateLightOnSurfaceVariant,
    outline = privateLightOutline
)

@Composable
fun PrivateTrainerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val privateColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> privateTrainerDarkColorScheme
        else -> privateTrainerLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = privateColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = privateColorScheme,
        typography = privateTypography,
        shapes = shapes,
        content = content
    )
}
