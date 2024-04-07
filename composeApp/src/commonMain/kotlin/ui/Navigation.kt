package ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import dev.nstv.composablesheep.library.model.Sheep
import dev.nstv.composablesheep.library.util.SheepColor

class RootNode(
    nodeContext: NodeContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.Grid,
            savedStateMap = nodeContext.savedStateMap,
        ),
        visualisation = {
            BackStackFader(it)
        }
    ),
) : Node<NavTarget>(
    appyxComponent = backStack,
    nodeContext = nodeContext,
) {
    private val sheeps = SheepColor.list.shuffled().map { Sheep(fluffColor = it) }

    @Composable
    override fun Content(modifier: Modifier) {
        AppyxNavigationContainer(
            appyxComponent = backStack,
            modifier = Modifier.fillMaxSize()
        )
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> =
        when (navTarget) {
            NavTarget.Grid -> node(nodeContext) {
                ViewSelectionScreen(
                    sheeps = sheeps,
                    onItemClick = backStack::push,
                )
            }

            NavTarget.SensorsFun -> node(nodeContext) { AnimatedSensorsScreen(sheeps[0]) }
            NavTarget.Parallax -> node(nodeContext) { ParallaxScreen(sheeps[1]) }
        }
}


sealed class NavTarget : Parcelable {
    @Parcelize
    data object Grid : NavTarget()

    @Parcelize
    data object SensorsFun : NavTarget()

    @Parcelize
    data object Parallax : NavTarget()
}