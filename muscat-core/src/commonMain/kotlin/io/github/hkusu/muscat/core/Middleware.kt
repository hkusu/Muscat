package io.github.hkusu.muscat.core

import kotlinx.coroutines.CoroutineScope

interface Middleware<S : State, A : Action, E : Event> {
    suspend fun onInit(store: Store<S, A, E>, coroutineScope: CoroutineScope) {}
    suspend fun beforeActionDispatch(state: S, action: A) {}
    suspend fun afterActionDispatch(state: S, action: A, nextState: S) {}
    suspend fun beforeEventEmit(state: S, event: E) {}
    suspend fun afterEventEmit(state: S, event: E) {}
    suspend fun beforeStateEnter(state: S) {}
    suspend fun afterStateEnter(state: S, nextState: S) {}
    suspend fun beforeStateExit(state: S) {}
    suspend fun afterStateExit(state: S) {}
    suspend fun beforeStateChange(state: S, nextState: S) {}
    suspend fun afterStateChange(state: S, prevState: S) {}
    suspend fun beforeError(state: S, error: Throwable) {}
    suspend fun afterError(state: S, nextState: S, error: Throwable) {}
}
