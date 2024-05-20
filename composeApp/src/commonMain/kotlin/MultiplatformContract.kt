import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.ui.Modifier
import sensorManager.MultiplatformSensorManager
import util.ScreenSize

@Composable
expect fun rememberScreenSize(): ScreenSize

@Composable
expect fun rememberSensorManager(): MultiplatformSensorManager

expect fun getSensorManager(local: (CompositionLocal<*>) -> Any): MultiplatformSensorManager

expect fun getScreenSize(local: (CompositionLocal<*>) -> Any): ScreenSize

@Composable
expect fun PermissionsWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
)