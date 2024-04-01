class iOSMultiplatformSensorManager : MultiplatformSensorManager {
    override fun getSensorList(): List<Sensor> {
        TODO("Not yet implemented")
    }

    override fun getSensor(sensorType: SensorType): Sensor {
        TODO("Not yet implemented")
    }

    override fun getDefaultSensor(sensorType: SensorType): Sensor {
        TODO("Not yet implemented")
    }

    override fun registerListener(
        listener: SensorEventListener,
        sensor: Sensor,
        samplingPeriodUs: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun unregisterListener(listener: SensorEventListener) {
        TODO("Not yet implemented")
    }
}

actual fun createSensorManager(): MultiplatformSensorManager {
    return iOSMultiplatformSensorManager()
}