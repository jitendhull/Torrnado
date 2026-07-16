package tech.jitendhull.torrnado.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicMaterialThemeState
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

@Composable
fun TorrnadoTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    accentTheme: AccentTheme = AccentTheme.CYBERPUNK,
    amoledTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val primarySeed = when (accentTheme) {
        AccentTheme.CYBERPUNK -> Color(0xFF8B5CF6)
        AccentTheme.OCEAN -> Color(0xFF0284C7)
        AccentTheme.FOREST -> Color(0xFF059669)
        AccentTheme.SUNSET -> Color(0xFFF97316)
    }

    val paletteStyle = when (accentTheme) {
        AccentTheme.CYBERPUNK -> PaletteStyle.Expressive
        AccentTheme.OCEAN -> PaletteStyle.Vibrant
        AccentTheme.FOREST -> PaletteStyle.TonalSpot
        AccentTheme.SUNSET -> PaletteStyle.FruitSalad
    }

    val dynamicThemeState = rememberDynamicMaterialThemeState(
        seedColor = primarySeed,
        isDark = darkTheme,
        style = paletteStyle
    )

    DynamicMaterialTheme(
        state = dynamicThemeState,
        animate = true,
    ) {
        val baseScheme = MaterialTheme.colorScheme
        val colorScheme = if (darkTheme && amoledTheme) {
            baseScheme.copy(
                background = Color.Black,
                surface = Color(0xFF000000),
                surfaceVariant = Color(0xFF121212),
                scrim = Color.Black,
                onBackground = Color(0xFFECECEC),
                onSurface = Color(0xFFECECEC)
            )
        } else {
            baseScheme
        }

        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}