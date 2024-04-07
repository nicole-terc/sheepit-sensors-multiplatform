package dev.nstv.gesturesfun

import GesturesFunTheme
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.bumble.appyx.navigation.integration.NodeActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import ui.RootNode
import util.LifecycleEvent

class MainActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GesturesFunTheme {
                NodeHost(
                    lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                    integrationPoint = appyxV2IntegrationPoint
                ) {
                    RootNode(nodeContext = it)
                }
            }
        }
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> util.LifecycleOwner.onLifecycleEvent(LifecycleEvent.onCreate)
                    Lifecycle.Event.ON_START -> util.LifecycleOwner.onLifecycleEvent(LifecycleEvent.onStart)
                    Lifecycle.Event.ON_RESUME -> util.LifecycleOwner.onLifecycleEvent(LifecycleEvent.onResume)
                    Lifecycle.Event.ON_PAUSE -> util.LifecycleOwner.onLifecycleEvent(LifecycleEvent.onPause)
                    Lifecycle.Event.ON_STOP -> util.LifecycleOwner.onLifecycleEvent(LifecycleEvent.onStop)
                    Lifecycle.Event.ON_DESTROY -> util.LifecycleOwner.onLifecycleEvent(
                        LifecycleEvent.onDestroy
                    )
                    Lifecycle.Event.ON_ANY -> util.LifecycleOwner.onLifecycleEvent(LifecycleEvent.onAny)
                }
            }
        })
    }
}