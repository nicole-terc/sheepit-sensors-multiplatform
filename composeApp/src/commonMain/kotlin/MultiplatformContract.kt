import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import sensorManager.MultiplatformSensorManager
import util.ScreenSize

@Composable
expect fun rememberScreenSize(): ScreenSize

@Composable
expect fun rememberSensorManager(): MultiplatformSensorManager

expect fun getSensorManager(local: (CompositionLocal<*>) -> Any): MultiplatformSensorManager

expect fun getScreenSize(local: (CompositionLocal<*>) -> Any): ScreenSize

