import sensorManager.MultiplatformSensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset

data class ScreenSize(
    val width: Dp,
    val height: Dp,
    val widthPx: Int,
    val heightPx: Int
) {
    val middlePoint: DpOffset = DpOffset(width / 2, height / 2)
}

@Composable
expect fun rememberScreenSize(): ScreenSize

@Composable
expect fun rememberSensorManager(): MultiplatformSensorManager

expect fun getSensorManager(local: (CompositionLocal<*>) -> Any): MultiplatformSensorManager

expect fun getScreenSize(local: (CompositionLocal<*>) -> Any): ScreenSize

