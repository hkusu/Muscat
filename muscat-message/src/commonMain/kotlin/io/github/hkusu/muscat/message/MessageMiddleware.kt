package io.github.hkusu.muscat.message

import io.github.hkusu.muscat.core.Action
import io.github.hkusu.muscat.core.Event
import io.github.hkusu.muscat.core.Middleware
import io.github.hkusu.muscat.core.State

interface Message

internal object MessageHub {
    private val receives: MutableList<suspend (Message) -> Unit> = mutableListOf()

    fun addReceive(receive: suspend (Message) -> Unit) {
        receives.add(receive)
    }

    suspend fun send(message: Message) {
        receives.forEach { it(message) }
    }
}

@Suppress("unused")
class MessageSendMiddleware<S : State, A : Action, E : Event>(
    private val send: suspend (E) -> Message,
) : Middleware<S, A, E> {
    override suspend fun runAfterEventEmit(state: S, event: E) {
        MessageHub.send(send(event))
    }
}

@Suppress("unused")
class MessageReceiveMiddleware<S : State, A : Action, E : Event>(
    receive: suspend (Message) -> Unit,
) : Middleware<S, A, E> {
    init {
        MessageHub.addReceive(receive)
    }
}
