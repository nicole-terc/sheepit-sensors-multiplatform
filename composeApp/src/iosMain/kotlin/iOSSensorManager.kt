import sensorManager.DeviceOrientation
import sensorManager.MultiplatformSensor
import sensorManager.MultiplatformSensorEvent
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import sensorManager.SamplingPeriod

class iOSSensorManager : MultiplatformSensorManager {
    override fun getSensorList(sensorType: MultiplatformSensorType): List<MultiplatformSensor> {
        TODO("Not yet implemented")
    }

    override fun getDefaultSensor(sensorType: MultiplatformSensorType): MultiplatformSensor? {
        TODO("Not yet implemented")
    }

    override fun registerListener(
        sensorType: MultiplatformSensorType,
        samplingPeriod: SamplingPeriod,
        onAccuracyChanged: (MultiplatformSensorType?, Int) -> Unit,
        onSensorChanged: (MultiplatformSensorEvent) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun unregisterListener(sensorType: MultiplatformSensorType) {
        TODO("Not yet implemented")
    }

    override fun unregisterAll() {
        TODO("Not yet implemented")
    }

    override fun observeOrientationChanges(onOrientationChanged: (DeviceOrientation) -> Unit) {
        TODO("Not yet implemented")
    }
}