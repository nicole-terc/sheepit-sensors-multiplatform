import androidx.compose.ui.uikit.ComposeUIViewControllerDelegate
import androidx.compose.ui.window.ComposeUIViewController
import ui.App
import util.LifecycleEvent
import util.LifecycleOwner.onLifecycleEvent

fun MainViewController() = ComposeUIViewController(configure = {
    delegate = CustomDelegate()
}) { App() }

private class CustomDelegate() : ComposeUIViewControllerDelegate {
    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
        onLifecycleEvent(LifecycleEvent.onResume)
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        onLifecycleEvent(LifecycleEvent.onCreate)
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        onLifecycleEvent(LifecycleEvent.onPause)
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        onLifecycleEvent(LifecycleEvent.onStart)
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)
        onLifecycleEvent(LifecycleEvent.onDestroy)
    }
}