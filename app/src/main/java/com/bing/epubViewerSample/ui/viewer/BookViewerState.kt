package com.bing.epubViewerSample.ui.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.bing.epubViewerSample.ui.common.UIEvent
import com.bing.epubViewerSample.ui.common.UIEventHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector

@Composable
fun rememberSaveableBookViewerState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    stateData: BookViewerStateData = rememberSaveable(saver = BookViewerStateData.Saver) {
        BookViewerStateData()
    }
) = remember(coroutineScope, stateData) {
    BookViewerState(coroutineScope, stateData)
}

/**
 * state for UI Composable in [BookViewerScreen]
 */
@Stable
class BookViewerState(
    coroutineScope: CoroutineScope,
    val stateData: BookViewerStateData,
) {
    private val eventHandler = UIEventHandler<BookViewerEvent>(coroutineScope)
    val events: List<BookViewerEvent> by eventHandler.eventState
    val onEventConsume: FlowCollector<BookViewerEvent> = eventHandler.onEventConsumed

    fun <T> onBookIndexClick(index: T) {
        eventHandler.scheduleEvent(BookViewerEvent.JumpToIndex(index))
        stateData.isTOCVisible = false
    }

    fun onSeekBarProgressChangeFinish(changedIndex: Int) {
        eventHandler.scheduleEvent(BookViewerEvent.JumpToPage(changedIndex))
    }


    fun updateTocVisible(visible: Boolean) {
        stateData.isTOCVisible = visible
    }

    fun updateShowTopContent(visible: Boolean) {
        stateData.isShowTopContent = visible
    }

    fun onPageInfoChanged(currentIndex: Int, totalPage: Int) {
        stateData.currentIndex = currentIndex
        stateData.totalPage = totalPage
    }
}

@Stable
class BookViewerStateData {
    var isShowTopContent: Boolean by mutableStateOf(false)
    var isTOCVisible by mutableStateOf(false)
    var currentIndex by mutableStateOf(0)
    var totalPage by mutableStateOf(0)

    companion object {
        val Saver = run {
            val isShowTopContentKey = "isShowTopContent"
            val isTOCVisibleKey = "isTOCVisible"
            val currentIndexKey = "currentIndex"
            val totalPageKey = "totalPage"
            mapSaver(
                save = {
                    mapOf(
                        isShowTopContentKey to it.isShowTopContent,
                        isTOCVisibleKey to it.isTOCVisible,
                        currentIndexKey to it.currentIndex,
                        totalPageKey to it.totalPage
                    )
                },
                restore = {
                    BookViewerStateData().apply {
                        isShowTopContent = it[isShowTopContentKey] as Boolean? ?: false
                        isTOCVisible = it[isTOCVisibleKey] as Boolean? ?: false
                        currentIndex = it[currentIndexKey] as Int? ?: 0
                        totalPage = it[totalPageKey] as Int? ?: 0
                    }
                }
            )
        }
    }
}

@Immutable
sealed class BookViewerEvent : UIEvent() {
    data class JumpToIndex<T>(val index: T) : BookViewerEvent()
    data class JumpToPage(val page: Int) : BookViewerEvent()
}
