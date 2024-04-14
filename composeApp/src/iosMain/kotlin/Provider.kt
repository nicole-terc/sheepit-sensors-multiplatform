import sensorManager.MultiplatformSensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

@Composable
actual fun rememberSensorManager(): MultiplatformSensorManager {
    return iOSSensorManager()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberScreenSize(): ScreenSize {
    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize

    return remember(density, containerSize) {
        val width = containerSize.width
        val height = containerSize.height
        ScreenSize(
            width = with(density) { width.toDp() },
            height = with(density) { height.toDp() },
            widthPx = width,
            heightPx = height,
        )
    }
}