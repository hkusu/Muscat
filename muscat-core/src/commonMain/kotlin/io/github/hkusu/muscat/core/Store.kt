package io.github.hkusu.muscat.core

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Store<S : State, A : Action, E : Event> {

    val state: StateFlow<S>

    val event: Flow<E>

    val currentState: S

    val middlewares: List<Middleware<S, A, E>>

    fun dispatch(action: A)

    fun collect(onState: OnState<S>, onEvent: OnEvent<E>): Job

    suspend fun onEnter(state: S, emit: EventEmit<E>): S

    suspend fun onExit(state: S, emit: EventEmit<E>)

    suspend fun onDispatch(state: S, action: A, emit: EventEmit<E>): S

    suspend fun onError(state: S, error: Throwable, emit: EventEmit<E>): S

    fun dispose()

    fun interface OnState<S> {
        suspend operator fun invoke(event: S)
    }

    fun interface OnEvent<E> {
        suspend operator fun invoke(event: E)
    }

    companion object
}

fun interface EventEmit<E> {
    suspend operator fun invoke(event: E)
}
