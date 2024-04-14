import sensorManager.MultiplatformSensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


actual fun getSensorManager(local: (CompositionLocal<*>) -> Any): MultiplatformSensorManager {
    return iOSSensorManager()
}

actual fun getScreenSize(local: (CompositionLocal<*>) -> Any): ScreenSize {
    val density = local(LocalDensity) as Density
    val containerSize = local(LocalWindowInfo) as IntSize

    return ScreenSize(
        width = with(density) { containerSize.width.toDp() },
        height = with(density) { containerSize.height.toDp() },
        widthPx = containerSize.width,
        heightPx = containerSize.height,
    )
}

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