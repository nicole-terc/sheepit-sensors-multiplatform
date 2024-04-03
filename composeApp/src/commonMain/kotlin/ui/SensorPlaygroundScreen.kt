package ui

import ScreenSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.ComposableSheep
import dev.nstv.composablesheep.library.model.Sheep
import getScreenSize
import rememberSensorManager
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import kotlin.math.roundToInt

data class SheepUiState(
    val sheep: Sheep = Sheep(),
    val position: Offset,
    val rotation: Float,
    val scale: Float,
)

val dpChange = 16.dp

@Composable
fun SensorPlaygroundScreen(
    modifier: Modifier = Modifier,
    screenSize: ScreenSize = getScreenSize(),
    sensorManager: MultiplatformSensorManager = rememberSensorManager(),
) {

    var positionOffset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }

    DisposableEffect(sensorManager) {
        sensorManager.registerListener(MultiplatformSensorType.GYROSCOPE) { sensorEvent ->
            println("sensorEvent: $sensorEvent")
            sensorEvent.values.forEach { println(it.toString()) }
            val rotationX = sensorEvent.values[0]
            val rotationY = sensorEvent.values[1]
            val rotationZ = sensorEvent.values[2]

            val newPositionOffset = DpOffset(
                x = (positionOffset.x + dpChange.times(rotationY))
                    .coerceIn(
                        -screenSize.middlePoint.x,
                        screenSize.middlePoint.x
                    ),
                y = (positionOffset.y + dpChange.times(rotationX))
                    .coerceIn(
                        -screenSize.middlePoint.y,
                        screenSize.middlePoint.y
                    )
            )
            positionOffset = newPositionOffset
        }

        onDispose {
            sensorManager.unregisterListener(MultiplatformSensorType.ACCELEROMETER)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ComposableSheep(
            modifier = Modifier.size(300.dp)
                .align(Alignment.Center)
                .offset(
                    x = positionOffset.x,
                    y = positionOffset.y,
                ),
        )
    }
}

fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())
