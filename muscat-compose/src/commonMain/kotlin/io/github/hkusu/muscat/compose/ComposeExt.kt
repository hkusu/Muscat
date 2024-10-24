package io.github.hkusu.muscat.compose

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.hkusu.muscat.core.Action
import io.github.hkusu.muscat.core.Event
import io.github.hkusu.muscat.core.State
import io.github.hkusu.muscat.core.Store
import kotlinx.coroutines.CoroutineScope

@Suppress("unused")
@Composable
inline fun <reified S : State> State.render(block: @Composable S.() -> Unit) {
    if (this is S) {
        block(this)
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <E : Event> E?.handle(block: CoroutineScope.(E) -> Unit) {
    this?.let {
        LaunchedEffect(it) {
            block(it)
        }
    }
}

@Suppress("unused")
@SuppressLint("ComposableNaming")
@Composable
fun <E : Event> Contract<*, *, E>.handleEvents(block: CoroutineScope.(E) -> Unit) {
    event?.handle(block = block)
}

data class Contract<S : State, A : Action, E : Event>(
    val state: S,
    val dispatch: (A) -> Unit = {},
    internal val event: E? = null,
)

@Suppress("unused")
@Composable
fun <S : State, A : Action, E : Event> contract(
    store: Store<S, A, E>,
): Contract<S, A, E> {
    val state by store.state.collectAsState()
    val event by store.event.collectAsState(initial = null)
    return Contract(state = state, dispatch = store::dispatch, event = event)
}
