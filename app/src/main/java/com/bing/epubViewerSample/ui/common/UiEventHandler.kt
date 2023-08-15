package com.bing.epubViewerSample.ui.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UIEventHandler<T : UIEvent>(coroutineScope: CoroutineScope) {
    private val _eventState: MutableState<List<T>> = mutableStateOf(emptyList())
    private val _onEventConsumed: MutableSharedFlow<T> = MutableSharedFlow()
    val eventState: State<List<T>> = _eventState
    val onEventConsumed: FlowCollector<T> = _onEventConsumed

    init {
        _onEventConsumed
            .onEach { consumeEvent(it) }
            .launchIn(coroutineScope)
    }

    private fun consumeEvent(targetEvent: T) {
        _eventState.update { events -> events.filterNot { event -> event.id == targetEvent.id } }
    }

    fun scheduleEvent(event: T) {
        _eventState.update { events -> listOf(event) + events }
    }
}

private fun <T> MutableState<T>.update(function: (T) -> T) {
    var property: T by this
    property = function.invoke(property)
}
