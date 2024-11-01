package io.github.hkusu.muscat.core

import kotlinx.coroutines.CoroutineScope

abstract class Middleware<S : State, A : Action, E : Event> {
    protected lateinit var store: Store<S, A, E>
    protected lateinit var coroutineScope: CoroutineScope

    suspend fun initialize(store: Store<S, A, E>, coroutineScope: CoroutineScope) {
        this.store = store
        this.coroutineScope = coroutineScope
        onInit()
    }

    protected open suspend fun onInit() {}

    open suspend fun runBeforeActionDispatch(state: S, action: A) {}
    open suspend fun runAfterActionDispatch(state: S, action: A, nextState: S) {}
    open suspend fun runBeforeEventEmit(state: S, event: E) {}
    open suspend fun runAfterEventEmit(state: S, event: E) {}
    open suspend fun runBeforeStateEnter(state: S) {}
    open suspend fun runAfterStateEnter(state: S, nextState: S) {}
    open suspend fun runBeforeStateExit(state: S) {}
    open suspend fun runAfterStateExit(state: S) {}
    open suspend fun runBeforeStateChange(state: S, nextState: S) {}
    open suspend fun runAfterStateChange(state: S, prevState: S) {}
    open suspend fun runBeforeError(state: S, error: Throwable) {}
    open suspend fun runAfterError(state: S, nextState: S, error: Throwable) {}
}
