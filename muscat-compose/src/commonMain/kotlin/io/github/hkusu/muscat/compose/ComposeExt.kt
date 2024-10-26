package io.github.hkusu.muscat.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.hkusu.muscat.core.Action
import io.github.hkusu.muscat.core.Event
import io.github.hkusu.muscat.core.State
import io.github.hkusu.muscat.core.Store
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf

@Suppress("unused")
@Composable
inline fun <reified S : State> Handle<*, *, *>.render(block: S.() -> Unit) {
    if (state is S) {
        block(state)
    }
}

@Suppress("unused")
@Composable
inline fun <reified E : Event> Handle<*, *, *>.on(crossinline block: E.() -> Unit) {
    LaunchedEffect(Unit) {
        event.filter { it is E }.collect {
            block(it as E)
        }
    }
}

data class Handle<S : State, A : Action, E : Event>(
    val state: S,
    val dispatch: (A) -> Unit = {},
    val event: Flow<E> = flowOf(),
)

@Suppress("unused")
@Composable
fun <S : State, A : Action, E : Event> handle(
    store: Store<S, A, E>,
): Handle<S, A, E> {
    val state by store.state.collectAsState()
    return Handle(state = state, dispatch = store::dispatch, event = store.event)
}
