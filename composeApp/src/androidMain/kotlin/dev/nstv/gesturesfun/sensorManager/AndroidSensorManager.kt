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
import sensorManager.SamplingPeriod

class AndroidSensorManager(
    private val context: Context
) : MultiplatformSensorManager {

    private val sensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    private val listeners: MutableMap<MultiplatformSensorType, SensorEventListener> = mutableMapOf()

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

}