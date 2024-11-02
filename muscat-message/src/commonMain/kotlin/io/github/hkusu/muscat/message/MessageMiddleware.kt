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
class MessageSendMiddleware<S : State, A : Action, E : Event>(
    private val send: suspend S.(event: E, send: suspend (Message) -> Unit) -> Unit,
) : Middleware<S, A, E>() {
    override suspend fun afterEventEmit(state: S, event: E) {
        coroutineScope.launch {
            send(store.currentState, event, this@MessageSendMiddleware::send)
        }
    }

    private suspend fun send(message: Message) {
        MessageHub.send(message)
    }
}

@Suppress("unused")
class MessageReceiveMiddleware<S : State, A : Action, E : Event>(
    private val receive: suspend S.(message: Message, dispatch: (A) -> Unit) -> Unit,
) : Middleware<S, A, E>() {
    override suspend fun onInit() {
        coroutineScope.launch {
            MessageHub.messages.collect {
                receive(store.currentState, it, store::dispatch)
            }
        }
    }
}
