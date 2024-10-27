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
class ComposeStore<S : State, A : Action, E : Event>(
    val state: S,
    val dispatch: (A) -> Unit = {},
    val event: Flow<E> = flowOf(),
) {
    @Composable
    inline fun <reified S2 : S> render(block: S2.() -> Unit) {
        if (state is S2) {
            block(state)
        }
    }

    @Composable
    inline fun <reified E2 : E> handle(crossinline block: E2.() -> Unit) {
        LaunchedEffect(Unit) {
            event.filter { it is E2 }.collect {
                block(it as E2)
            }
        }
    }
}

@Suppress("unused")
@Composable
fun <S : State, A : Action, E : Event> Store<S, A, E>.composeStore(): ComposeStore<S, A, E> {
    val state by state.collectAsState()
    return ComposeStore(state = state, dispatch = ::dispatch, event = event)
}
