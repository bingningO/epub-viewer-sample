package com.bing.epublib.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UIEventHandler<T : UIEvent>(coroutineScope: CoroutineScope) {
    private var _eventState: List<T> by mutableStateOf(emptyList())
    private val _onEventConsumed: MutableSharedFlow<T> = MutableSharedFlow()
    val eventState: List<T> = _eventState
    val onEventConsumed: FlowCollector<T> = _onEventConsumed

    init {
        val launchIn = _onEventConsumed
            .onEach { consumeEvent(it) }
            .launchIn(coroutineScope)
    }

    private fun consumeEvent(targetEvent: T) {
        _eventState = _eventState.filterNot { event -> event.id == targetEvent.id }
    }

    fun scheduleEvent(event: T) {
        _eventState = listOf(event) + _eventState
    }
}