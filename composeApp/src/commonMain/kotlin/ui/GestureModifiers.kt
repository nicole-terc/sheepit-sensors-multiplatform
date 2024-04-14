package ui

import ScreenSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.unit.Constraints
import getScreenSize
import getSensorManager
import kotlinx.coroutines.launch
import sensorManager.DeviceOrientation
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import util.PiFloat
import util.mapValues


// Orientation Sensor Modifier
// Factory
fun Modifier.animateOrientationChange(): Modifier =
    this then AnimateOrientationChangeElement

// Modifier Node Element
private data object AnimateOrientationChangeElement :
    ModifierNodeElement<AnimateOrientationChangeNode>() {
    override fun create(): AnimateOrientationChangeNode = AnimateOrientationChangeNode()

    override fun update(node: AnimateOrientationChangeNode) {
        // No - op
    }
}

// Node
private class AnimateOrientationChangeNode : Modifier.Node(),
    CompositionLocalConsumerModifierNode, LayoutModifierNode {
    lateinit var sensorManager: MultiplatformSensorManager
    lateinit var screenSize: ScreenSize

    val rotation = Animatable(Offset(0f, 0f), Offset.VectorConverter)
    val translation = Animatable(Offset(0f, 0f), Offset.VectorConverter)

    override fun onAttach() {
        super.onAttach()
        sensorManager = getSensorManager { currentValueOf(it)!! }
        screenSize = getScreenSize { currentValueOf(it)!! }

        sensorManager.observeOrientationChangesWithCorrection { orientation ->

            val roll = orientation.roll
            val pitch = orientation.pitch

            coroutineScope.launch {
                // Rotation
                val degreesX = mapValues(
                    value = pitch,
                    fromStart = -PiFloat,
                    fromEnd = PiFloat,
                    toStart = -90f,
                    toEnd = 90f,
                )

                val degreesY = mapValues(
                    value = roll,
                    fromStart = -PiFloat,
                    fromEnd = PiFloat,
                    toStart = -90f,
                    toEnd = 90f,
                )

                rotation.animateTo(
                    Offset(
                        x = degreesX,
                        y = degreesY,
                    ),
                )
            }
            coroutineScope.launch {
                // Styled movement
                val offsetX = mapValues(
                    value = roll,
                    fromStart = -PiFloat,
                    fromEnd = PiFloat,
                    toStart = -screenSize.widthPx / 2f,
                    toEnd = screenSize.widthPx / 2f,
                )

                val offsetY = mapValues(
                    value = pitch,
                    fromStart = -PiFloat,
                    fromEnd = PiFloat,
                    toStart = -screenSize.heightPx / 2f,
                    toEnd = screenSize.heightPx / 2f,
                )

                translation.animateTo(
                    Offset(
                        x = offsetX,
                        y = -offsetY,
                    )
                )
            }
        }
    }

    override fun onDetach() {
        sensorManager.unregisterListener(MultiplatformSensorType.CUSTOM_ORIENTATION)
        super.onDetach()
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0, layerBlock = {
                translationX = translation.value.x
                translationY = translation.value.y
                rotationX = rotation.value.x
                rotationY = rotation.value.y
            })
        }
    }
}

// ----------------------

// Factory
fun Modifier.onOrientationChange(onOrientationChange: (DeviceOrientation) -> Unit): Modifier =
    this then OnOrientationChangeElement(onOrientationChange)

// Modifier Node Element
private data class OnOrientationChangeElement(val onOrientationChange: (DeviceOrientation) -> Unit) :
    ModifierNodeElement<OnOrientationChangeNode>() {
    override fun create(): OnOrientationChangeNode =
        OnOrientationChangeNode(onOrientationChange)

    override fun update(node: OnOrientationChangeNode) {
        node.onOrientationChange = onOrientationChange
    }
}

// Node
private class OnOrientationChangeNode(
    var onOrientationChange: (DeviceOrientation) -> Unit,
) : Modifier.Node(), CompositionLocalConsumerModifierNode {
    lateinit var sensorManager: MultiplatformSensorManager

    override fun onAttach() {
        super.onAttach()
        sensorManager = getSensorManager { currentValueOf(it)!! }
        sensorManager.observeOrientationChangesWithCorrection { orientation ->
            onOrientationChange(orientation)
        }
    }

    override fun onDetach() {
        super.onDetach()
        sensorManager.unregisterListener(MultiplatformSensorType.CUSTOM_ORIENTATION)
    }
}