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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.ComposableSheep
import dev.nstv.composablesheep.library.model.Sheep
import dev.nstv.composablesheep.library.util.SheepColor
import rememberScreenSize
import rememberSensorManager
import sensorManager.DeviceOrientation
import sensorManager.MultiplatformSensorManager
import util.DisposableEffectWithLifecycle
import util.ScreenSize
import util.ShadowSheep

// Inspired on: https://trailingclosure.com/device-motion-effect/
@Composable
fun ParallaxTowerScreen(
    sheep: Sheep,
    modifier: Modifier = Modifier,
    screenSize: ScreenSize = rememberScreenSize(),
    sensorManager: MultiplatformSensorManager = rememberSensorManager(),
) {
    val sheepSizes = listOf(400.dp, 300.dp, 200.dp, 100.dp)
    val sheepColors =
        listOf(SheepColor.Purple, SheepColor.Green, SheepColor.Blue, SheepColor.Orange)
    var orientation by remember { mutableStateOf(DeviceOrientation(0f, 0f, 0f)) }

    DisposableEffectWithLifecycle(
        onPause = { },
    ) {
        sensorManager.observeOrientationChangesWithCorrection {
            orientation = it
        }
    }
    Box(modifier.fillMaxSize()) {
        sheepSizes.forEachIndexed { index, size ->
            // border
            ComposableSheep(
                sheep = ShadowSheep,
                modifier = Modifier
                    .size(size)
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = orientation.roll * (index+1) * 18f
                        translationY = -orientation.pitch * (index+1) * 28f
                    },

                )
            // sheep
            ComposableSheep(
                fluffColor = sheepColors[index],
                modifier = Modifier
                    .size(size)
                    .align(Alignment.Center)
                    .graphicsLayer {
                        translationX = orientation.roll * (index+1) * 20f
                        translationY = -orientation.pitch * (index+1) * 30f
                    },

                )
        }
    }
}