package ui.modifiers

import ScreenSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import rememberScreenSize
import rememberSensorManager
import sensorManager.DeviceOrientation
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType.CUSTOM_ORIENTATION
import sensorManager.MultiplatformSensorType.CUSTOM_ORIENTATION_CORRECTED
import util.HalfPi
import util.mapRotation
import util.mapTranslationHeight
import util.mapTranslationWidth


// ----------------------
// Tap modifier


// ----------------------
// Animated Orientation Change

@Composable
fun Modifier.animateOrientationChange(
    enabled: Boolean = true,
    adjusted: Boolean = false,
    onOrientationChanged: ((DeviceOrientation) -> Unit)? = null,
): Modifier {
    val coroutineScope = rememberCoroutineScope()
    val sensorManager: MultiplatformSensorManager = rememberSensorManager()
    val screenSize: ScreenSize = rememberScreenSize()

    val rotation = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
    val translation = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
    val sensors =
        remember(adjusted) { listOf(if (adjusted) CUSTOM_ORIENTATION_CORRECTED else CUSTOM_ORIENTATION) }

    val sensorModifier = if (enabled) {
        onSensorEvent(
            sensors = sensors,
            sensorManager = sensorManager,
        ) { _, event ->
            val roll = event.values[2]
            val pitch = event.values[1]

            // Rotation
            coroutineScope.launch {
                val degreesX = mapRotation(pitch, HalfPi)
                val degreesY = mapRotation(roll)
                rotation.animateTo(Offset(x = degreesX, y = degreesY))
            }

            // Styled movement
            coroutineScope.launch {
                val offsetX = mapTranslationWidth(roll, screenSize)
                val offsetY = mapTranslationHeight(pitch, screenSize, HalfPi)
                translation.animateTo(Offset(x = offsetX, y = -offsetY))
            }
            onOrientationChanged?.invoke(DeviceOrientation(event.values))
        }.graphicsLayer {
            translationX = translation.value.x
            translationY = translation.value.y
            rotationX = rotation.value.x
            rotationY = rotation.value.y
        }
    } else Modifier

    return this then sensorModifier
}


