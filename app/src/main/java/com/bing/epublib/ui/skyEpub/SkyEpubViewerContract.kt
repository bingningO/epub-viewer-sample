package com.bing.epublib.ui.skyEpub

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.bing.epublib.ui.common.UIEvent
import com.skytree.epub.SkyProvider
import kotlinx.coroutines.flow.FlowCollector

class SkyEpubViewerContract {

    @Stable
    interface ViewModel : DefaultLifecycleObserver {
        val uiState: UiState
        val uiInput: UiInput
    }

    interface UiState {
        val uiData: UiData
        val events: List<SkyEpubViewerEvent>
    }

    interface UiInput {
        val onEventConsumed: FlowCollector<SkyEpubViewerEvent>
        val onLoadingStateChanged: FlowCollector<Boolean>
        val onChangePagePosition: FlowCollector<Double>
    }

    @Stable
    interface UiData {
        val isLoading: Boolean
        val initialPositionInBook: Double
        val error: Throwable?
        val bookProvider: SkyProvider?
        val bookPath: String
        val bookCode: Int
        val isFixedLayout: Boolean
    }

    @Stable
    data class BookPagingInfo(
        val totalPage: Int,
        val totalPageInChapter: Int,
        val currentIndexInChapter: Int,
        val currentIndexInBook: Int,
        val currentPositionInBook: Double,
        val currentChapterIndex: Int,
        val totalNumberOfChapters: Int
    )

    internal class MutableUiData : UiData {
        override var isLoading: Boolean by mutableStateOf(false)
        override var initialPositionInBook: Double by mutableStateOf(0.0)
        override var error: Throwable? by mutableStateOf(null)
        override var bookPath: String by mutableStateOf("")
        override var bookCode: Int by mutableStateOf(0)
        override var bookProvider: SkyProvider? by mutableStateOf(null)
        override var isFixedLayout: Boolean by mutableStateOf(false)
    }

    @Immutable
    sealed class SkyEpubViewerEvent : UIEvent() {

    }
}