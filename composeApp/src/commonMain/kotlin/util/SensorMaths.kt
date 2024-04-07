package util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import dev.nstv.composablesheep.library.util.toRadians
import kotlin.math.PI
import kotlin.math.roundToInt

// Constants
const val SensorMagnitude = 500f
const val AccelerationThreshold = 1f
const val DegreesThreshold = 3f
const val RotationDegreesThreshold = 10f

// PI
const val PiFloat = PI.toFloat()
val PiPlusThreshold = PI.toFloat() + RotationDegreesThreshold.toRadians()
val PiMinusThreshold = PI.toFloat() - RotationDegreesThreshold.toRadians()

// Extensions
fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())
fun Double.toDegrees() = 180.0 * this / PI
fun Float.toDegrees() = (180.0 * this / PI).toFloat()

// Functions
fun mapValues(
    value: Float,
    fromStart: Float,
    fromEnd: Float,
    toStart: Float,
    toEnd: Float,
) = (value - fromStart) / (fromEnd - fromStart) * (toEnd - toStart) + toStart


