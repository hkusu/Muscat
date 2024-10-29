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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter

interface ComposeStore<S : State, A : Action, E : Event> {
    val state: S
    val dispatch: (A) -> Unit
    val event: Flow<E>

    companion object
}

@Suppress("unused")
@Composable
inline fun <S : State, A : Action, E : Event, reified S2 : S> ComposeStore<S, A, E>.render(block: S2.() -> Unit) {
    if (state is S2) {
        block(state as S2)
    }
}

@Suppress("unused")
@Composable
inline fun <S : State, A : Action, E : Event, reified E2 : E> ComposeStore<S, A, E>.handle(crossinline block: E2.() -> Unit) {
    LaunchedEffect(Unit) {
        event.filter { it is E2 }.collect {
            block(it as E2)
        }
    }
}

@Suppress("unused")
@Composable
fun <S : State, A : Action, E : Event> ComposeStore.Companion.create(store: Store<S, A, E>): ComposeStore<S, A, E> {
    val state by store.state.collectAsState()
    return object : ComposeStore<S, A, E> {
        override val state: S = state
        override val dispatch: (A) -> Unit = store::dispatch
        override val event: Flow<E> = store.event
    }
}

// or, ComposeStore.create(Store.createMock(<initial State>))
@Suppress("unused")
@Composable
fun <S : State, A : Action, E : Event> ComposeStore.Companion.createMock(state: S): ComposeStore<S, A, E> {
    return object : ComposeStore<S, A, E> {
        override val state: S = state
        override val dispatch: (A) -> Unit = {}
        override val event: Flow<E> = emptyFlow()
    }
}
