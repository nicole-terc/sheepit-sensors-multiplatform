import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import dev.nstv.composablesheep.library.model.Sheep

@Composable
fun SensorPlaygroundScreen(
    modifier: Modifier = Modifier,
) {

    Box(modifier = modifier.fillMaxSize()) {

    }
}

data class SheepUiState(
    val sheep: Sheep = Sheep(),
    val position: Offset,
    val rotation: Float,
    val scale: Float,
)