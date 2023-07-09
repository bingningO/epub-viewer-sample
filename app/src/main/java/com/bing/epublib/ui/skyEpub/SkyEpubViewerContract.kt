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
        val isInitLoading: Boolean
        val isAnalysisLoading: Boolean
        val error: Throwable?
        val bookProvider: StableBookProvider?
        val bookPath: String
    }

    @Stable
    class StableBookProvider : SkyProvider()

    internal class MutableUiData() : UiData {
        override var isAnalysisLoading: Boolean by mutableStateOf(false)
        override var isInitLoading: Boolean by mutableStateOf(false)
        override var error: Throwable? by mutableStateOf(null)
        override var bookPath: String by mutableStateOf("")
        override var bookProvider: StableBookProvider? by mutableStateOf(null)
    }

    @Immutable
    sealed class SkyEpubViewerEvent : UIEvent() {

    }
}