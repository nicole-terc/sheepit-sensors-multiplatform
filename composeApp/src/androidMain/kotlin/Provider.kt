import android.content.Context
import android.content.res.Configuration
import sensorManager.MultiplatformSensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.nstv.gesturesfun.sensorManager.AndroidSensorManager
import util.ScreenSize


@Composable
actual fun rememberSensorManager(): MultiplatformSensorManager {
    val context = LocalContext.current
    return remember(context) { AndroidSensorManager(context) }
}


@Composable
actual fun rememberScreenSize(): ScreenSize {
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

actual fun getSensorManager(local: (CompositionLocal<*>) -> Any): MultiplatformSensorManager {
    val context = local(LocalContext) as Context
    return AndroidSensorManager(context)
}

actual fun getScreenSize(local: (CompositionLocal<*>) -> Any): ScreenSize {
    val density = local(LocalDensity) as Density
    val configuration = local(LocalConfiguration) as Configuration

    val width = configuration.screenWidthDp.dp
    val height = configuration.screenHeightDp.dp
    return ScreenSize(
        width = width,
        height = height,
        widthPx = with(density) { width.roundToPx() },
        heightPx = with(density) { height.roundToPx() },
    )
}