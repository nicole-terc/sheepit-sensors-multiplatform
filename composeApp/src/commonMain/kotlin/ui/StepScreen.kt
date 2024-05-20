package ui

import PermissionsWrapper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.ComposableSheep
import dev.nstv.composablesheep.library.model.Sheep
import dev.nstv.composablesheep.library.util.SheepColor
import kotlinx.coroutines.launch
import rememberSensorManager
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import util.DisposableEffectWithLifecycle
import util.sideToSideKeyframes

@Composable
fun StepScreen(
    modifier: Modifier = Modifier,
    sheep: Sheep = Sheep(fluffColor = SheepColor.Orange),
    sensorManager: MultiplatformSensorManager = rememberSensorManager()
) = PermissionsWrapper {
    val coroutineScope = rememberCoroutineScope()
    var steps by remember { mutableIntStateOf(0) }
    val rotationZ = remember { Animatable(0f) }

    DisposableEffectWithLifecycle(
        key = sensorManager,
        onPause = { sensorManager.unregisterAll() }
    ) {

        sensorManager.registerListener(
            sensorType = MultiplatformSensorType.STEP_COUNTER,
        ) { event ->
            steps = event.values[0].toInt()
            println("StepScreen New steps: $steps")

        }

        sensorManager.registerListener(
            sensorType = MultiplatformSensorType.STEP_DETECTOR,
        ) { _ ->
            coroutineScope.launch {
                rotationZ.animateTo(
                    targetValue = 0f,
                    animationSpec = sideToSideKeyframes(0f, 15f, duration = 300)
                )
            }
        }
    }
    Box(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AnimatedContent(
            targetState = steps,
            label = "Step counter",
        ) { newSteps ->
            Text(
                text = "Steps: $newSteps",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxSize(),
            )
        }

        ComposableSheep(
            sheep = sheep,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    this.rotationZ = rotationZ.value
                },
        )
    }
}