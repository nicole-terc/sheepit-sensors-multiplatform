package ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.SensorPlaygroundScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface {
            SensorPlaygroundScreen(modifier = Modifier.fillMaxWidth())
        }
    }
}