package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.ComposableSheep
import dev.nstv.composablesheep.library.model.Sheep
import rememberScreenSize
import rememberSensorManager
import sensorManager.DeviceOrientation
import sensorManager.MultiplatformSensorManager
import util.DisposableEffectWithLifecycle
import util.ScreenSize

// Based on: https://proandroiddev.com/parallax-effect-with-sensormanager-using-jetpack-compose-a735a2f5811b
@Composable
fun ParallaxScreen(
    sheep: Sheep,
    modifier: Modifier = Modifier,
    screenSize: ScreenSize = rememberScreenSize(),
    sensorManager: MultiplatformSensorManager = rememberSensorManager(),
) {
    var orientation by remember { mutableStateOf(DeviceOrientation(0f, 0f, 0f)) }

    DisposableEffectWithLifecycle(
        onPause = { },
    ) {
        sensorManager.observeOrientationChangesWithCorrection {
            orientation = it
        }
    }

    Box(modifier.fillMaxSize()) {
        //Shadow
        ComposableSheep(
            sheep = sheep,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = -orientation.roll.times(0.5).dp.roundToPx(),
                        y = orientation.pitch.times(1.5).dp.roundToPx()
                    )
                }
                .size(256.dp)
                .align(Alignment.Center)
                .blur(radius = 24.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        )


        // Edge
        val edgeColor = Color.Gray.copy(alpha = 0.5f)
        ComposableSheep(
            sheep = sheep.copy(
                fluffColor = edgeColor,
                headColor = edgeColor,
                legColor = edgeColor,
                glassesColor = edgeColor
            ),
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = orientation.roll.times(18).dp.roundToPx(),
                        y = -orientation.pitch.times(18).dp.roundToPx()
                    )
                }
                .size(300.dp)
                .align(Alignment.Center)
        )

        // Regular
        Box(Modifier.wrapContentSize().align(Alignment.Center)) {
            ComposableSheep(
                sheep = sheep,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = orientation.roll.times(20).dp.roundToPx(),
                            y = -orientation.pitch.times(20).dp.roundToPx()
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
}