enum class SensorType{
    ACCELEROMETER,
    MAGNETIC_FIELD,
    GYROSCOPE,
    LIGHT,
    PRESSURE,
    PROXIMITY,
    GRAVITY,
    LINEAR_ACCELERATION,
    ROTATION_VECTOR,
    RELATIVE_HUMIDITY,
    AMBIENT_TEMPERATURE,
    SIGNIFICANT_MOTION,
    STEP_DETECTOR,
    STEP_COUNTER,
    GEOMAGNETIC_ROTATION_VECTOR,
    HEART_RATE,
    POSE_6DOF,
    STATIONARY_DETECTOR,
    MOTION_DETECTOR,
    HEART_BEAT,
    ALL
}

interface Sensor {
    val type: SensorType
    val name: String
}

interface SensorEventListener {
    fun onSensorChanged(sensorEvent: SensorEvent)
    fun onAccuracyChanged(sensor: Sensor, accuracy: Int)
}

interface SensorEvent {
    val values: FloatArray
}

interface MultiplatformSensorManager {
    fun getSensorList(): List<Sensor>
    fun getSensor(sensorType: SensorType): Sensor
    fun getDefaultSensor(sensorType: SensorType): Sensor
    fun registerListener(listener: SensorEventListener, sensor: Sensor, samplingPeriodUs: Int)
    fun unregisterListener(listener: SensorEventListener)
}

expect fun createSensorManager(): MultiplatformSensorManager