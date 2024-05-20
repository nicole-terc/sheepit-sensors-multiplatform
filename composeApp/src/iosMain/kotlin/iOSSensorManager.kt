import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.datetime.Clock
import platform.CoreMotion.CMAttitudeReferenceFrameXMagneticNorthZVertical
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import sensorManager.DeviceOrientation
import sensorManager.MultiplatformSensor
import sensorManager.MultiplatformSensorEvent
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import sensorManager.SamplingPeriod
import util.HalfPi
import util.Pi
import util.TwoPi
import util.mapValues

@OptIn(ExperimentalForeignApi::class)
class iOSSensorManager : MultiplatformSensorManager {
    private val motionManager = CMMotionManager().apply {
        deviceMotionUpdateInterval = SamplingPeriod.GAME.toUpdateInterval()
    }

    override fun registerListener(
        sensorType: MultiplatformSensorType,
        samplingPeriod: SamplingPeriod,
        onAccuracyChanged: (MultiplatformSensorType?, Int) -> Unit,
        onSensorChanged: (MultiplatformSensorEvent) -> Unit
    ) {
        when (sensorType) {
            MultiplatformSensorType.LINEAR_ACCELERATION,
            MultiplatformSensorType.ACCELEROMETER -> startAccelerometerUpdates(onSensorChanged)
            MultiplatformSensorType.GAME_ROTATION_VECTOR,
            MultiplatformSensorType.ROTATION_VECTOR,
            MultiplatformSensorType.GYROSCOPE -> startGyroscopeUpdates(onSensorChanged)
            MultiplatformSensorType.CUSTOM_ORIENTATION -> observeOrientationChanges {
                onSensorChanged(
                    MultiplatformSensorEvent(
                        values = floatArrayOf(it.pitch, it.roll, it.azimuth),
                        accuracy = 0,
                        timestamp = Clock.System.now().toEpochMilliseconds(),
                    )
                )
            }
            MultiplatformSensorType.CUSTOM_ORIENTATION_CORRECTED -> observeOrientationChangesWithCorrection {
                onSensorChanged(
                    MultiplatformSensorEvent(
                        values = floatArrayOf(it.pitch, it.roll, it.azimuth),
                        accuracy = 0,
                        timestamp = Clock.System.now().toEpochMilliseconds(),
                    )
                )
            }
            else -> {
                // Do nothing
            }
        }

    }

    override fun unregisterListener(sensorType: MultiplatformSensorType) {
        unregisterAll()
    }

    override fun unregisterAll() {
        motionManager.stopDeviceMotionUpdates()
        motionManager.stopGyroUpdates()
        motionManager.stopMagnetometerUpdates()
        motionManager.stopAccelerometerUpdates()
    }

    override fun observeOrientationChanges(onOrientationChanged: (DeviceOrientation) -> Unit) {
        if (motionManager.isDeviceMotionAvailable()) {
            motionManager.startDeviceMotionUpdatesUsingReferenceFrame(
                CMAttitudeReferenceFrameXMagneticNorthZVertical,
                NSOperationQueue.mainQueue,
            ) { motion, error ->
                if (error != null) {
                    return@startDeviceMotionUpdatesUsingReferenceFrame
                }
                motion?.let {
                    val orientation = DeviceOrientation(
                        pitch = -it.attitude.pitch.toFloat()
                            .coerceIn(-HalfPi, HalfPi), // Convert to -90 to 90
                        roll = it.attitude.roll.toFloat(), // -180 to 180
                        azimuth = mapValues(
                            it.attitude.yaw.toFloat(),
                            fromStart = -Pi,
                            fromEnd = Pi,
                            toStart = 0f,
                            toEnd = TwoPi
                        ), // Convert to 0 to 360
                    )
                    println("Sensor Data Orientation: ${orientation.azimuth} ${orientation.pitch} ${orientation.roll}")
                    onOrientationChanged(orientation)
                }
            }
        }
    }

    override fun observeOrientationChangesWithCorrection(onOrientationChanged: (DeviceOrientation) -> Unit) {
        observeOrientationChanges(onOrientationChanged)
    }


    private fun startAccelerometerUpdates(
        onSensorChanged: (MultiplatformSensorEvent) -> Unit,
    ) {
        if (motionManager.isAccelerometerAvailable()) {
            motionManager.startAccelerometerUpdatesToQueue(
                NSOperationQueue.mainQueue,
            ) { data, error ->
                if (error != null) {
                    return@startAccelerometerUpdatesToQueue
                }
                data?.let {
                    it.acceleration.useContents {
                        println("Sensor Data Accelerations: ${this.x} ${this.y} ${this.z}")
                        onSensorChanged(
                            MultiplatformSensorEvent(
                                values = floatArrayOf(
                                    this.x.toFloat(),
                                    this.y.toFloat(),
                                    this.z.toFloat(),
                                ),
                                accuracy = 0,
                                timestamp = it.timestamp.toLong(),
                            )
                        )
                    }
                }
            }
        }
    }

    private fun startGyroscopeUpdates(
        onSensorChanged: (MultiplatformSensorEvent) -> Unit,
    ) {
        if (motionManager.isGyroAvailable()) {
            motionManager.startGyroUpdatesToQueue(
                NSOperationQueue.mainQueue,
            ) { data, error ->
                if (error != null) {
                    return@startGyroUpdatesToQueue
                }
                data?.let {
                    it.rotationRate.useContents {
                        println("Sensor Data Rotation: ${this.x} ${this.y} ${this.z}")
                        onSensorChanged(
                            MultiplatformSensorEvent(
                                values = floatArrayOf(
                                    this.x.toFloat(),
                                    this.y.toFloat(),
                                    this.z.toFloat(),
                                ),
                                accuracy = 0,
                                timestamp = it.timestamp.toLong(),
                            )
                        )
                    }
                }
            }
        }
    }
}