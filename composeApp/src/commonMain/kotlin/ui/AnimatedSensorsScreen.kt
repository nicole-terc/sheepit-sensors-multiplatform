package ui

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.ComposableSheep
import dev.nstv.composablesheep.library.model.Sheep
import dev.nstv.composablesheep.library.util.SheepColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import rememberScreenSize
import rememberSensorManager
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import sensorManager.MultiplatformSensorType.*
import util.DefaultDelay
import util.DisposableEffectWithLifecycle
import util.GravityEarth
import util.HalfPi
import util.ScreenSize
import util.backAndForthKeyframes
import util.mapRotation
import util.mapTranslationHeight
import util.mapTranslationWidth
import util.sideToSideKeyframes
import kotlin.math.sqrt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedSensorsScreen(
    modifier: Modifier = Modifier,
    sheep: Sheep = Sheep(fluffColor = SheepColor.Green),
//    sensorManager: MultiplatformSensorManager = rememberSensorManager(),
    screenSize: ScreenSize = rememberScreenSize(),
) {

    val sensorManager: MultiplatformSensorManager = rememberSensorManager()
    val coroutineScope = rememberCoroutineScope()

    // Properties to animate
    // Step 1.1.1 scale
    val scale = remember { Animatable(1f) }

    // Step 2.1 rotationZ
    val rotationZ = remember { Animatable(0f) }

    // Step 6.1 rotationX and rotationY
    val rotationX = remember { Animatable(0f) }
    val rotationY = remember { Animatable(0f) }

    // Step 4.1 translation offset
    val translation = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
    translation.updateBounds(
        lowerBound = Offset(-screenSize.halfSize.x, -screenSize.halfSize.y),
        upperBound = Offset(screenSize.halfSize.x, screenSize.halfSize.y),
    )

    // Dragging State & Decay
    // Step 4.2 Dragging state - Snap!
    val draggableState = rememberDraggable2DState { delta ->
        coroutineScope.launch {
            translation.snapTo(translation.value.plus(delta))
        }
    }

    // Step 5.2.3 Decay
    val decay = rememberSplineBasedDecay<Offset>()


    // Sensors
    // Step 8.1 Bonus
    val fluffColor = remember { Animatable(SheepColor.Green) }
    val glassesColor = remember { Animatable(SheepColor.Black) }

    fun doShakeMove() {
        val duration = 800
        // Translation
        coroutineScope.launch {
            translation.animateTo(
                targetValue = Offset.Zero,
                animationSpec = keyframes {
                    durationMillis = duration
                    Offset.Zero atFraction 0f
                    Offset(x = screenSize.widthPx * 0.33f, y = 0f) atFraction 0.25f
                    Offset.Zero atFraction 0.5f
                    Offset(x = screenSize.widthPx * -0.33f, y = 0f) atFraction 0.75f
                    Offset.Zero atFraction 1f
                }
            )
            translation.animateTo(
                targetValue = Offset.Zero,
                animationSpec = keyframes {
                    durationMillis = 200
                    Offset.Zero atFraction 0f
                    Offset(x = screenSize.widthPx * 0.1f, y = 0f) atFraction 0.25f
                    Offset.Zero atFraction 0.5f
                    Offset(x = screenSize.widthPx * -0.1f, y = 0f) atFraction 0.75f
                    Offset.Zero atFraction 1f
                }
            )
        }

        //Scale
        coroutineScope.launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = backAndForthKeyframes(1f, 1.1f, duration = duration)
            )
        }

        // Rotation
        val maxAngle = 225f
        coroutineScope.launch {
            rotationZ.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = duration
                    0f atFraction 0f
                    (maxAngle) atFraction 0.25f
                    0f atFraction 0.5f
                    (-maxAngle) atFraction 0.75f
                    0f atFraction 1f
                }
            )
            rotationZ.animateTo(
                targetValue = 0f,
                animationSpec = sideToSideKeyframes(0f, 25f, duration = 200)
            )
        }
    }

    fun doSideToSideMove() {
        // Translation
        coroutineScope.launch {
            translation.animateTo(
                targetValue = Offset.Zero,
                animationSpec = keyframes {
                    durationMillis = 500
                    Offset.Zero atFraction 0f
                    Offset(x = screenSize.widthPx * 0.33f, y = 0f) atFraction 0.25f
                    Offset.Zero atFraction 0.5f
                    Offset(x = screenSize.widthPx * -0.33f, y = 0f) atFraction 0.75f
                    Offset.Zero atFraction 1f
                }
            )
        }
        //Scale
        coroutineScope.launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = backAndForthKeyframes(1f, 0.5f)
            )
        }

        // Rotation
        coroutineScope.launch {
            rotationY.animateTo(
                targetValue = 0f,
                animationSpec = backAndForthKeyframes(0f, 90f)
            )
        }
    }

    // 5.2.2 Fling
    fun doFlingMove(velocity: Velocity) {
        //1. Calculate target offset based on velocity
        val velocityOffset = Offset(velocity.x / 2f, velocity.y / 2f)

        val targetOffset = decay.calculateTargetValue(
            typeConverter = Offset.VectorConverter,
            initialValue = translation.value,
            initialVelocity = velocityOffset,
        )

        // 2. If the target offset is within bounds, animate to it
        if (targetOffset.x < screenSize.halfSize.x && targetOffset.x > -screenSize.halfSize.x &&
            targetOffset.y < screenSize.halfSize.y && targetOffset.y > -screenSize.halfSize.y
        ) {
            coroutineScope.launch {
                translation.animateDecay(velocityOffset, decay)
            }
        }
        // 3. If not, animate to farthest point within bounds and then animate back to center
        else {
            coroutineScope.launch {
                val adjustedOffset = Offset(
                    x = if (targetOffset.x < -screenSize.halfSize.x) -screenSize.halfSize.x else if (targetOffset.x > screenSize.halfSize.x) screenSize.halfSize.x else targetOffset.x,
                    y = if (targetOffset.y < -screenSize.halfSize.y) -screenSize.halfSize.y else if (targetOffset.y > screenSize.halfSize.y) screenSize.halfSize.y else targetOffset.y
                )
                translation.animateTo(adjustedOffset)

                translation.animateTo(
                    Offset(0f, 0f),
                    spring(
                        stiffness = Spring.StiffnessMedium,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                )
            }
        }
    }


    DisposableEffectWithLifecycle(
        key = sensorManager,
        onPause = { sensorManager.unregisterAll() }
    ) {

        // Step 6.2 Rotation
//        sensorManager.registerListener(
//            sensorType = ROTATION_VECTOR,
//            onSensorChanged = { event ->
//                println(
//                    "Rotation values: " +
//                            "x: ${event.values[0]} " +
//                            "y: ${event.values[1]} " +
//                            "z: ${event.values[2]} "
//                )
//                coroutineScope.launch {
//                    rotationX.snapTo(event.values[0] * 180)
//                }
//                coroutineScope.launch {
//                    rotationY.snapTo(event.values[1] * 180)
//                }
//                coroutineScope.launch {
//                    rotationZ.snapTo(-event.values[2] * 180)
//                }
//            }
//        )

        // Step 6.2 Rotation with orientation adjusted
        sensorManager.observeOrientationChangesWithCorrection { orientation ->
            orientation.prettyPrint(degrees = true)

            coroutineScope.launch {
                rotationX.snapTo(orientation.pitchDegrees)
            }
            coroutineScope.launch {
                rotationY.snapTo(orientation.rollDegrees)
            }
        }

//        // Step 6.2 v2 Rotation Shift
//        sensorManager.observeOrientationChangesWithCorrection { orientation ->
//            // Smooth animation
//            val pitch = orientation.pitch
//            val roll = orientation.roll
//
//            // Smooth Rotation
//            coroutineScope.launch {
//                rotationX.animateTo(mapRotation(pitch, HalfPi))
//            }
//            coroutineScope.launch {
//                rotationY.animateTo(mapRotation(roll))
//
//            }
//
//            // Smooth Translation
//            coroutineScope.launch {
//                val offsetX = mapTranslationWidth(roll, screenSize)
//                val offsetY = mapTranslationHeight(pitch, screenSize, HalfPi)
//
//                translation.animateTo(Offset(x = offsetX, y = -offsetY))
//            }
//        }


        // Step 7.2 Shake it!
        val shakeStopDelay = 100
        var lastAcceleration = GravityEarth
        var acceleration = 0f
        var isShaking = false
        var lastShakeTime: Long = 0

        sensorManager.registerListener(
            sensorType = LINEAR_ACCELERATION,
            onSensorChanged = { event ->
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // source: https://stackoverflow.com/questions/2317428/how-to-refresh-app-upon-shaking-the-device
                val currentAcceleration = sqrt(x * x + y * y + z * z)
                val delta: Float = currentAcceleration - lastAcceleration
                acceleration = acceleration * 0.9f + delta

                if (acceleration > 8f) {
                    lastShakeTime = Clock.System.now().toEpochMilliseconds()
                    isShaking = true
                    // Shake started
                } else if (isShaking && Clock.System.now()
                        .toEpochMilliseconds() - lastShakeTime > shakeStopDelay
                ) {
                    isShaking = false
                    // Shake ended
                    doShakeMove()
                }
                lastAcceleration = currentAcceleration
            })

        // Step 8.2 Bonus
        sensorManager.registerListener(
            sensorType = LIGHT,
            onSensorChanged = { event ->
                val lux = event.values[0]
                println("Light values: $lux")
                if (lux < 10f) {
                    // Dark
                    coroutineScope.launch {
                        fluffColor.animateTo(SheepColor.Black)
                    }
                    coroutineScope.launch {
                        glassesColor.animateTo(Color.White.copy(alpha = 0.5f))
                    }
                } else {
                    // Light
                    coroutineScope.launch {
                        fluffColor.animateTo(SheepColor.Green)
                    }
                    coroutineScope.launch {
                        glassesColor.animateTo(SheepColor.Black)
                    }
                }

            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        ComposableSheep(
            sheep = sheep,
            fluffColor = fluffColor.value,
            glassesColor = glassesColor.value,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                // Step 1.1.2 set scale in graphicsLayer(!)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    this.rotationX = rotationX.value
                    this.rotationY = rotationY.value
                    this.rotationZ = rotationZ.value
                    translationX = translation.value.x
                    translationY = translation.value.y
                }
                // Step 1.2 clickable scale change
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null,
//                    onClick = {
//                        coroutineScope.launch {
//                            val newScale = if (scale.value == 1f) 1.2f else 1f
//                            scale.animateTo(
//                                targetValue = newScale,
//                                animationSpec = spring(
//                                    dampingRatio = Spring.DampingRatioHighBouncy,
//                                    stiffness = Spring.StiffnessHigh,
//                                )
//                            )
//                        }
//                    })
                // Step 2.2 Double tap pointerInput || Step 3.2 Long press pointerInput
                .pointerInput(Unit) {
                    var isSpinning: Boolean = false
                    detectTapGestures(
                        onTap = {
                            coroutineScope.launch {
                                val newScale = if (scale.value == 1f) 1.2f else 1f
                                val offshootScale = if (scale.value == 1f) 1.5f else 0.7f
                                val initialScale = if (scale.value == 1f) 0.2f else 2f

                                scale.animateTo(
                                    targetValue = newScale,
                                    animationSpec = keyframes {
                                        durationMillis = 500
                                        initialScale atFraction 0.1f using LinearOutSlowInEasing
                                        offshootScale atFraction 0.75f using FastOutLinearInEasing
                                        newScale atFraction 1f using LinearEasing
                                    }
//                                    animationSpec = spring(
//                                        dampingRatio = Spring.DampingRatioHighBouncy,
//                                        stiffness = Spring.StiffnessMediumLow,
//                                    )
//                                    animationSpec = spring(
//                                        dampingRatio = Spring.DampingRatioHighBouncy,
//                                        stiffness = Spring.StiffnessHigh,
//                                    )
                                )
                            }
                        },
                        onDoubleTap = {
//                            doShakeMove()
                            coroutineScope.launch {
                                listOf(-45f, 0f, 45f, 0f).forEach {
                                    rotationZ.animateTo(
                                        targetValue = it,
                                        animationSpec = spring(
                                            stiffness = Spring.StiffnessMedium,
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                        )
                                    )
                                    delay(DefaultDelay)
                                }
                            }
                        },
                        onPress = {
                            awaitRelease()
                            if (isSpinning) {
                                isSpinning = false
                                rotationZ.animateTo(0f)
                            }
                        },
                        onLongPress = {
                            isSpinning = true
                            coroutineScope.launch {
                                rotationZ.animateTo(
                                    targetValue = 360f,
                                    animationSpec = infiniteRepeatable(
                                        keyframes {
                                            0f atFraction 0f
                                            360f atFraction 1f
                                        }
                                    )
                                )
                            }
                        }
                    )
                }
                // Step 4.2 Draggable2D || 5.2.1 Fling onDragStopped
                .draggable2D(
                    state = draggableState,
                    onDragStopped = { velocity ->
                        doFlingMove(velocity)
                    }
                )
        )
    }
}