package ui

import ScreenSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.ComposableSheep
import dev.nstv.composablesheep.library.model.Sheep
import getScreenSize
import rememberSensorManager
import sensorManager.DeviceOrientation
import sensorManager.MultiplatformSensorManager
import util.DisposableEffectWithLifecycle

@Composable
fun ParallaxScreen(
    sheep: Sheep,
    modifier: Modifier = Modifier,
    screenSize: ScreenSize = getScreenSize(),
    sensorManager: MultiplatformSensorManager = rememberSensorManager(),
) {
    var orientation by remember { mutableStateOf(DeviceOrientation(0f, 0f, 0f)) }

    DisposableEffectWithLifecycle(
        onPause = { },
    ) {
        sensorManager.observeOrientationChanges {
            orientation = it
        }
    }

    Box(modifier.fillMaxSize()) {
        ComposableSheep(
            sheep = sheep,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = orientation.roll.dp.roundToPx(),
                        y = -orientation.pitch.dp.roundToPx()
                    )
                }
                .size(300.dp)
                .align(
                    BiasAlignment(
                        horizontalBias = (orientation.roll * 0.005f),
                        verticalBias = 0f
                    )
                )

        )
    }
}