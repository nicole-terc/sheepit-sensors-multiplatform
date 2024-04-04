package util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

enum class LifecycleEvent {
    onCreate, onStart, onResume, onPause, onStop, onDestroy, onAny
}

object LifecycleOwner {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val _lifecycleEvents = MutableSharedFlow<LifecycleEvent>()
    val lifecycleEvents = _lifecycleEvents.asSharedFlow()

    fun onLifecycleEvent(event: LifecycleEvent) {
        coroutineScope.launch {
            _lifecycleEvents.emit(event)
        }
    }
}

@Composable
fun observeLifecycle(
    key: Any? = Unit,
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {},
    onAny: () -> Unit = {},
) {
    LaunchedEffect(key) {
        LifecycleOwner.lifecycleEvents.collect {
            when (it) {
                LifecycleEvent.onCreate -> onCreate()
                LifecycleEvent.onStart -> onStart()
                LifecycleEvent.onResume -> onResume()
                LifecycleEvent.onPause -> onPause()
                LifecycleEvent.onStop -> onStop()
                LifecycleEvent.onDestroy -> onDestroy()
                LifecycleEvent.onAny -> onAny()
            }
        }
    }
}

