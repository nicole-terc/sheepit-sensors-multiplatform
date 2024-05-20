import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.useContents
import kotlinx.datetime.Clock
import objcnames.classes.Protocol
import platform.CoreMotion.CMAttitudeReferenceFrameXMagneticNorthZVertical
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSError
import platform.Foundation.NSOperationQueue
import platform.SensorKit.SRAuthorizationStatus
import platform.SensorKit.SRFetchRequest
import platform.SensorKit.SRFetchResult
import platform.SensorKit.SRSensorAmbientLightSensor
import platform.SensorKit.SRSensorReader
import platform.SensorKit.SRSensorReaderDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.NSUInteger
import platform.posix.err
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
import kotlin.native.internal.collectReferenceFieldValues

@OptIn(ExperimentalForeignApi::class)
class iOSSensorManager : MultiplatformSensorManager {
    private val motionManager = CMMotionManager().apply {
        deviceMotionUpdateInterval = SamplingPeriod.GAME.toUpdateInterval()
    }

    private val reader = SRSensorReader(sensor = SRSensorAmbientLightSensor)

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

            MultiplatformSensorType.LIGHT -> startLightUpdates(onSensorChanged)
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

    // Requires a developer account and a provisioning profile to work
    // more info: https://developer.apple.com/documentation/sensorkit/configuring_your_project_for_sensor_reading
    private fun startLightUpdates(
        onSensorChanged: (MultiplatformSensorEvent) -> Unit,
    ) {
        SRSensorReader.requestAuthorizationForSensors(setOf(SRSensorAmbientLightSensor)) { error ->
            error?.let {
                println("Error requesting authorization for light sensor: $it")
                return@requestAuthorizationForSensors
            } ?: println("Dialog dismissed")
        }
        reader.delegate = ReaderDelegate(onSensorChanged)
    }

    private class ReaderDelegate(
        val onSensorChanged: (MultiplatformSensorEvent) -> Unit
    ) : SRSensorReaderDelegateProtocol, NSObject() {

        override fun sensorReader(
            reader: SRSensorReader,
            fetchingRequest: SRFetchRequest,
            didFetchResult: SRFetchResult
        ): Boolean {
            println("light: ${didFetchResult.sample}")
            onSensorChanged(
                MultiplatformSensorEvent(
                    values = floatArrayOf(didFetchResult.sample as? Float ?: 0.0f),
                    accuracy = 0,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                )
            )
            return true
        }

        override fun sensorReader(
            reader: SRSensorReader,
            didChangeAuthorizationStatus: SRAuthorizationStatus
        ) {
            if (didChangeAuthorizationStatus.toInt() == 1) {
                println("Light Sensor authorized")
                reader.startRecording()
            } else {
                println("Light Sensor not authorized")
            }
        }

        override fun description(): String? {
            return "Light Sensor"
        }

        override fun hash(): NSUInteger {
            return 0u
        }

        override fun superclass(): ObjCClass? {
            return null
        }

        override fun `class`(): ObjCClass? {
            return null
        }

        override fun conformsToProtocol(aProtocol: Protocol?): Boolean {
            return true
        }

        override fun isEqual(`object`: Any?): Boolean {
            return false
        }

        override fun isKindOfClass(aClass: ObjCClass?): Boolean {
            return true
        }

        override fun isMemberOfClass(aClass: ObjCClass?): Boolean {
            return false
        }

        override fun isProxy(): Boolean {
            return true
        }

        override fun performSelector(aSelector: COpaquePointer?): Any? {
            return null
        }

        override fun performSelector(
            aSelector: COpaquePointer?,
            withObject: Any?,
            _withObject: Any?
        ): Any? {
            return null
        }

        override fun performSelector(aSelector: COpaquePointer?, withObject: Any?): Any? {
            return null
        }

        override fun respondsToSelector(aSelector: COpaquePointer?): Boolean {
            return true
        }

    }
}

