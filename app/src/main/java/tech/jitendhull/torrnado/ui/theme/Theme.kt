package tech.jitendhull.torrnado.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import tech.jitendhull.torrnado.domain.model.AccentTheme
import tech.jitendhull.torrnado.domain.model.AppTheme

val CyberCyan = Color(0xFF00F0FF)
val ElectricViolet = Color(0xFF8B5CF6)
val DeepSlateBg = Color(0xFF0F172A)
val CardSlateBg = Color(0xFF1E293B)
val BorderSlate = Color(0xFF334155)
val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)

val EmeraldHealthy = Color(0xFF10B981)
val RoseDead = Color(0xFFEF4444)
val AmberWarning = Color(0xFFF59E0B)

// Cyberpunk (Cyan/Violet)
private val CyberpunkDark = darkColorScheme(
    primary = Color(0xFF00F0FF),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFF10B981),
    error = Color(0xFFEF4444),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val CyberpunkLight = lightColorScheme(
    primary = Color(0xFF0D9488),
    secondary = Color(0xFF7C3AED),
    tertiary = Color(0xFF059669),
    error = Color(0xFFDC2626),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF64748B)
)

// Ocean Breeze (Teal/Blue)
private val OceanDark = darkColorScheme(
    primary = Color(0xFF38BDF8),
    secondary = Color(0xFF60A5FA),
    tertiary = Color(0xFF34D399),
    error = Color(0xFFF87171),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFF8FAFB),
    onSurface = Color(0xFFF8FAFB),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val OceanLight = lightColorScheme(
    primary = Color(0xFF0284C7),
    secondary = Color(0xFF2563EB),
    tertiary = Color(0xFF059669),
    error = Color(0xFFDC2626),
    background = Color(0xFFF0F9FF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE0F2FE),
    onSurfaceVariant = Color(0xFF0369A1)
)

// Emerald Mint (Green/Teal)
private val ForestDark = darkColorScheme(
    primary = Color(0xFF34D399),
    secondary = Color(0xFF059669),
    tertiary = Color(0xFF6EE7B7),
    error = Color(0xFFF87171),
    background = Color(0xFF022C22),
    surface = Color(0xFF064E3B),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFECFDF5),
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = Color(0xFF0F5B46),
    onSurfaceVariant = Color(0xFFA7F3D0)
)

private val ForestLight = lightColorScheme(
    primary = Color(0xFF059669),
    secondary = Color(0xFF047857),
    tertiary = Color(0xFF10B981),
    error = Color(0xFFDC2626),
    background = Color(0xFFF0FDF4),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF064E3B),
    onSurface = Color(0xFF064E3B),
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFF065F46)
)

// Amber Sunset (Orange/Rose)
private val SunsetDark = darkColorScheme(
    primary = Color(0xFFF97316),
    secondary = Color(0xFFF43F5E),
    tertiary = Color(0xFFF59E0B),
    error = Color(0xFFEF4444),
    background = Color(0xFF1E1B4B),
    surface = Color(0xFF311042),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFFFF1F2),
    onSurface = Color(0xFFFFF1F2),
    surfaceVariant = Color(0xFF4C1D95),
    onSurfaceVariant = Color(0xFFFDA4AF)
)

private val SunsetLight = lightColorScheme(
    primary = Color(0xFFD97706),
    secondary = Color(0xFFE11D48),
    tertiary = Color(0xFFD97706),
    error = Color(0xFFDC2626),
    background = Color(0xFFFFF7ED),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF4C1D95),
    onSurface = Color(0xFF4C1D95),
    surfaceVariant = Color(0xFFFFE4E6),
    onSurfaceVariant = Color(0xFF9F1239)
)

@Composable
fun TorrnadoTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    accentTheme: AccentTheme = AccentTheme.CYBERPUNK,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val colorScheme = when (accentTheme) {
        AccentTheme.CYBERPUNK -> if (darkTheme) CyberpunkDark else CyberpunkLight
        AccentTheme.OCEAN -> if (darkTheme) OceanDark else OceanLight
        AccentTheme.FOREST -> if (darkTheme) ForestDark else ForestLight
        AccentTheme.SUNSET -> if (darkTheme) SunsetDark else SunsetLight
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}