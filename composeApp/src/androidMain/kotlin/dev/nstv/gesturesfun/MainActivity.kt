package dev.nstv.gesturesfun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ui.App
import util.LifecycleEvent
import util.LifecycleOwner.onLifecycleEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> onLifecycleEvent(LifecycleEvent.onCreate)
                    Lifecycle.Event.ON_START -> onLifecycleEvent(LifecycleEvent.onStart)
                    Lifecycle.Event.ON_RESUME -> onLifecycleEvent(LifecycleEvent.onResume)
                    Lifecycle.Event.ON_PAUSE -> onLifecycleEvent(LifecycleEvent.onPause)
                    Lifecycle.Event.ON_STOP -> onLifecycleEvent(LifecycleEvent.onStop)
                    Lifecycle.Event.ON_DESTROY -> onLifecycleEvent(LifecycleEvent.onDestroy)
                    Lifecycle.Event.ON_ANY -> onLifecycleEvent(LifecycleEvent.onAny)
                }
            }
        })
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}