package sensorManager.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import rememberSensorManager
import sensorManager.MultiplatformSensorEvent
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import util.DisposableEffectWithLifecycle

// General sensorManager
@Composable
fun Modifier.onSensorEvent(
    sensors: List<MultiplatformSensorType>,
    sensorManager: MultiplatformSensorManager = rememberSensorManager(),
    onSensorEvent: (MultiplatformSensorType, MultiplatformSensorEvent) -> Unit,
): Modifier {
    var enabled by remember { mutableStateOf(true) }
    DisposableEffectWithLifecycle(
        onPause = {
            sensorManager.unregisterAll()
            enabled = false
        }
    ) {
        enabled = true
    }

    return this then (if (enabled) {
        OnSensorEventElement(
            enabled = enabled,
            sensorManager = sensorManager,
            sensors = sensors,
            onSensorEvent = onSensorEvent,
        )
    } else {
        Modifier
    })
}

private data class OnSensorEventElement(
    val enabled: Boolean,
    val sensorManager: MultiplatformSensorManager,
    val sensors: List<MultiplatformSensorType>,
    val onSensorEvent: (MultiplatformSensorType, MultiplatformSensorEvent) -> Unit,
) :
    ModifierNodeElement<OnSensorEventNode>() {
    override fun create(): OnSensorEventNode =
        OnSensorEventNode(sensorManager, sensors, onSensorEvent)

    override fun update(node: OnSensorEventNode) {
        node.sensors = sensors
        node.onSensorEvent = onSensorEvent
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OnSensorEventElement) return false

        if (sensors != other.sensors) return false
        if (onSensorEvent != other.onSensorEvent) return false
        if (sensorManager != other.sensorManager) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sensorManager.hashCode()
        result = 31 * result + sensors.hashCode()
        result = 31 * result + onSensorEvent.hashCode()
        return result
    }
}

// Node
private class OnSensorEventNode(
    var sensorManager: MultiplatformSensorManager,
    var sensors: List<MultiplatformSensorType>,
    var onSensorEvent: (MultiplatformSensorType, MultiplatformSensorEvent) -> Unit,
) : Modifier.Node(), CompositionLocalConsumerModifierNode {

    override fun onAttach() {
        super.onAttach()
        sensors.forEach { sensorType ->
            when (sensorType) {
                MultiplatformSensorType.CUSTOM_ORIENTATION -> {
                    sensorManager.observeOrientationChanges { orientation ->
                        onSensorEvent(sensorType, orientation.asEvent())
                    }
                }

                MultiplatformSensorType.CUSTOM_ORIENTATION_CORRECTED -> {
                    sensorManager.observeOrientationChangesWithCorrection { orientation ->
                        onSensorEvent(sensorType, orientation.asEvent())
                    }
                }

                else -> {
                    sensorManager.registerListener(
                        sensorType = sensorType,
                        onSensorChanged = { event ->
                            onSensorEvent(sensorType, event)
                        },
                    )
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        sensorManager.unregisterAll()
    }
}