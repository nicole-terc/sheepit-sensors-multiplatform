package dev.nstv.gesturesfun.sensorManager

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import sensorManager.DeviceOrientation
import sensorManager.MultiplatformSensor
import sensorManager.MultiplatformSensorEvent
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import sensorManager.MultiplatformSensorType.CUSTOM_ORIENTATION
import sensorManager.MultiplatformSensorType.GAME_ROTATION_VECTOR
import sensorManager.SamplingPeriod
import util.PiFloat

const val ShowSensorLog = false

class AndroidSensorManager(
    private val context: Context
) : MultiplatformSensorManager {

    private val sensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val listeners: MutableMap<MultiplatformSensorType, SensorEventListener> = mutableMapOf()

    // Orientation
    var accelerometerReading: FloatArray? = null
    var magnetometerReading: FloatArray? = null
    var lastRotationReading: FloatArray = FloatArray(9)
    var lastRotationSet = false

    override fun getSensorList(sensorType: MultiplatformSensorType): List<MultiplatformSensor> {
        return sensorManager.getSensorList(sensorType.toSensorType())
            .map(Sensor::toMultiplatformSensor)
    }

    override fun getDefaultSensor(sensorType: MultiplatformSensorType): MultiplatformSensor? {
        return sensorManager.getDefaultSensor(sensorType.toSensorType())?.toMultiplatformSensor()
    }


    override fun registerListener(
        sensorType: MultiplatformSensorType,
        samplingPeriod: SamplingPeriod,
        onAccuracyChanged: (MultiplatformSensorType?, Int) -> Unit,
        onSensorChanged: (MultiplatformSensorEvent) -> Unit
    ) {

        if (listeners.containsKey(sensorType)) {
            sensorManager.unregisterListener(listeners[sensorType])
        }

        sensorManager.getDefaultSensor(sensorType.toSensorType())?.let { sensor ->
            val sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    onSensorChanged(event.toMultiplatformSensorEvent())
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    onAccuracyChanged(sensor?.type?.toMultiplatformSensorType(), accuracy)
                }
            }

            listeners[sensorType] = sensorEventListener
            sensorManager.registerListener(
                sensorEventListener,
                sensor,
                samplingPeriod.toSamplingPeriodUs()
            )
        }
    }

    override fun unregisterListener(sensorType: MultiplatformSensorType) {
        listeners[sensorType]?.let { sensorManager.unregisterListener(it) }
    }

    override fun unregisterAll() {
        listeners.forEach { (_, listener) -> sensorManager.unregisterListener(listener) }
        listeners.clear()
        accelerometerReading = null
        magnetometerReading = null
        lastRotationReading = FloatArray(9)
        lastRotationSet = false
    }

    override fun observeOrientationChanges(
        onOrientationChanged: (DeviceOrientation) -> Unit
    ) {
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (ShowSensorLog) {
                    println("event received: ${event.sensor.type.toMultiplatformSensorType()}")
                }
                if (event.sensor.type == Sensor.TYPE_GRAVITY) {
                    accelerometerReading = event.values
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    magnetometerReading = event.values
                }

                if (accelerometerReading != null && magnetometerReading != null) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        accelerometerReading,
                        magnetometerReading
                    )

                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val orientation = DeviceOrientation(
                        orientationAngles[0],
                        orientationAngles[1],
                        orientationAngles[2],
                    )
                    if (ShowSensorLog) orientation.prettyPrint()
                    onOrientationChanged(orientation)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Do nothing
            }
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)?.let {
            sensorManager.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
            sensorManager.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        listeners[CUSTOM_ORIENTATION] = sensorEventListener
    }

    // Corrected orientation to avoid Gimbal Lock
    override fun observeOrientationChangesWithCorrection(onOrientationChanged: (DeviceOrientation) -> Unit) {
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (lastRotationSet) {
                    val currentRotationReading = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(currentRotationReading, event.values)
                    val orientationAngles = FloatArray(3) { index ->
                        when (index) {
                            0 -> (lastRotationReading[0] * currentRotationReading[1] + lastRotationReading[3] * currentRotationReading[4] + lastRotationReading[6] * currentRotationReading[7]) * PiFloat
                            1 -> -(lastRotationReading[2] * currentRotationReading[1] + lastRotationReading[5] * currentRotationReading[4] + lastRotationReading[8] * currentRotationReading[7]) * PiFloat
                            2 -> -(lastRotationReading[2] * currentRotationReading[0] + lastRotationReading[5] * currentRotationReading[3] + lastRotationReading[8] * currentRotationReading[6]) * PiFloat
                            else -> 0f
                        }
                    }
                    val orientation = DeviceOrientation(
                        orientationAngles[0],
                        orientationAngles[1],
                        orientationAngles[2],
                    )
                    if (ShowSensorLog) orientation.prettyPrint()
                    onOrientationChanged(orientation)
                } else {
                    lastRotationReading = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(lastRotationReading, event.values)
                    lastRotationSet = true
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Do nothing
            }
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            sensorManager.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        listeners[GAME_ROTATION_VECTOR] = sensorEventListener
    }
}