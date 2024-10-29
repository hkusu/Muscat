package io.github.hkusu.muscat.core

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.job
import kotlin.coroutines.EmptyCoroutineContext

interface Store<S : State, A : Action, E : Event> {
    val state: StateFlow<S>

    val event: Flow<E>

    val currentState: S

    fun dispatch(action: A)

    fun collect(onState: OnState<S>, onEvent: OnEvent<E>): Job

    fun interface OnState<S> {
        suspend operator fun invoke(event: S)
    }

    fun interface OnEvent<E> {
        suspend operator fun invoke(event: E)
    }

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
                override fun collect(onState: OnState<S>, onEvent: OnEvent<E>): Job = EmptyCoroutineContext.job
            }
        }
    }
}
