// Generated using MaterialKolor Builder version 1.3.0 (103)
// https://materialkolor.com/?color_seed=FFFAFAFF&color_primary=FFFAFAFF&color_secondary=FFFFF8EB&color_neutral=FF787778&dark_mode=true&style=Neutral&color_spec=SPEC_2025&package_name=tech.jitendhull.torrnado

package tech.jitendhull.torrnado

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicMaterialThemeState

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val dynamicThemeState = rememberDynamicMaterialThemeState(
        isDark = isDarkTheme,
        style = PaletteStyle.Neutral,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        primary = Primary,
        secondary = Secondary,
        neutral = Neutral,
    )
    
    DynamicMaterialTheme(
        state = dynamicThemeState,
        animate = true,
        content = content,
    )
}