package co.zsmb.countdownclock

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import co.zsmb.countdownclock.ui.theme.CountdownClockTheme

import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountdownClockTheme {
                Surface(color = MaterialTheme.colors.background) {
                    ClockScreen()
                }
            }
        }
    }
}

data class Time(val hours: Int, val minutes: Int, val seconds: Int) {
    operator fun dec(): Time {
        val newTotalSeconds = ((hours * 60) + minutes) * 60 + seconds - 1
        val hours = newTotalSeconds / 3600
        val minutes = (newTotalSeconds - hours * 3600) / 60
        val seconds = (newTotalSeconds - hours * 3600 - minutes * 60)
        return Time(hours, minutes, seconds)
    }
}

@Composable
fun ClockScreen() {
    var time by remember { mutableStateOf(Time(hours = 15, minutes = 28, seconds = 58)) }

    Clock(time)

    LaunchedEffect(0) {
        while (true) {
            delay(1000)
            time--
        }
    }
}

@Composable
fun Clock(time: Time) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .height(40.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary.copy(alpha = 0.5f))
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .offset(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NumberColumn(time.hours / 10, 0..12)
            NumberColumn(time.hours % 10, 0..9, Modifier.offset(x = -16.dp))

            Spacer(Modifier.size(4.dp))

            NumberColumn(time.minutes / 10, 0..5)
            NumberColumn(time.minutes % 10, 0..9, Modifier.offset(x = -16.dp))

            Spacer(Modifier.size(4.dp))

            NumberColumn(time.seconds / 10, 0..5)
            NumberColumn(time.seconds % 10, 0..9, Modifier.offset(x = -16.dp))
        }
    }
}

@Composable
fun NumberColumn(
    current: Int,
    range: IntRange,
    modifier: Modifier = Modifier,
) {
    val mid = (range.last - range.first) / 2f
    val reset = current == range.first

    val animationSpec: FiniteAnimationSpec<Dp> = if (reset) {
        spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        )
    } else {
        tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing,
        )
    }

    val yOffset by animateDpAsState(
        targetValue = 40.dp * (mid - current),
        animationSpec = animationSpec
    )
    val xOffset by animateDpAsState(
        targetValue = current * 12.dp,
        animationSpec = animationSpec
    )

    Column(modifier.offset(x = xOffset, y = yOffset)) {
        range.forEach { num ->
            Number(num, num == current)
        }
    }
}

@Composable
fun Number(value: Int, active: Boolean) {
    val backgroundColor by animateColorAsState(
        if (active) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant,
    )

    Box(
        modifier = Modifier
            .height(40.dp)
            .width(56.dp)
            .offset(value * -12.dp)
            .clip(DiamondShape(12.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            color = Color.White,
            style = TextStyle(fontStyle = FontStyle.Italic)
        )
    }
}

fun DiamondShape(size: Dp) = DiamondShape(CornerSize(size))

fun DiamondShape(corner: CornerSize) = DiamondShape(corner, corner, corner, corner)

data class DiamondShape(
    private val topStart: CornerSize,
    private val topEnd: CornerSize,
    private val bottomEnd: CornerSize,
    private val bottomStart: CornerSize
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val minDimension = size.minDimension
        val topStart = min(topStart.toPx(size, density), minDimension)
        val topEnd = min(topEnd.toPx(size, density), minDimension)
        val bottomEnd = min(bottomEnd.toPx(size, density), minDimension - topEnd)
        val bottomStart = min(bottomStart.toPx(size, density), minDimension - topStart)
        require(topStart >= 0.0f && topEnd >= 0.0f && bottomEnd >= 0.0f && bottomStart >= 0.0f)

        return if (topStart + topEnd + bottomStart + bottomEnd == 0.0f) {
            Outline.Rectangle(size.toRect())
        } else Outline.Generic(
            Path().apply {
                moveTo(topStart, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width - bottomEnd, size.height)
                lineTo(0f, size.height)
                close()
            }
        )
    }
}
