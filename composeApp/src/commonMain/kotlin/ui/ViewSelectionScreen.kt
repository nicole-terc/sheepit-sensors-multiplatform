package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nstv.composablesheep.library.ComposableSheep
import dev.nstv.composablesheep.library.model.Sheep

data class ScreenItem(
    val title: String,
    val sheep: Sheep,
    val navTarget: NavTarget,
)

@Composable
fun ViewSelectionScreen(
    sheeps: List<Sheep>,
    onItemClick: (NavTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    val screens = remember {
        listOf(
            ScreenItem(
                title = "Sensors",
                sheep = sheeps[0],
                navTarget = NavTarget.SensorsFun,
            ),
            ScreenItem(
                title = "Parallax",
                sheep = sheeps[1],
                navTarget = NavTarget.Parallax,
            ),
            ScreenItem(
                title = "Parallax Tower",
                sheep = sheeps[2],
                navTarget = NavTarget.SheepTower,
            ),
            ScreenItem(
                title = "Step Counter",
                sheep = sheeps[3],
                navTarget = NavTarget.StepCounter,
            ),
        )
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(screens) { screenItem ->
            ScreenItemCard(
                screenItem = screenItem,
                onClick = {
                    onItemClick(screenItem.navTarget)
                },
            )
        }
    }

}

@Composable
fun ScreenItemCard(
    screenItem: ScreenItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier.clickable { onClick() }) {
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            ComposableSheep(
                sheep = screenItem.sheep,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            )
            Text(
                text = screenItem.title,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}