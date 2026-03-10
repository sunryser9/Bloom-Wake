package com.bloomwake.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Palette ────────────────────────────────────────────────────────────────────
val RoseQuartz    = Color(0xFFE8A0B4)
val DeepPlum      = Color(0xFF5E3A5A)
val SoftLavender  = Color(0xFFCDB4DB)
val WarmCream     = Color(0xFFFFF8F0)
val SageGreen     = Color(0xFF8FB996)
val MidnightBlue  = Color(0xFF0A1628)
val DeepNight     = Color(0xFF050D1A)
val CoralFlame    = Color(0xFFFF6B35)

private val DarkColorScheme = darkColorScheme(
    primary           = RoseQuartz,
    onPrimary         = Color(0xFF3D1A2E),
    primaryContainer  = DeepPlum,
    onPrimaryContainer = RoseQuartz,
    secondary         = SageGreen,
    onSecondary       = Color(0xFF0A2710),
    secondaryContainer = Color(0xFF1E3B22),
    onSecondaryContainer = Color(0xFFC8E6C9),
    tertiary          = Color(0xFFFFD54F),
    onTertiary        = Color(0xFF3A2800),
    tertiaryContainer = Color(0xFF4A3500),
    onTertiaryContainer = Color(0xFFFFD54F),
    background        = MidnightBlue,
    onBackground      = Color(0xFFF0E8F4),
    surface           = Color(0xFF12203A),
    onSurface         = Color(0xFFEDE0F0),
    surfaceVariant    = Color(0xFF1E2D48),
    onSurfaceVariant  = Color(0xFFBDB0C8),
    outline           = Color(0xFF4A3D5C),
    error             = Color(0xFFCF6679),
    onError           = Color(0xFF370B1E)
)

private val LightColorScheme = lightColorScheme(
    primary           = DeepPlum,
    onPrimary         = Color.White,
    primaryContainer  = SoftLavender,
    onPrimaryContainer = DeepPlum,
    secondary         = SageGreen,
    onSecondary       = Color.White,
    secondaryContainer = Color(0xFFD8EDDB),
    onSecondaryContainer = Color(0xFF1B3B20),
    tertiary          = Color(0xFFB5838D),
    background        = WarmCream,
    onBackground      = Color(0xFF2D1F29),
    surface           = Color(0xFFFFF8F0),
    onSurface         = Color(0xFF2D1F29),
    surfaceVariant    = Color(0xFFF3E5F5),
    onSurfaceVariant  = Color(0xFF4A3D5C),
    outline           = Color(0xFFB39DCA)
)

@Composable
fun BloomWakeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content
    )
}
