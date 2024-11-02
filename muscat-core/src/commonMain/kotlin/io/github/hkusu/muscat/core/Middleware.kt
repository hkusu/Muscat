package io.github.hkusu.muscat.core

import kotlinx.coroutines.CoroutineScope

abstract class Middleware<S : State, A : Action, E : Event> {
    protected lateinit var store: Store<S, A, E>
    protected lateinit var coroutineScope: CoroutineScope

    suspend fun init(store: Store<S, A, E>, coroutineScope: CoroutineScope) {
        this.store = store
        this.coroutineScope = coroutineScope
        onInit()
    }

    protected open suspend fun onInit() {}

    open suspend fun beforeActionDispatch(state: S, action: A) {}
    open suspend fun afterActionDispatch(state: S, action: A, nextState: S) {}
    open suspend fun beforeEventEmit(state: S, event: E) {}
    open suspend fun afterEventEmit(state: S, event: E) {}
    open suspend fun beforeStateEnter(state: S) {}
    open suspend fun afterStateEnter(state: S, nextState: S) {}
    open suspend fun beforeStateExit(state: S) {}
    open suspend fun afterStateExit(state: S) {}
    open suspend fun beforeStateChange(state: S, nextState: S) {}
    open suspend fun afterStateChange(state: S, prevState: S) {}
    open suspend fun beforeError(state: S, error: Throwable) {}
    open suspend fun afterError(state: S, nextState: S, error: Throwable) {}
}
