import sensorManager.MultiplatformSensor
import sensorManager.MultiplatformSensorManager
import sensorManager.MultiplatformSensorType
import sensorManager.MultiplatformSensorEventListener
import sensorManager.SamplingPeriod

class iOSSensorManager : MultiplatformSensorManager {
    override fun getSensorList(sensorType: MultiplatformSensorType): List<MultiplatformSensor> {
        TODO("Not yet implemented")
    }

    override fun getDefaultSensor(sensorType: MultiplatformSensorType): MultiplatformSensor? {
        TODO("Not yet implemented")
    }

    override fun registerListener(
        listener: MultiplatformSensorEventListener,
        sensorType: MultiplatformSensorType,
        samplingPeriod: SamplingPeriod
    ) {
        TODO("Not yet implemented")
    }

    override fun unregisterListener(sensorType: MultiplatformSensorType) {
        TODO("Not yet implemented")
    }

    override fun unregisterAll() {
        TODO("Not yet implemented")
    }


}