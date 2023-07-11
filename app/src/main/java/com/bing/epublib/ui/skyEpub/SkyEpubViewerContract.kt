package com.bing.epublib.ui.skyEpub

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bing.epublib.ui.common.UIEvent
import com.skytree.epub.SkyProvider
import kotlinx.coroutines.flow.FlowCollector

class SkyEpubViewerContract {

    interface ViewModel {
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
    }

    @Stable
    interface UiData {
        val isLoading: Boolean
        val error: Throwable?
        val bookProvider: StableBookProvider?
        val bookPath: String
        val bookMetaData: BookMetaData?
    }

    @Stable
    data class BookMetaData(
        val title: String,
        val isFixedLayout: Boolean,
        val description: String,
        val orientation: Int
    )

    @Stable
    data class BookPagingInfo(
        // todo for SDK problem could not get the correct total page
        val totalPage: Int = 0,
        val totalPageInChapter: Int,
        val currentIndexInChapter: Int,
        val currentIndexInBook: Int,
        val currentChapterIndex: Int,
        val totalNumberOfChapters: Int
    )

    @Stable
    class StableBookProvider : SkyProvider()

    internal class MutableUiData() : UiData {
        override var isLoading: Boolean by mutableStateOf(false)
        override var error: Throwable? by mutableStateOf(null)
        override var bookPath: String by mutableStateOf("")
        override var bookProvider: StableBookProvider? by mutableStateOf(null)
        override var bookMetaData: BookMetaData? by mutableStateOf(null)
    }

    @Immutable
    sealed class SkyEpubViewerEvent : UIEvent() {

    }
}