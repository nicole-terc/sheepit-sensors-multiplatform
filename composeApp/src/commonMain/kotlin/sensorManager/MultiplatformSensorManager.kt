package sensorManager

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
    CUSTOM_ORIENTATION,
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

interface MultiplatformSensorManager {
    fun getSensorList(sensorType: MultiplatformSensorType = MultiplatformSensorType.TYPE_ALL): List<MultiplatformSensor>
    fun getDefaultSensor(sensorType: MultiplatformSensorType): MultiplatformSensor?
    fun registerListener(
        sensorType: MultiplatformSensorType,
        samplingPeriod: SamplingPeriod = SamplingPeriod.NORMAL,
        onAccuracyChanged: (MultiplatformSensorType?, Int) -> Unit = { _, _ -> },
        onSensorChanged: (MultiplatformSensorEvent) -> Unit,
    )

    fun unregisterListener(sensorType: MultiplatformSensorType)
    fun unregisterAll()

    // Specific Readings
    fun observeOrientationChanges(
        onOrientationChanged: (DeviceOrientation) -> Unit
    )
}

