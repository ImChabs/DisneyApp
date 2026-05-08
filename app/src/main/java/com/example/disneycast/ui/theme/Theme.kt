package com.example.disneycast.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DisneyColors.Gold,
    onPrimary = DisneyColors.Ink,
    secondary = DisneyColors.Lavender,
    tertiary = DisneyColors.Orchid,
    background = DisneyColors.Midnight,
    surface = DisneyColors.Ink,
    surfaceContainer = DisneyColors.InkElevated,
    onBackground = DisneyColors.TextPrimaryOnDark,
    onSurface = DisneyColors.TextPrimaryOnDark,
)

private val LightColorScheme = lightColorScheme(
    primary = DisneyColors.Violet,
    onPrimary = Color.White,
    secondary = DisneyColors.RoyalBlue,
    tertiary = DisneyColors.GoldDeep,
    background = DisneyColors.MaterialBackgroundLight,
    surface = DisneyColors.MaterialBackgroundLight,
    surfaceContainer = Color.White,
    onBackground = DisneyColors.MaterialOnBackgroundLight,
    onSurface = DisneyColors.MaterialOnBackgroundLight,

    /*
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    */
)

@Composable
fun DisneyCastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
