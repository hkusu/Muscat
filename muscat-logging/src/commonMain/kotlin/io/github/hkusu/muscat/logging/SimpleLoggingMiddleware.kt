package io.github.hkusu.muscat.logging

import io.github.hkusu.muscat.core.Action
import io.github.hkusu.muscat.core.Event
import io.github.hkusu.muscat.core.Middleware
import io.github.hkusu.muscat.core.State

@Suppress("unused")
open class SimpleLoggingMiddleware<S : State, A : Action, E : Event>(
    protected open val logger: Logger = DefaultLogger(),
    protected open val tag: String = "Tart",
    protected open val level: Logger.Level = Logger.Level.Debug,
) : Middleware<S, A, E> {
    override suspend fun runAfterActionDispatch(state: S, action: A, nextState: S) {
        logger.log(level = level, tag = tag) { "Action: $action" }
    }

    override suspend fun runAfterEventEmit(state: S, event: E) {
        logger.log(level = level, tag = tag) { "Event: $event" }
    }

    override suspend fun runAfterStateChange(state: S, prevState: S) {
        logger.log(level = level, tag = tag) { "State: $prevState -> $state" }
    }

    override suspend fun runAfterError(state: S, nextState: S, error: Throwable) {
        logger.log(level = level, tag = tag) { "Error: $error" }
    }
}
