package io.github.hkusu.muscat.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

interface Store<S : State, A : Action, E : Event> {
    val state: StateFlow<S>

    val event: Flow<E>

    val currentState: S

    fun dispatch(action: A)

    fun collectState(state: (S) -> Unit)

    fun collectEvent(event: (E) -> Unit)

    @Suppress("unused")
    companion object {
        fun <S : State, A : Action, E : Event> createMock(
            initialState: S,
        ): Store<S, A, E> {
            return object : Store<S, A, E> {
                override val state: StateFlow<S> = MutableStateFlow(initialState)
                override val event: Flow<E> = emptyFlow()
                override val currentState: S = initialState
                override fun dispatch(action: A) {}
                override fun collectState(state: (S) -> Unit) {}
                override fun collectEvent(event: (E) -> Unit) {}
            }
        }
    }
}
