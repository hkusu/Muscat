package io.github.hkusu.muscat.message

import io.github.hkusu.muscat.core.Action
import io.github.hkusu.muscat.core.Event
import io.github.hkusu.muscat.core.Middleware
import io.github.hkusu.muscat.core.State
import kotlinx.coroutines.CoroutineScope
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
    private val coroutineScope: CoroutineScope,
    private val send: suspend (E) -> Message,
) : Middleware<S, A, E> {
    override suspend fun runAfterEventEmit(state: S, event: E) {
        coroutineScope.launch {
            MessageHub.send(send(event))
        }
    }
}

@Suppress("unused")
class MessageReceiveMiddleware<S : State, A : Action, E : Event>(
    coroutineScope: CoroutineScope,
    receive: suspend (Message) -> Unit,
) : Middleware<S, A, E> {
    init {
        coroutineScope.launch {
            MessageHub.messages.collect {
                receive(it)
            }
        }
    }
}
