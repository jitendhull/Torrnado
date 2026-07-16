package tech.jitendhull.torrnado.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    secondary = ElectricViolet,
    background = DeepSlateBg,
    surface = CardSlateBg,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = BorderSlate,
    onSurfaceVariant = TextSecondary
)

@Composable
fun TorrnadoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}