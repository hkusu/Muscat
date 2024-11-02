package io.github.hkusu.muscat.message

import io.github.hkusu.muscat.core.Action
import io.github.hkusu.muscat.core.Event
import io.github.hkusu.muscat.core.Middleware
import io.github.hkusu.muscat.core.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

interface Message

internal object MessageHub {
    private val internalMessages = MutableSharedFlow<Message>()
    val messages: Flow<Message> get() = internalMessages

    suspend fun send(message: Message) {
        internalMessages.emit(message)
    }
}

@Suppress("unused")
abstract class MessageSendMiddleware<S : State, A : Action, E : Event> : Middleware<S, A, E>() {
    override suspend fun afterEventEmit(state: S, event: E) {
        coroutineScope.launch {
            onEvent(event, this@MessageSendMiddleware::send)
        }
    }

    protected abstract suspend fun onEvent(event: E, send: Send)

    private suspend fun send(message: Message) {
        MessageHub.send(message)
    }

    protected fun interface Send {
        suspend operator fun invoke(message: Message)
    }
}

@Suppress("unused")
abstract class MessageReceiveMiddleware<S : State, A : Action, E : Event>(
) : Middleware<S, A, E>() {
    override suspend fun onInit() {
        coroutineScope.launch {
            MessageHub.messages.collect {
                receive(it)
            }
        }
    }

    protected abstract suspend fun receive(message: Message)
}
