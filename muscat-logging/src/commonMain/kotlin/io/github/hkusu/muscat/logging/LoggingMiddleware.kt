package io.github.hkusu.muscat.logging

import io.github.hkusu.muscat.core.Action
import io.github.hkusu.muscat.core.Event
import io.github.hkusu.muscat.core.Middleware
import io.github.hkusu.muscat.core.State

@Suppress("unused")
open class LoggingMiddleware<S : State, A : Action, E : Event>(
    protected val logger: Logger = DefaultLogger(),
    protected val tag: String = "Tart",
    protected val level: Logger.Level = Logger.Level.Debug,
) : Middleware<S, A, E>() {
    override suspend fun afterActionDispatch(state: S, action: A, nextState: S) {
        logger.log(level = level, tag = tag) { "Action: $action" }
    }

    override suspend fun afterEventEmit(state: S, event: E) {
        logger.log(level = level, tag = tag) { "Event: $event" }
    }

    override suspend fun afterStateChange(state: S, prevState: S) {
        logger.log(level = level, tag = tag) { "State: $prevState -> $state" }
    }

    override suspend fun afterError(state: S, nextState: S, error: Throwable) {
        logger.log(level = level, tag = tag) { "Error: $error" }
    }
}
