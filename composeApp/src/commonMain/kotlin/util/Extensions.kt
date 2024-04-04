package util

import androidx.annotation.FloatRange
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.PI
import kotlin.math.roundToInt

fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())
fun Double.toDegrees() = 180.0 * this / PI
fun Float.toDegrees() = (180.0 * this / PI).toFloat()


fun mapValues(
    value: Float,
    fromStart: Float,
    fromEnd: Float,
    toStart: Float,
    toEnd: Float,
) = (value - fromStart) / (fromEnd - fromStart) * (toEnd - toStart) + toStart


