package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TradingDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = BackgroundDark,
    primaryContainer = CardSurfaceVariant,
    onPrimaryContainer = GoldPrimary,
    secondary = BuyGreen,
    onSecondary = TextPrimary,
    secondaryContainer = BuyGreenContainer,
    onSecondaryContainer = BuyGreen,
    tertiary = SellRed,
    onTertiary = TextPrimary,
    tertiaryContainer = SellRedContainer,
    onTertiaryContainer = SellRed,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = CardSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // We default to sleek trading dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = TradingDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = BackgroundDark.toArgb()
                window.navigationBarColor = BackgroundDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
