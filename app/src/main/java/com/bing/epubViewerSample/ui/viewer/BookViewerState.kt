package com.bing.epubViewerSample.ui.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bing.epubViewerSample.ui.common.UIEvent
import com.bing.epubViewerSample.ui.common.UIEventHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector

/**
 * state for UI Composable in [BookViewerScreen]
 */
@Stable
class BookViewerState(
    private val eventHandler: UIEventHandler<BookViewerEvent>
) {
    val events: List<BookViewerEvent> by eventHandler.eventState
    val onEventConsume: FlowCollector<BookViewerEvent> = eventHandler.onEventConsumed
    var isShowTopContent by mutableStateOf(false)
        private set
    var isTOCVisible by mutableStateOf(false)
        private set

    var currentIndex by mutableStateOf(0)
        private set
    var totalPage by mutableStateOf(0)
        private set

    fun <T> onBookIndexClick(index: T) {
        eventHandler.scheduleEvent(BookViewerEvent.JumpToIndex(index))
        isTOCVisible = false
    }

    fun onSeekBarProgressChangeFinish(changedIndex: Int) {
        eventHandler.scheduleEvent(BookViewerEvent.JumpToPage(changedIndex))
    }


    fun updateTocVisible(visible: Boolean) {
        isTOCVisible = visible
    }

    fun updateShowTopContent(visible: Boolean) {
        isShowTopContent = visible
    }

    fun onPageInfoChanged(currentIndex: Int, totalPage: Int) {
        this.currentIndex = currentIndex
        this.totalPage = totalPage
    }
}

@Composable
// todo add [Saver] for all state
fun rememberBookViewerState(
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(coroutineScope) {
    BookViewerState(eventHandler = UIEventHandler(coroutineScope))
}

@Immutable
sealed class BookViewerEvent : UIEvent() {
    data class JumpToIndex<T>(val index: T) : BookViewerEvent()
    data class JumpToPage(val page: Int) : BookViewerEvent()
}
