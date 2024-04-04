import sensorManager.MultiplatformSensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.nstv.gesturesfun.sensorManager.AndroidSensorManager


@Composable
actual fun rememberSensorManager(): MultiplatformSensorManager {
    val context = LocalContext.current
    return remember(context) { AndroidSensorManager(context) }
}


@Composable
actual fun getScreenSize(): ScreenSize {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    return remember(density, configuration) {
        val width = configuration.screenWidthDp.dp
        val height = configuration.screenHeightDp.dp
        ScreenSize(
            width = width,
            height = height,
            widthPx = with(density) { width.roundToPx() },
            heightPx = with(density) { height.roundToPx() },
        )
    }
}