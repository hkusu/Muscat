package io.github.hkusu.muscat.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface State
interface Action
interface Event

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class Store<S : State, A : Action, E : Event>(
    private val initialState: S,
    private val processInitialStateEnter: Boolean = true,
    private val latestState: suspend (S) -> Unit = {},
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
) {
    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state: StateFlow<S> by lazy {
        init()
        _state
    }

    private val _event: MutableSharedFlow<E> = MutableSharedFlow()
    val event: Flow<E> = _event

    val currentState: S get() = _state.value

    protected open val middlewares: List<Middleware<S, A, E>> = emptyList()

    private val mutex = Mutex()

    fun dispatch(action: A) {
        state // initialize if need
        coroutineScope.launch {
            mutex.withLock {
                onActionDispatched(_state.value, action)
            }
        }
    }

    fun collectState(state: (S) -> Unit) {
        coroutineScope.launch {
            this@Store.state.collect { state(it) }
        }
    }

    fun collectEvent(event: (E) -> Unit) {
        coroutineScope.launch {
            this@Store.event.collect { event(it) }
        }
    }

    protected open suspend fun onEnter(state: S, emit: Emit<E>): S = state

    protected open suspend fun onExit(state: S, emit: Emit<E>) {}

    protected open suspend fun onDispatch(state: S, action: A, emit: Emit<E>): S = state

    protected open suspend fun onError(state: S, error: Throwable, emit: Emit<E>): S = state

    protected fun dispose() {
        coroutineScope.cancel()
    }

    private fun init() {
        coroutineScope.launch {
            mutex.withLock {
                middlewares.forEach {
                    it.onInit(this@Store, coroutineScope)
                }
                if (processInitialStateEnter) {
                    onStateEntered(initialState)
                }
            }
        }
    }

    private suspend fun onActionDispatched(state: S, action: A) {
        try {
            val nextState = processActonDispatch(state, action)

            if (state::class != nextState::class) {
                processStateExit(state)
            }

            if (state != nextState) {
                processStateChange(state, nextState)
            }

            if (state::class != nextState::class) {
                onStateEntered(nextState)
            }
        } catch (t: Throwable) {
            onErrorOccurred(currentState, t)
        }
    }

    private suspend fun onStateEntered(state: S, inErrorHandling: Boolean = false) {
        try {
            val nextState = processStateEnter(state)

            if (state::class != nextState::class) {
                processStateExit(state)
            }

            if (state != nextState) {
                processStateChange(state, nextState)
            }

            if (state::class != nextState::class) {
                onStateEntered(nextState, inErrorHandling = inErrorHandling)
            }
        } catch (t: Throwable) {
            if (!inErrorHandling) {
                onErrorOccurred(currentState, t)
            } else {
                printNote(t)
            }
        }
    }

    private suspend fun onErrorOccurred(state: S, throwable: Throwable) {
        try {
            val nextState = processError(state, throwable)

            if (state::class != nextState::class) {
                processStateExit(state)
            }

            if (state != nextState) {
                processStateChange(state, nextState)
            }

            if (state::class != nextState::class) {
                onStateEntered(nextState, inErrorHandling = true)
            }
        } catch (t: Throwable) {
            printNote(t)
        }
    }

    private suspend fun processActonDispatch(state: S, action: A): S {
        middlewares.forEach {
            it.beforeActionDispatch(state, action)
        }
        val nextState = onDispatch(state, action, ::emit)

        middlewares.forEach {
            it.afterActionDispatch(state, action, nextState)
        }
        return nextState
    }

    private suspend fun processEventEmit(state: S, event: E) {
        middlewares.forEach {
            it.beforeEventEmit(state, event)
        }
        _event.emit(event)
        middlewares.forEach {
            it.afterEventEmit(state, event)
        }
    }

    private suspend fun processStateEnter(state: S): S {
        middlewares.forEach {
            it.beforeStateEnter(state)
        }
        val nextState = onEnter(state, ::emit)
        middlewares.forEach {
            it.afterStateEnter(state, nextState)
        }
        return nextState
    }

    private suspend fun processStateExit(state: S) {
        middlewares.forEach {
            it.beforeStateExit(state)
        }
        onExit(state, ::emit)
        middlewares.forEach {
            it.afterStateExit(state)
        }
    }

    private suspend fun processStateChange(state: S, nextState: S) {
        middlewares.forEach {
            it.beforeStateChange(state, nextState)
        }
        _state.update { nextState }
        latestState(nextState)
        middlewares.forEach {
            it.afterStateChange(nextState, state)
        }
    }

    private suspend fun processError(state: S, throwable: Throwable): S {
        middlewares.forEach {
            it.beforeError(state, throwable)
        }
        val nextState = onError(state, throwable, ::emit)
        middlewares.forEach {
            it.afterError(state, nextState, throwable)
        }
        return nextState
    }

    private fun printNote(throwable: Throwable) {
        println("[Tart] An error occurred during error handling. $throwable")
    }

    private suspend fun emit(event: E) {
        processEventEmit(currentState, event)
    }

    protected fun interface Emit<E> {
        suspend operator fun invoke(event: E)
    }
}
