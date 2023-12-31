package com.bing.epubViewerSample.ui.viewer

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.bing.epubViewerSample.ui.common.UIEvent
import com.skytree.epub.NavPoint
import com.skytree.epub.SkyProvider
import kotlinx.coroutines.flow.FlowCollector

class BookViewerContract {

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
        val onEventConsume: FlowCollector<SkyEpubViewerEvent>
        val onLoadingStateChange: FlowCollector<Boolean>
        val onChangePagePosition: FlowCollector<Double>
        val onClickFontSizeBigger: FlowCollector<Unit>
        val onClickFontSizeSmaller: FlowCollector<Unit>
        val onIndexDataLoad: FlowCollector<List<NavPoint>>
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
        val realFontSize: Int
        val fontSizeIndex: Int
        val indexList: List<ViewerIndexData<NavPoint>>
    }

    @Immutable
    data class ViewerIndexData<T>(
        val indexTitle: String,
        val pageData: T,
        /**
         *  level of this index, 0 is the first-level
         */
        val nestLevel: Int,
    )

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
        override var realFontSize: Int by mutableStateOf(0)
        override var fontSizeIndex: Int by mutableStateOf(0)
        override var indexList: List<ViewerIndexData<NavPoint>> by mutableStateOf(emptyList())
    }

    @Immutable
    sealed class SkyEpubViewerEvent : UIEvent() {
        data class ShowToast(val message: String) : SkyEpubViewerEvent()
    }
}
