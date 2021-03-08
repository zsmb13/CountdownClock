package co.zsmb.countdownclock.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CountdownClockTheme(
    content: @Composable() () -> Unit
) {
    val colors = lightColors(
        primary = Color(0xFFFF8F00),
        primaryVariant = Color(0xFFE53935),
        secondary = Color(0xFFFFC046),
        background = Color(0xFFDCE775),
    )

    MaterialTheme(
        colors = colors,
        content = content
    )
}
