package io.github.hkusu.muscat.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
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

    @Suppress("unused")
    companion object {
        fun <S : State, A : Action, E : Event> create(
            initialState: S,
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        ): Store<S, A, E> {
            return DefaultStore(
                initialState = initialState,
                coroutineScope = coroutineScope,
            )
        }

        fun <S : State, A : Action, E : Event> createMock(
            initialState: S,
        ): Store<S, A, E> {
            return object : Store<S, A, E> {
                override val state: StateFlow<S> = MutableStateFlow(initialState)
                override val event: Flow<E> = emptyFlow()
                override val currentState: S = initialState
                override val middlewares: List<Middleware<S, A, E>> = listOf()
                override fun dispatch(action: A) {}
                override fun collect(onState: OnState<S>, onEvent: OnEvent<E>): Job = EmptyCoroutineContext.job
                override suspend fun onEnter(state: S, emit: EventEmit<E>): S = state
                override suspend fun onExit(state: S, emit: EventEmit<E>) {}
                override suspend fun onDispatch(state: S, action: A, emit: EventEmit<E>): S = state
                override suspend fun onError(state: S, error: Throwable, emit: EventEmit<E>): S = state
                override fun dispose() {}
            }
        }
    }
}

fun interface EventEmit<E> {
    suspend operator fun invoke(event: E)
}
