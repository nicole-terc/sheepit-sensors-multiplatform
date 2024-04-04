package ui

import ScreenSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.ComposableSheep
import dev.nstv.composablesheep.library.model.Sheep
import getScreenSize
import kotlinx.coroutines.launch
import rememberSensorManager
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import util.LifecycleEvent
import util.LifecycleOwner
import util.observeLifecycle
import kotlin.math.abs
import kotlin.math.roundToInt

data class SheepUiState(
    val sheep: Sheep = Sheep(),
    val position: Offset,
    val rotation: Float,
    val scale: Float,
)

const val SensorMagnitude = 500f
const val AccelerationThreshold = 1f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedSensorsScreen(
    modifier: Modifier = Modifier,
    screenSize: ScreenSize = getScreenSize(),
    sensorManager: MultiplatformSensorManager = rememberSensorManager(),
) {
    var initSensorManager by remember { mutableStateOf(0) }

    observeLifecycle(
        onResume = { initSensorManager++ },
        onPause = { sensorManager.unregisterAll() }
    )

    val coroutineScope = rememberCoroutineScope()

    // Sheep Properties
    val sheepScale = remember { Animatable(1f) }
    val sheepTranslation = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
    sheepTranslation.updateBounds(
        lowerBound = Offset(-screenSize.widthPx / 2f, -screenSize.heightPx / 2f),
        upperBound = Offset(screenSize.widthPx / 2f, screenSize.heightPx / 2f),
    )


    // Gesture states
    val decay = rememberSplineBasedDecay<Offset>()

    var isDragging by remember { mutableStateOf(false) }
    val draggableState = rememberDraggable2DState { delta ->
        coroutineScope.launch {
            sheepTranslation.snapTo(sheepTranslation.value.plus(delta))
        }
    }

    DisposableEffect(initSensorManager) {
        sensorManager.registerListener(MultiplatformSensorType.GYROSCOPE) { sensorEvent ->

            val xValue = sensorEvent.values[0]
            val yValue = sensorEvent.values[1]
            val zValue = sensorEvent.values[2]

            if (isDragging || (abs(yValue) < AccelerationThreshold && abs(xValue) < AccelerationThreshold)) return@registerListener

            println("sensorEvent: $sensorEvent, values: ${sensorEvent.values.joinToString(",")}")


            val velocity = Offset(SensorMagnitude.times(yValue), SensorMagnitude.times(xValue))

            val decayOffset = decay.calculateTargetValue(
                typeConverter = Offset.VectorConverter,
                initialValue = sheepTranslation.value,
                initialVelocity = velocity,
            )

            coroutineScope.launch {
                sheepTranslation.animateTo(decayOffset, initialVelocity = velocity)
            }
        }

        onDispose {
            sensorManager.unregisterAll()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ComposableSheep(
            modifier = Modifier.size(300.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    translationX = sheepTranslation.value.x
                    translationY = sheepTranslation.value.y
                    scaleX = sheepScale.value
                    scaleY = sheepScale.value
                }
                .pointerInput(PointerEventType.Press) {
                    awaitEachGesture {
                        awaitFirstDown()
                        coroutineScope.launch {
                            sheepScale.animateTo(1.2f)
                        }
                    }
                }
                .draggable2D(
                    state = draggableState,
                    onDragStarted = {
                        isDragging = true
                    },
                    onDragStopped = {
                        sheepScale.animateTo(1f)
                        isDragging = false
                    }
                )
        )
    }
}

fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())

