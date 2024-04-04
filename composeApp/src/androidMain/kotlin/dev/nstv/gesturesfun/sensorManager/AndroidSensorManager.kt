package dev.nstv.gesturesfun.sensorManager

import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensor
import sensorManager.MultiplatformSensorEventListener
import sensorManager.MultiplatformSensorType
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import sensorManager.MultiplatformSensorType.*
import sensorManager.SamplingPeriod

class AndroidSensorManager(
    private val context: Context
) : MultiplatformSensorManager {

    private val sensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val listeners: MutableMap<MultiplatformSensorType, SensorEventListener> = mutableMapOf()

    // Orientation
    var accelerometerReading: FloatArray? = null
    var magnetometerReading: FloatArray? = null

    override fun getSensorList(sensorType: MultiplatformSensorType): List<MultiplatformSensor> {
        return sensorManager.getSensorList(sensorType.toSensorType())
            .map(Sensor::toMultiplatformSensor)
    }

    override fun getDefaultSensor(sensorType: MultiplatformSensorType): MultiplatformSensor? {
        return sensorManager.getDefaultSensor(sensorType.toSensorType())?.toMultiplatformSensor()
    }

    override fun registerListener(
        listener: MultiplatformSensorEventListener,
        sensorType: MultiplatformSensorType,
        samplingPeriod: SamplingPeriod
    ) {

        if (listeners.containsKey(sensorType)) {
            sensorManager.unregisterListener(listeners[sensorType])
        }

        sensorManager.getDefaultSensor(sensorType.toSensorType())?.let { sensor ->
            val sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    listener.onSensorChanged(event.toMultiplatformSensorEvent())
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    listener.onAccuracyChanged(sensor?.type?.toMultiplatformSensorType(), accuracy)
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
    }

    override fun observeOrientationChanges(
        onOrientationChanged: (azimuth: Float, pitch: Float, roll: Float) -> Unit
    ) {
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                println("event received: ${event.sensor.type.toMultiplatformSensorType()}")
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
//
//                    val azimuth = Math.toDegrees(orientationAngles[0].toDouble())
//                    val pitch = Math.toDegrees(orientationAngles[1].toDouble())
//                    val roll = Math.toDegrees(orientationAngles[2].toDouble())
//
//                    println("Azimuth: $azimuth, Pitch: $pitch, Roll: $roll")

                    println("Azimuth: ${orientationAngles[0]}, Pitch: ${orientationAngles[1]}, Roll: ${orientationAngles[2]}")

                    onOrientationChanged(
                        orientationAngles[0],
                        orientationAngles[1],
                        orientationAngles[2]
                    )
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
}