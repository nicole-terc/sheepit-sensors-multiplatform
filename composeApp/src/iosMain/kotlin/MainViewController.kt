import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.uikit.ComposeUIViewControllerDelegate
import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.ui.zIndex
import com.bumble.appyx.navigation.integration.IntegrationPoint
import com.bumble.appyx.navigation.integration.IosNodeHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import platform.UIKit.UIViewController
import platform.UIKit.navigationController
import ui.RootNode
import util.LifecycleEvent
import util.LifecycleOwner

private val integrationPoint = MainIntegrationPoint()
val backEvents: Channel<Unit> = Channel()

fun MainViewController() = ComposeUIViewController(configure = {
    delegate = CustomDelegate()
}) {
    GesturesFunTheme {
        IosNodeHost(
            modifier = Modifier,
            onBackPressedEvents = backEvents.receiveAsFlow(),
            integrationPoint = integrationPoint
        ) {
            RootNode(
                nodeContext = it
            )
        }
    }
}

@Composable
private fun BackButton(coroutineScope: CoroutineScope) {
    IconButton(
        onClick = {
            coroutineScope.launch {
                backEvents.send(Unit)
            }
        },
        modifier = Modifier.zIndex(99f)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            tint = Color.White,
            contentDescription = "Go Back"
        )
    }
}

class MainIntegrationPoint : IntegrationPoint() {
    private lateinit var viewController: UIViewController

    override val isChangingConfigurations: Boolean
        get() = false

    fun setViewController(viewController: UIViewController) {
        this.viewController = viewController
    }

    override fun onRootFinished() {
        viewController.dismissModalViewControllerAnimated(false)
    }

    override fun handleUpNavigation() {
        viewController.navigationController?.popViewControllerAnimated(false)
    }
}

private class CustomDelegate() : ComposeUIViewControllerDelegate {
    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
        LifecycleOwner.onLifecycleEvent(LifecycleEvent.onResume)
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        LifecycleOwner.onLifecycleEvent(LifecycleEvent.onCreate)
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        LifecycleOwner.onLifecycleEvent(LifecycleEvent.onPause)
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        LifecycleOwner.onLifecycleEvent(LifecycleEvent.onStart)
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)
        LifecycleOwner.onLifecycleEvent(LifecycleEvent.onDestroy)
    }
}