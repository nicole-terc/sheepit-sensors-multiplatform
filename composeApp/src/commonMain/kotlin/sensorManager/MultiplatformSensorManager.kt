package sensorManager

import kotlinx.collections.immutable.ImmutableList

enum class MultiplatformSensorType {
    ACCELEROMETER,
    AMBIENT_TEMPERATURE,
    GAME_ROTATION_VECTOR,
    GEOMAGNETIC_ROTATION_VECTOR,
    GRAVITY,
    GYROSCOPE,
    GYROSCOPE_UNCALIBRATED,
    HEART_BEAT,
    HEART_RATE,
    LIGHT,
    LINEAR_ACCELERATION,
    LOW_LATENCY_OFFBODY_DETECT,
    MAGNETIC_FIELD,
    MAGNETIC_FIELD_UNCALIBRATED,
    MOTION_DETECT,
    POSE_6DOF,
    PRESSURE,
    PROXIMITY,
    RELATIVE_HUMIDITY,
    ROTATION_VECTOR,
    SIGNIFICANT_MOTION,
    STATIONARY_DETECT,
    STEP_COUNTER,
    STEP_DETECTOR,
    UNKNOWN,
    TYPE_ALL,
}

enum class SamplingPeriod {
    NORMAL,
    UI,
    GAME,
    FASTEST,
}

data class MultiplatformSensor(
    val name: String,
    val type: MultiplatformSensorType,
)

data class MultiplatformSensorEvent(
    val values: FloatArray,
    val accuracy: Int,
    val timestamp: Long,
)

interface MultiplatformSensorEventListener {
    fun onSensorChanged(sensorEvent: MultiplatformSensorEvent)
    fun onAccuracyChanged(sensorType: MultiplatformSensorType?, accuracy: Int)
}

interface MultiplatformSensorManager {
    fun getSensorList(sensorType: MultiplatformSensorType = MultiplatformSensorType.TYPE_ALL): List<MultiplatformSensor>
    fun getDefaultSensor(sensorType: MultiplatformSensorType): MultiplatformSensor?
    fun registerListener(
        listener: MultiplatformSensorEventListener,
        sensorType: MultiplatformSensorType,
        samplingPeriod: SamplingPeriod = SamplingPeriod.NORMAL
    )
    fun registerListener(
        sensorType: MultiplatformSensorType,
        samplingPeriod: SamplingPeriod = SamplingPeriod.NORMAL,
        onSensorChanged: (MultiplatformSensorEvent) -> Unit,
    ) {
        registerListener(
            listener = object : MultiplatformSensorEventListener {
                override fun onSensorChanged(sensorEvent: MultiplatformSensorEvent) {
                    onSensorChanged(sensorEvent)
                }

                override fun onAccuracyChanged(
                    sensorType: MultiplatformSensorType?,
                    accuracy: Int
                ) {
                    /* no op */
                }
            },
            sensorType = sensorType,
            samplingPeriod = samplingPeriod,
        )
    }


    fun unregisterListener(sensorType: MultiplatformSensorType)
    fun unregisterAll()

}

