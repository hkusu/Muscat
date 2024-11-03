package io.github.hkusu.muscat.message

import io.github.hkusu.muscat.core.Action
import io.github.hkusu.muscat.core.Event
import io.github.hkusu.muscat.core.Middleware
import io.github.hkusu.muscat.core.State
import io.github.hkusu.muscat.core.Store
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

interface Message

internal object MessageHub {
    private val _messages = MutableSharedFlow<Message>()
    val messages: Flow<Message> get() = _messages

    suspend fun send(message: Message) {
        _messages.emit(message)
    }
}

internal val defaultExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, _ -> }

@Suppress("unused")
abstract class MessageSendMiddleware<S : State, A : Action, E : Event> : Middleware<S, A, E> {
    private lateinit var store: Store<S, A, E>
    private lateinit var coroutineScope: CoroutineScope

    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception -> onError(exception) }

    final override suspend fun onInit(store: Store<S, A, E>, coroutineScope: CoroutineScope) {
        this.store = store
        this.coroutineScope = coroutineScope
    }

    final override suspend fun afterEventEmit(state: S, event: E) {
        coroutineScope.launch(exceptionHandler) {
            onEvent(event, this@MessageSendMiddleware::send, store, coroutineScope)
        }
    }

    protected abstract suspend fun onEvent(event: E, send: SendFun, store: Store<S, A, E>, coroutineScope: CoroutineScope)

    protected open fun onError(error: Throwable) {
        throw error
    }

    private suspend fun send(message: Message) {
        MessageHub.send(message)
    }

    protected fun interface SendFun {
        suspend operator fun invoke(message: Message)
    }

    final override suspend fun beforeActionDispatch(state: S, action: A) {}
    final override suspend fun afterActionDispatch(state: S, action: A, nextState: S) {}
    final override suspend fun beforeEventEmit(state: S, event: E) {}
    final override suspend fun beforeStateEnter(state: S) {}
    final override suspend fun afterStateEnter(state: S, nextState: S) {}
    final override suspend fun beforeStateExit(state: S) {}
    final override suspend fun afterStateExit(state: S) {}
    final override suspend fun beforeStateChange(state: S, nextState: S) {}
    final override suspend fun afterStateChange(state: S, prevState: S) {}
    final override suspend fun beforeError(state: S, error: Throwable) {}
    final override suspend fun afterError(state: S, nextState: S, error: Throwable) {}
}

@Suppress("unused")
abstract class MessageReceiveMiddleware<S : State, A : Action, E : Event> : Middleware<S, A, E> {
    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception -> onError(exception) }

    final override suspend fun onInit(store: Store<S, A, E>, coroutineScope: CoroutineScope) {
        coroutineScope.launch(exceptionHandler) {
            MessageHub.messages.collect {
                receive(it, store, coroutineScope)
            }
        }
    }

    protected abstract suspend fun receive(message: Message, store: Store<S, A, E>, coroutineScope: CoroutineScope)

    protected open fun onError(error: Throwable) {
        throw error
    }

    final override suspend fun beforeActionDispatch(state: S, action: A) {}
    final override suspend fun afterActionDispatch(state: S, action: A, nextState: S) {}
    final override suspend fun beforeEventEmit(state: S, event: E) {}
    final override suspend fun afterEventEmit(state: S, event: E) {}
    final override suspend fun beforeStateEnter(state: S) {}
    final override suspend fun afterStateEnter(state: S, nextState: S) {}
    final override suspend fun beforeStateExit(state: S) {}
    final override suspend fun afterStateExit(state: S) {}
    final override suspend fun beforeStateChange(state: S, nextState: S) {}
    final override suspend fun afterStateChange(state: S, prevState: S) {}
    final override suspend fun beforeError(state: S, error: Throwable) {}
    final override suspend fun afterError(state: S, nextState: S, error: Throwable) {}
}
